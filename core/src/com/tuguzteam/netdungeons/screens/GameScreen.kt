package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.tuguzteam.netdungeons.*
import com.tuguzteam.netdungeons.field.*
import com.tuguzteam.netdungeons.field.tile.Tile
import com.tuguzteam.netdungeons.field.tile.Wall
import com.tuguzteam.netdungeons.input.MovementGestureListener
import com.tuguzteam.netdungeons.input.ObjectChooseGestureListener
import com.tuguzteam.netdungeons.input.RotationZoomGestureListener
import com.tuguzteam.netdungeons.objects.GameObject
import com.tuguzteam.netdungeons.objects.ModelObject
import com.tuguzteam.netdungeons.objects.Renderable
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ktx.async.KtxAsync
import ktx.graphics.color
import ktx.log.info
import ktx.log.logger
import ktx.math.*
import kotlin.math.absoluteValue

class GameScreen(loader: Loader, prevScreen: StageScreen) : ReturnableScreen(loader, prevScreen) {
    private companion object {
        private val logger = logger<GameScreen>()
        private val assets = Field.assets

        private const val viewDistance = 4u
        private const val maxViewDistance = 6u
    }

    val assetManager = loader.assetManager
    var playerPosition = immutableGridPoint2()
        set(value) {
            camera?.apply {
                position += vec3(
                    x = value.x.toFloat() - field.x.toFloat(),
                    z = value.y.toFloat() - field.y.toFloat(),
                )
                update(true)
            }
            field = value
        }
    private val _visibleObjects = mutableSetOf<GameObject>()
    val visibleObjects: Set<GameObject> = _visibleObjects

    private val _visitedObjects = mutableSetOf<GameObject>()

    private var camera: OrthographicCamera? = null
    private val modelBatch = ModelBatch()
    private val environmentVisible = Environment().apply {
        val ambient = 0.35f
        this with ColorAttribute.createAmbientLight(
            color(red = ambient, green = ambient, blue = ambient),
        )
        val directional = 0.4f
        this += DirectionalLight().set(
            color(red = directional, green = directional, blue = directional),
            vec3(x = 0.6f, y = 0.4f, z = 0.2f),
        )
    }
    private val environmentVisited = Environment().apply {
        val ambient = 0.25f
        this with ColorAttribute.createAmbientLight(
            color(red = ambient, green = ambient, blue = ambient),
        )
    }
    var field: Field? = null
        private set

    private lateinit var rotationZoomGestureListener: RotationZoomGestureListener
    private lateinit var objectChooseGestureListener: ObjectChooseGestureListener
    private lateinit var movementGestureListener: MovementGestureListener

    init {
        viewport = ExtendViewport(10f, 10f)
    }

    override fun show() {
        super.show()
        playerPosition = immutableGridPoint2()
        camera = OrthographicCamera().apply {
            val pos = vec3(x = 10f, z = 10f)
            val angle = 30f
            pos.y = pos.len() * MathUtils.sinDeg(angle)
            position.set(pos)
            lookAt(vec3())
            near = 1f
            far = 50f
            update(true)
        }
        viewport.apply {
            camera = this@GameScreen.camera
            update(Gdx.graphics.width, Gdx.graphics.height)
        }

        rotationZoomGestureListener = RotationZoomGestureListener(gameScreen = this)
        objectChooseGestureListener = ObjectChooseGestureListener(gameScreen = this)
        movementGestureListener = MovementGestureListener(gameScreen = this)
        inputMultiplexer.apply {
            addProcessor(GestureDetector(rotationZoomGestureListener))
            addProcessor(GestureDetector(objectChooseGestureListener))
            addProcessor(GestureDetector(movementGestureListener))
        }

        KtxAsync.launch {
            assetManager.load(assets)
            logger.info { "Asset loading finished" }
            field = Field(gameScreen = this@GameScreen).apply {
                playerPosition = immutableGridPoint2(
                    x = (size * Tile.size / 2u).toInt(),
                    y = (size * Tile.size / 2u).toInt(),
                )
            }
            updateVisibleObjects()
            logger.info { "Map generation finished" }
        }
    }

    override fun hide() {
        super.hide()
        runBlocking {
            assetManager.unload(assets)
        }
        _visibleObjects.clear()
        _visitedObjects.clear()
        field?.dispose()
        field = null
    }

    fun updateVisibleObjects() {
        field?.let { field ->
            _visibleObjects.clear()

            // Current tile is always visible
            val currentTile = field[playerPosition] ?: return
            _visibleObjects += currentTile

            // Filter visible objects (Field Of Vision by shadow casting)
            repeat(8) { octant ->
                data class Shadow(var start: Float, var end: Float) {
                    operator fun contains(shadow: Shadow) =
                        start <= shadow.start && end >= shadow.end
                }

                class ShadowLine {
                    private val shadows = mutableListOf<Shadow>()

                    val isFullShadow: Boolean
                        get() = shadows.size == 1 && shadows[0].start == 0f && shadows[0].end == 1f

                    operator fun contains(projection: Shadow): Boolean {
                        shadows.forEach { shadow ->
                            if (projection in shadow) return true
                        }
                        return false
                    }

                    operator fun plusAssign(shadow: Shadow) {
                        var index = 0
                        while (index < shadows.size) {
                            if (shadows[index].start >= shadow.start) break
                            index++
                        }
                        val overlappingPrevious =
                            if (index > 0 && shadows[index - 1].end > shadow.start)
                                shadows[index - 1]
                            else null
                        val overlappingNext =
                            if (index < shadows.size && shadows[index].start < shadow.end)
                                shadows[index]
                            else null
                        if (overlappingNext != null) {
                            if (overlappingPrevious != null) {
                                overlappingPrevious.end = overlappingNext.end
                                shadows.removeAt(index)
                            } else {
                                overlappingNext.start = shadow.start
                            }
                        } else {
                            if (overlappingPrevious != null) {
                                overlappingPrevious.end = shadow.end
                            } else {
                                shadows.add(index, shadow)
                            }
                        }
                    }
                }

                fun projectTile(row: Int, col: Int): Shadow {
                    val topLeft = col.toFloat() / (row + 2)
                    val bottomRight = (col + 1).toFloat() / (row + 1)
                    return Shadow(topLeft, bottomRight)
                }

                fun transformOctant(row: Int, col: Int, octant: UInt) = when (octant) {
                    0u -> immutableGridPoint2(col, -row)
                    1u -> immutableGridPoint2(row, -col)
                    2u -> immutableGridPoint2(row, col)
                    3u -> immutableGridPoint2(col, row)
                    4u -> immutableGridPoint2(-col, row)
                    5u -> immutableGridPoint2(-row, col)
                    6u -> immutableGridPoint2(-row, -col)
                    7u -> immutableGridPoint2(-col, -row)
                    else -> {
                        throw IllegalArgumentException("octant must be less than 8: $octant given")
                    }
                }

                val line = ShadowLine()
                var fullShadow = false
                for (row in 1 until viewDistance.toInt()) {
                    for (col in 0..row) {
                        val pos = playerPosition + transformOctant(row, col, octant.toUInt())
                        val tile = field[pos] ?: continue
                        _visibleObjects -= tile
                        if (!fullShadow) {
                            val projection = projectTile(row, col)
                            val visible = projection !in line
                            if (visible) {
                                _visibleObjects += tile
                                if (tile is Wall) {
                                    line += projection
                                    fullShadow = line.isFullShadow
                                }
                            }
                        }
                    }
                }
            }

            // Update set of visited objects
            _visitedObjects += visibleObjects
        }
    }

    override fun render(delta: Float) {
        super.render(delta)
        val camera = camera
        val field = field
        if (camera != null && field != null && assetManager.loaded(assets)) {
            rotationZoomGestureListener.update()

            modelBatch.use(camera) {
                render(
                    _visitedObjects.asSequence()
                        .filter { gameObject ->
                            when (gameObject) {
                                is Tile -> {
                                    val x = (gameObject.position.x - playerPosition.x).absoluteValue
                                    val y = (gameObject.position.y - playerPosition.y).absoluteValue
                                    x <= maxViewDistance.toInt() && y < maxViewDistance.toInt()
                                }
                                is ModelObject -> {
                                    val x = (gameObject.position.x - playerPosition.x).absoluteValue
                                    val y = (gameObject.position.z - playerPosition.y).absoluteValue
                                    x <= maxViewDistance.toInt() && y < maxViewDistance.toInt()
                                }
                                else -> false
                            }
                        }
                        .filterIsInstance<Renderable>()
                        .map(Renderable::renderableProviders).flatten()
                        .asIterable(),
                    environmentVisited,
                )
                render(
                    visibleObjects.asSequence()
                        .filterIsInstance<Renderable>()
                        .map(Renderable::renderableProviders).flatten()
                        .asIterable(),
                    environmentVisible,
                )
            }
        } else {
            logger.info { "Asset loading progress: ${assetManager.progress.percent * 100}%" }
        }
    }

    override fun dispose() {
        super.dispose()
        modelBatch.dispose()
    }
}
