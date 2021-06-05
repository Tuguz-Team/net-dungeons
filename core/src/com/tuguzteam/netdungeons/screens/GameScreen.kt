package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.kotcrab.vis.ui.widget.VisTextButton
import com.tuguzteam.netdungeons.*
import com.tuguzteam.netdungeons.assets.TextureAtlasAll
import com.tuguzteam.netdungeons.assets.TextureAtlasAsset
import com.tuguzteam.netdungeons.field.*
import com.tuguzteam.netdungeons.field.tile.Tile
import com.tuguzteam.netdungeons.input.MovementGestureListener
import com.tuguzteam.netdungeons.input.ObjectChooseGestureListener
import com.tuguzteam.netdungeons.input.RotationZoomGestureListener
import com.tuguzteam.netdungeons.objects.*
import com.tuguzteam.netdungeons.screens.main.MainScreen
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.Dialog
import com.tuguzteam.netdungeons.ui.YesNoDialog
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.graphics.color
import ktx.log.debug
import ktx.log.info
import ktx.log.logger
import ktx.math.*
import kotlin.math.absoluteValue

class GameScreen(loader: Loader) : StageScreen(loader) {
    private companion object {
        private val logger = logger<GameScreen>()

        private const val viewDistance = 4u
        private const val maxViewDistance = 6u
    }

    private val yesNoDialog = YesNoDialog("Are you sure you want to leave this game?",
        onYesOption = { loader.setScreen<MainScreen>() },
        onNoOption = { gameInfoDialog.show(this) }
    )

    private val leaveButton: VisTextButton = VisTextButton("Leave game").apply {
        addListener(ClickListener {
            yesNoDialog.show(this@GameScreen)
        })
    }

    private val gameInfoDialog = Dialog("Menu").apply {
        button("Back to fight!").button(leaveButton)
        size().pad()
    }

    val assetManager = loader.assetManager
    val textureAtlasAll = assetManager[TextureAtlasAsset.All]!! as TextureAtlasAll

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
            assetManager.load(Loader.requiredAssets)
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
        _visibleObjects.clear()
        _visitedObjects.clear()
        field?.dispose()
        field = null
    }

    private val renderVisitedObjects = mutableListOf<GameObject>()
    private val renderVisibleObjects = mutableListOf<GameObject>()
    fun updateVisibleObjects() {
        field?.let { field ->
            _visibleObjects.clear()

            // Current tile is always visible
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
    }

    private fun isVisible(gameObject: GameObject): Boolean {
        if (gameObject !is Bounded) return false
        return camera?.frustum?.boundsInFrustum(gameObject.boundingBox) == true
    }

    override fun render(delta: Float) {
        super.render(delta)
        val camera = camera
        val field = field
        if (camera != null && field != null) {
            rotationZoomGestureListener.update(delta)

            modelBatch.use(camera) {
                // Enable blending
                Gdx.gl20.glEnable(GL20.GL_BLEND)
                Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

                // Render already visited objects
                renderVisitedObjects.forEach { visited ->
                    if (isVisible(visited) && visited is Renderable) {
                        visited.renderableProviders.forEach {
                            render(it, environmentVisited)
                        }
                    }
                }

                // Render currently visible objects
                renderVisibleObjects.forEach { visible ->
                    if (isVisible(visible) && visible is Renderable) {
                        visible.renderableProviders.forEach {
                            render(it, environmentVisible)
                        }
                    }
                }
            }
            logger.debug(Gdx.graphics.framesPerSecond::toString)
        } else {
            logger.info { "Asset loading progress: ${assetManager.progress.percent * 100}%" }
        }
    }

    override fun dispose() {
        super.dispose()
        modelBatch.dispose()
    }

    override fun onBackPressed() {
        loader.setScreen<MainScreen>()
//        if (gameInfoDialog.isHidden) gameInfoDialog.show(this)
    }
}
