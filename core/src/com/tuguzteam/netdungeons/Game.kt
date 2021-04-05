package com.tuguzteam.netdungeons

import com.badlogic.gdx.Application
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
import com.badlogic.gdx.utils.viewport.Viewport
import com.tuguzteam.netdungeons.input.ObjectChooseGestureListener
import com.tuguzteam.netdungeons.input.RotationZoomGestureListener
import ktx.app.KtxApplicationAdapter
import ktx.app.clearScreen
import ktx.graphics.color
import ktx.log.debug
import ktx.log.logger
import ktx.math.vec3

class Game : KtxApplicationAdapter {
    private lateinit var modelBatch: ModelBatch
    private lateinit var environment: Environment

    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: Viewport

    private lateinit var rotationZoomGestureListener: RotationZoomGestureListener
    private lateinit var objectChooseGestureListener: ObjectChooseGestureListener

    private lateinit var assetManager: AssetManager
    private lateinit var field: Field

    private val renderables: ArrayList<RenderableProvider> = ArrayList()

    private companion object {
        private val logger = logger<Game>()
    }

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG

        modelBatch = ModelBatch()
        environment = Environment().apply {
            set(
                ColorAttribute(
                    ColorAttribute.AmbientLight,
                    color(red = 0.3f, green = 0.3f, blue = 0.3f)
                )
            )
            add(
                DirectionalLight().set(
                    color(red = 0.6f, green = 0.6f, blue = 0.6f),
                    vec3(x = -1f, y = -0.8f, z = -0.2f)
                )
            )
        }

        camera = OrthographicCamera().apply {
            position.set(vec3(x = 30f, y = 30f, z = 30f))
            lookAt(vec3())
            near = 20f
            far = 120f
            update()
        }
        viewport = ExtendViewport(50f, 50f, camera)

        rotationZoomGestureListener = RotationZoomGestureListener(camera)
        objectChooseGestureListener = ObjectChooseGestureListener(viewport)
        val multiplexer = InputMultiplexer(
            GestureDetector(rotationZoomGestureListener),
            GestureDetector(objectChooseGestureListener)
        )
        Gdx.input.inputProcessor = multiplexer

        assetManager = AssetManager()
    }

    private fun doneLoading() {
        field = Field(side = 9, assetManager)
        renderables.run {
            addAll(field.map { it.renderableProvider })
            trimToSize()
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun render() {
        clearScreen(red = 0f, green = 0f, blue = 0f)

        if (assetManager.isFinished) {
            rotationZoomGestureListener.update()
            modelBatch.use(camera) {
                it.render(renderables, environment)
            }
        } else {
            logger.debug { "Asset loading progress: ${assetManager.progress}" }
            if (assetManager.update()) {
                doneLoading()
            }
        }
    }

    override fun dispose() {
        modelBatch.dispose()
        field.dispose()
        assetManager.dispose()
    }
}
