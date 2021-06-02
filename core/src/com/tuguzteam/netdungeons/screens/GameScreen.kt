package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.tuguzteam.netdungeons.*
import com.tuguzteam.netdungeons.assets.TextureAsset
import com.tuguzteam.netdungeons.field.*
import com.tuguzteam.netdungeons.input.MovementGestureListener
import com.tuguzteam.netdungeons.input.ObjectChooseGestureListener
import com.tuguzteam.netdungeons.input.RotationZoomGestureListener
import com.tuguzteam.netdungeons.objects.GameObject
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
        private val assets = listOf(TextureAsset.Wood, TextureAsset.Wood1)

        private const val viewDistance = 5u
    }

    val assetManager = loader.assetManager
    var playerPosition = immutableVec2()
        set(value) {
            camera?.apply {
                position += vec3(
                    x = value.x - field.x,
                    z = value.y - field.y,
                )
                update(true)
            }
            field = value
        }
    private val _visibleObjects = mutableListOf<GameObject>()
    val visibleObjects = _visibleObjects as List<GameObject>

    private var camera: OrthographicCamera? = null
    private val modelBatch = ModelBatch()
    private val environmentVisible = Environment().apply {
        val ambient = 0.4f
        this with ColorAttribute.createAmbientLight(
            color(red = ambient, green = ambient, blue = ambient),
        )
        val directional = 0.425f
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
    private val visitedObjects = mutableSetOf<GameObject>()
    private var field: Field? = null

    private lateinit var rotationZoomGestureListener: RotationZoomGestureListener
    private lateinit var objectChooseGestureListener: ObjectChooseGestureListener
    private lateinit var movementGestureListener: MovementGestureListener

    init {
        viewport = ExtendViewport(10f, 10f)
    }

    override fun show() {
        super.show()
        playerPosition = immutableVec2()
        camera = OrthographicCamera().apply {
            position.set(vec3(x = 10f, y = 10f, z = 10f))
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
            field = Field(gameScreen = this@GameScreen)
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
        visitedObjects.clear()
        field?.dispose()
        field = null
    }

    fun updateVisibleObjects() {
        field?.let { field ->
            _visibleObjects.clear()
            // Filter visible objects
            val candidates = field.asSequence().filter { gameObject ->
                var distanceX = (playerPosition.x - gameObject.position.x).absoluteValue
                var distanceY = (playerPosition.y - gameObject.position.z).absoluteValue
                if (gameObject is Wall) {
                    // TODO: check direction of wall
                    distanceX -= Cell.width.toFloat() / 2
                    distanceY -= Cell.length.toFloat() / 2
                }
                distanceX <= viewDistance.toFloat() && distanceY <= viewDistance.toFloat()
            }
            // TODO: check if an object is REALLY visible
            //  (there are no others between this object and the player)
            _visibleObjects += candidates
            visitedObjects += _visibleObjects
        }
    }

    override fun render(delta: Float) {
        super.render(delta)
        val camera = camera
        val field = field
        if (camera != null && field != null && assetManager.loaded(assets)) {
            rotationZoomGestureListener.update()

            @Suppress("UNCHECKED_CAST")
            modelBatch.use(camera) {
                render(
                    (visitedObjects.asSequence() as Sequence<Renderable>)
                        .map(Renderable::renderableProvider)
                        .asIterable(),
                    environmentVisited,
                )
                render(
                    (visibleObjects.asSequence() as Sequence<Renderable>)
                        .map(Renderable::renderableProvider)
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
