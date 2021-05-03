package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.assets.AssetManager
import com.tuguzteam.netdungeons.assets.ModelAsset
import com.tuguzteam.netdungeons.field.Field
import com.tuguzteam.netdungeons.input.ObjectChooseGestureListener
import com.tuguzteam.netdungeons.input.RotationZoomGestureListener
import com.tuguzteam.netdungeons.use
import ktx.graphics.color
import ktx.log.debug
import ktx.log.logger
import ktx.math.vec3

class GameScreen(loader: Loader) : Screen(loader) {
    private val modelBatch: ModelBatch = ModelBatch()
    private val environment: Environment = Environment().apply {
        set(
            ColorAttribute.createAmbientLight(color(red = 0.3f, green = 0.3f, blue = 0.3f))
        )
        add(
            DirectionalLight().set(
                color(red = 0.6f, green = 0.6f, blue = 0.6f),
                vec3(x = -1f, y = -0.8f, z = -0.2f)
            )
        )
    }
    private val camera: OrthographicCamera = OrthographicCamera().apply {
        position.set(vec3(x = 30f, y = 30f, z = 30f))
        lookAt(vec3())
        near = 20f
        far = 120f
        update()
    }
    private val renderables: ArrayList<RenderableProvider> = ArrayList()
    private val assetManager: AssetManager = AssetManager().apply {
        addTask(ModelAsset.Suzanne) {
            logger.debug { "Asset loading finished" }
            field = Field(side = 9)
            renderables.run {
                addAll(field.map { it.renderableProvider })
                trimToSize()
            }
        }
    }

    private val rotationZoomGestureListener: RotationZoomGestureListener
    private val objectChooseGestureListener: ObjectChooseGestureListener

    private lateinit var field: Field

    private companion object {
        private val logger = logger<GameScreen>()
    }

    init {
        viewport = ExtendViewport(50f, 50f, camera)

        rotationZoomGestureListener = RotationZoomGestureListener(camera)
        objectChooseGestureListener = ObjectChooseGestureListener(viewport)
        val multiplexer = InputMultiplexer(
            GestureDetector(rotationZoomGestureListener),
            GestureDetector(objectChooseGestureListener)
        )
        Gdx.input.inputProcessor = multiplexer
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun render(delta: Float) {
        if (assetManager.update()) {
            rotationZoomGestureListener.update()
            modelBatch.use(camera) {
                render(renderables, environment)
            }
        } else {
            logger.debug { "Asset loading progress: ${assetManager.progress}" }
        }
    }

    override fun dispose() {
        super.dispose()
        modelBatch.dispose()
        field.dispose()
        assetManager.dispose()
    }
}
