package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy
import com.badlogic.gdx.graphics.g3d.decals.Decal
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.assets.Asset
import com.tuguzteam.netdungeons.assets.ModelAsset
import com.tuguzteam.netdungeons.assets.TextureAsset
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
        private val assets = arrayListOf<Asset>(TextureAsset.Wood, ModelAsset.Suzanne)
    }

    private val camera = OrthographicCamera().apply {
        position.set(vec3(x = 60f, y = 60f, z = 60f))
        lookAt(vec3())
        near = 15f
        far = 185f
        update()
    }

    private val modelBatch = ModelBatch()
    private val decalBatch = DecalBatch(CameraGroupStrategy(camera))
    private val environment = Environment().apply {
        this with ColorAttribute.createAmbientLight(color(red = 0.3f, green = 0.3f, blue = 0.3f))
        this += DirectionalLight().set(
            color(red = 0.6f, green = 0.6f, blue = 0.6f),
            vec3(x = -1f, y = -0.8f, z = -0.2f),
        )
    }

    private val renderables = arrayListOf<RenderableProvider>()
    private val decals = arrayListOf<Decal>()
    private val assetManager = loader.assetManager

    private val rotationZoomGestureListener: RotationZoomGestureListener
    private val objectChooseGestureListener: ObjectChooseGestureListener

    private lateinit var field: Field

    init {
        viewport = ExtendViewport(120f, 120f, camera)

        rotationZoomGestureListener = RotationZoomGestureListener(camera)
        inputMultiplexer.addProcessor(GestureDetector(rotationZoomGestureListener))

        objectChooseGestureListener = ObjectChooseGestureListener(viewport)
        inputMultiplexer.addProcessor(GestureDetector(objectChooseGestureListener))
    }

    override fun show() {
        super.show()
        KtxAsync.launch {
            assetManager.load(assets)
            logger.info { "Asset loading finished" }
            field = Field(side = 9u, assetManager)
            decals.run {
                addAll(field.map { it.decal })
                trimToSize()
            }
        }
    }

    override fun hide() {
        super.hide()
        runBlocking {
            assetManager.unload(assets)
        }
        field.dispose()
        renderables.clear()
        decals.clear()
    }

    override fun render(delta: Float) {
        super.render(delta)
        if (assetManager.loaded(assets)) {
            rotationZoomGestureListener.update()
            decalBatch.use(decals)
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
        decalBatch.dispose()
    }
}
