package com.tuguzteam.netdungeons

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.input.GestureDetector
import ktx.app.KtxApplicationAdapter

class NetDungeonsGame : KtxApplicationAdapter {
    private lateinit var camera: OrthographicCamera
    private lateinit var modelBatch: ModelBatch
    private lateinit var field: Field
    private lateinit var environment: Environment
    private lateinit var gestureListener: GestureListener

    companion object {
        private const val VIEW_MULTIPLIER = 0.05f
    }

    override fun create() {
        environment = Environment().apply {
            set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
            add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
        }

        modelBatch = ModelBatch()

        camera = OrthographicCamera(
                Gdx.graphics.width * VIEW_MULTIPLIER,
                Gdx.graphics.height * VIEW_MULTIPLIER,
        ).apply {
            position.set(30f, 30f, 30f)
            lookAt(0f, 0f, 0f)
            far = 500f
            update()
        }

        field = Field()

        gestureListener = GestureListener(camera)
        Gdx.input.inputProcessor = GestureDetector(gestureListener)
    }

    override fun resize(width: Int, height: Int) {
        camera.apply {
            viewportWidth = width * VIEW_MULTIPLIER
            viewportHeight = height * VIEW_MULTIPLIER
        }
    }

    override fun render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        gestureListener.update()
        modelBatch.begin(camera)
        for (array in field.grid) {
            for (item in array!!) {
                modelBatch.render(item!!.modelInstance, environment)
            }
        }
        modelBatch.end()
    }

    override fun dispose() {
        modelBatch.dispose()
        field.dispose()
    }
}
