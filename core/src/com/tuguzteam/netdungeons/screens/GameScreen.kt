package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.assets.ModelAsset
import com.tuguzteam.netdungeons.field.Field
import com.tuguzteam.netdungeons.input.ObjectChooseGestureListener
import com.tuguzteam.netdungeons.input.RotationZoomGestureListener
import com.tuguzteam.netdungeons.plusAssign
import com.tuguzteam.netdungeons.use
import com.tuguzteam.netdungeons.with
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
    }

    private val modelBatch: ModelBatch = ModelBatch()
    private val environment: Environment = Environment().apply {
        this with ColorAttribute.createAmbientLight(color(red = 0.3f, green = 0.3f, blue = 0.3f))
        this += DirectionalLight().set(
            color(red = 0.6f, green = 0.6f, blue = 0.6f),
            vec3(x = -1f, y = -0.8f, z = -0.2f),
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
    private val assetManager = loader.assetManager

    private val rotationZoomGestureListener: RotationZoomGestureListener
    private val objectChooseGestureListener: ObjectChooseGestureListener

    private lateinit var field: Field

    init {
        viewport = ExtendViewport(50f, 50f, camera)

        rotationZoomGestureListener = RotationZoomGestureListener(camera)
        inputMultiplexer.addProcessor(GestureDetector(rotationZoomGestureListener))

        objectChooseGestureListener = ObjectChooseGestureListener(viewport)
        inputMultiplexer.addProcessor(GestureDetector(objectChooseGestureListener))
    }

    override fun show() {
        super.show()
        KtxAsync.launch {
            assetManager.load(ModelAsset.Suzanne)
            logger.info { "Asset loading finished" }
            field = Field(side = 9)
            renderables.run {
                addAll(field.map { it.renderableProvider })
                trimToSize()
            }
        }
    }

    override fun hide() {
        super.hide()
        runBlocking {
            assetManager.unload(ModelAsset.Suzanne)
        }
        field.dispose()
        renderables.clear()
    }

    override fun render(delta: Float) {
        super.render(delta)
        if (assetManager.loaded(ModelAsset.Suzanne)) {
            rotationZoomGestureListener.update()
            modelBatch.use(camera) {
                render(renderables, environment)
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
