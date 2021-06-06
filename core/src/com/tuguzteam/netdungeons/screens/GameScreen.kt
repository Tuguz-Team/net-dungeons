package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.tuguzteam.netdungeons.*
import com.tuguzteam.netdungeons.assets.Asset
import com.tuguzteam.netdungeons.assets.ModelAsset
import com.tuguzteam.netdungeons.assets.TextureAtlasAll
import com.tuguzteam.netdungeons.assets.TextureAtlasAsset
import com.tuguzteam.netdungeons.field.*
import com.tuguzteam.netdungeons.field.tile.Tile
import com.tuguzteam.netdungeons.input.MovementGestureListener
import com.tuguzteam.netdungeons.input.ObjectChooseGestureListener
import com.tuguzteam.netdungeons.input.RotationZoomGestureListener
import com.tuguzteam.netdungeons.objects.*
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.graphics.color
import ktx.log.debug
import ktx.log.info
import ktx.log.logger
import ktx.math.*
import kotlin.math.absoluteValue

class GameScreen(loader: Loader, prevScreen: StageScreen) : ReturnableScreen(loader, prevScreen) {
    private companion object {
        private val logger = logger<GameScreen>()
        private val assets = listOf<Asset>(ModelAsset.MaleExample)

        private const val viewDistance = 4u
        private const val maxViewDistance = 6u
    }

    val assetManager = loader.assetManager
    val textureAtlasAll = assetManager[TextureAtlasAsset.All]!! as TextureAtlasAll

    var playerPosition = immutableGridPoint2()
        set(value) {
            player?.position = Tile.toImmutableVec3(point = value)
            camera?.apply {
                position += Tile.toImmutableVec3(point = value - field).toMutable()
                update(true)
            }
            field = value
        }
    var playerDirection: Direction?
        get() = player?.direction
        set(value) {
            player?.direction = value ?: return
        }

    private val _visibleObjects = mutableSetOf<GameObject>()
    val visibleObjects: Set<GameObject> = _visibleObjects

    private val _visitedObjects = mutableSetOf<GameObject>()
    val visitedObjects: Set<GameObject> = _visitedObjects

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
        val ambient = 0.3f
        this with ColorAttribute.createAmbientLight(
            color(red = ambient, green = ambient, blue = ambient),
        )
    }
    var field: Field? = null
        private set
    private var player: Player? = null

    private lateinit var rotationZoomGestureListener: RotationZoomGestureListener
    private lateinit var objectChooseGestureListener: ObjectChooseGestureListener
    private lateinit var movementGestureListener: MovementGestureListener

    init {
        viewport = ExtendViewport(20f, 20f)
    }

    override fun show() {
        super.show()
        playerPosition = immutableGridPoint2()
        camera = OrthographicCamera().apply {
            val pos = vec3(x = 12f, z = 12f)
            val angle = 30f
            pos.y = pos.len() * MathUtils.sinDeg(angle)
            position.set(pos)
            lookAt(vec3())
            near = 1f
            far = 60f
            update(true)
        }
        viewport.apply {
            camera = this@GameScreen.camera
            update(widthFraction().toInt(), heightFraction().toInt())
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
                    x = (size / 2u).toInt(),
                    y = (size / 2u).toInt(),
                )
            }
            logger.info { "Map generation finished" }
            player = Player(
                position = Tile.toImmutableVec3(playerPosition),
                model = assetManager[ModelAsset.MaleExample]!!,
            )
            updateVisibleObjects()
        }
    }

    override fun hide() {
        super.hide()
        KtxAsync.launch {
            assetManager.unload(assets)
        }
        _visibleObjects.clear()
        renderVisibleObjects.clear()
        _visitedObjects.clear()
        renderVisitedObjects.clear()
        field?.dispose()
        field = null
        player?.dispose()
        player = null
    }

    private val renderVisitedObjects = mutableListOf<GameObject>()
    private val renderVisibleObjects = mutableListOf<GameObject>()
    fun updateVisibleObjects() {
        val field = field ?: return
        _visibleObjects.clear()

        // Current tile and the player are always visible
        _visibleObjects += player ?: return
        _visibleObjects += field[playerPosition] ?: return
        // Filter visible objects (Field Of Vision by shadow casting)
        _visibleObjects += FieldOfVision.compute(field, playerPosition, viewDistance)
        _visitedObjects += visibleObjects

        // Preserve list of visited objects to render
        renderVisitedObjects.clear()
        visitedObjects.filterTo(renderVisitedObjects) { gameObject ->
            // Avoid duplicates (we don't need to render visible objects as visited)
            if (gameObject in visibleObjects) return@filterTo false
            when (gameObject) {
                is Tile -> {
                    val x = (gameObject.position.x - playerPosition.x).absoluteValue
                    val y = (gameObject.position.y - playerPosition.y).absoluteValue
                    val distance = maxViewDistance.toInt()
                    x <= distance && y <= distance
                }
                is ModelObject -> {
                    val position = Tile.toImmutableVec3(playerPosition)
                    val x = (gameObject.position.x - position.x).absoluteValue
                    val y = (gameObject.position.z - position.z).absoluteValue
                    val distance = (maxViewDistance * Tile.size).toFloat()
                    x <= distance && y <= distance
                }
                else -> false
            }
        }
        renderVisitedObjects.forEach { gameObject ->
            if (gameObject is Blendable) gameObject.alpha = 0.75f
        }

        // Preserve list of visible objects to render
        renderVisibleObjects.clear()
        renderVisibleObjects += visibleObjects
        renderVisibleObjects.forEach { gameObject ->
            if (gameObject is Blendable) gameObject.alpha = 1f
        }
    }

    private fun isVisible(gameObject: GameObject): Boolean {
        if (gameObject !is Renderable) return false
        return camera?.frustum?.boundsInFrustum(gameObject.boundingBox) == true
    }

    override fun render(delta: Float) {
        super.render(delta)
        val camera = camera
        val field = field
        if (camera != null && field != null && assetManager.loaded(ModelAsset.MaleExample)) {
            rotationZoomGestureListener.update(delta)

            modelBatch.use(camera) {
                renderContext.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
                renderContext.setDepthTest(GL20.GL_LESS)

                fun render(gameObject: GameObject, environment: Environment) {
                    if (isVisible(gameObject) && gameObject is Renderable) {
                        gameObject.renderableProviders.forEach {
                            render(it, environment)
                        }
                    }
                }

                renderVisitedObjects.forEach { render(it, environmentVisited) }
                renderVisibleObjects.forEach { render(it, environmentVisible) }
            }
            logger.debug(framesPerSecond::toString)
        } else {
            logger.info { "Asset loading progress: ${assetManager.progress.percent * 100}%" }
        }
    }

    override fun dispose() {
        super.dispose()
        modelBatch.dispose()
    }
}
