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
import com.tuguzteam.netdungeons.field.Field
import com.tuguzteam.netdungeons.field.rooms.Corridor
import com.tuguzteam.netdungeons.field.rooms.Room
import com.tuguzteam.netdungeons.input.ObjectChooseGestureListener
import com.tuguzteam.netdungeons.input.RotationZoomGestureListener
import com.tuguzteam.netdungeons.objects.Renderable
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ktx.async.KtxAsync
import ktx.graphics.color
import ktx.log.info
import ktx.log.logger
import ktx.math.vec3

class GameScreen(loader: Loader, prevScreen: StageScreen) : ReturnableScreen(loader, prevScreen) {
    private companion object {
        private val logger = logger<GameScreen>()
        private val assets = arrayOf(TextureAsset.Wood, TextureAsset.Wood1)
    }

    private lateinit var camera: OrthographicCamera
    private val modelBatch = ModelBatch()
    private val environment = Environment().apply {
        this with ColorAttribute.createAmbientLight(color(red = 0.5f, green = 0.5f, blue = 0.5f))
        this += DirectionalLight().set(
            color(red = 0.8f, green = 0.8f, blue = 0.8f),
            vec3(x = -1f, y = -0.8f, z = -0.2f),
        )
    }

    private val assetManager = loader.assetManager

    private lateinit var rotationZoomGestureListener: RotationZoomGestureListener
    private lateinit var objectChooseGestureListener: ObjectChooseGestureListener

    private var field: Field? = null
    private var room: Room? = null

    init {
        viewport = ExtendViewport(120f, 120f)
    }

    override fun show() {
        super.show()
        camera = OrthographicCamera().apply {
            position.set(vec3(x = 60f, y = 60f, z = 60f))
            lookAt(vec3())
            near = 15f
            far = 185f
            update(true)
        }
        viewport.apply {
            camera = this@GameScreen.camera
            update(Gdx.graphics.width, Gdx.graphics.height)
        }

        rotationZoomGestureListener = RotationZoomGestureListener(camera)
        objectChooseGestureListener = ObjectChooseGestureListener(viewport)
        inputMultiplexer.apply {
            addProcessor(GestureDetector(rotationZoomGestureListener))
            addProcessor(GestureDetector(objectChooseGestureListener))
        }

        KtxAsync.launch {
            assetManager.load(assets.asIterable())
            logger.info { "Asset loading finished" }
            field = Field(side = 9u, assetManager).onEach {
                it.visible = false
            }
            room = Corridor(
                position = immutableVec3(), assetManager,
                width = 3u, length = 7u,
            )
        }
    }

    override fun hide() {
        super.hide()
        runBlocking {
            assetManager.unload(assets.asIterable())
        }
        field?.dispose()
        room?.dispose()
    }

    override fun render(delta: Float) {
        super.render(delta)
        val room = room
        val field = field
        if (room != null && field != null && assetManager.loaded(assets.asIterable())) {
            rotationZoomGestureListener.update()

            modelBatch.use(camera) {
                val renderableProviders = room.asSequence()
                    .filter { gameObject -> gameObject.visible && gameObject is Renderable }
                    .map { gameObject -> (gameObject as Renderable).renderableProvider }
                    .asIterable()
                render(renderableProviders, environment)
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
