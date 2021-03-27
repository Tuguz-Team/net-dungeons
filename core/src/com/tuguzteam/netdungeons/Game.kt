package com.tuguzteam.netdungeons

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.tuguzteam.netdungeons.ui.GestureListener
import ktx.app.KtxApplicationAdapter
import ktx.graphics.color
import ktx.math.vec3

class Game : KtxApplicationAdapter {
    private lateinit var modelBatch: ModelBatch
    private lateinit var environment: Environment

    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: Viewport

    private lateinit var gestureListener: GestureListener

    private lateinit var field: Field
    private lateinit var assetManager: AssetManager

    override fun create() {
        modelBatch = ModelBatch()
        environment = Environment().apply {
            set(ColorAttribute(
                    ColorAttribute.AmbientLight,
                    color(red = 0.3f, green = 0.3f, blue = 0.3f)
            ))
            add(DirectionalLight().set(
                    color(red = 0.6f, green = 0.6f, blue = 0.6f),
                    vec3(x = -1f, y = -0.8f, z = -0.2f)
            ))
        }

        camera = OrthographicCamera().apply {
            position.set(vec3(x = 30f, y = 30f, z = 30f))
            lookAt(vec3(x = 0f, y = 0f, z = 0f))
            near = 20f
            far = 120f
            update()
        }
        viewport = ExtendViewport(50f, 50f, camera)

        gestureListener = GestureListener(camera)
        Gdx.input.inputProcessor = GestureDetector(gestureListener)

        assetManager = AssetManager()
        field = Field(side = 9, assetManager)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        gestureListener.update()
        modelBatch.use(camera) {
            for (cell in field) {
                it.render(cell.modelInstance, environment)
            }
        }
    }

    override fun dispose() {
        modelBatch.dispose()
        field.dispose()
        assetManager.dispose()
    }
}
