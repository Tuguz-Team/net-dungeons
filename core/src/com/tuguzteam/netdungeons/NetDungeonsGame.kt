package com.tuguzteam.netdungeons

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxApplicationAdapter

class NetDungeonsGame : KtxApplicationAdapter {
    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: Viewport

    private lateinit var field: Field

    private lateinit var modelBatch: ModelBatch
    private lateinit var environment: Environment
    private lateinit var gestureListener: GestureListener

    override fun create() {
        environment = Environment().apply {
            set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
            add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
        }

        modelBatch = ModelBatch()

        camera = OrthographicCamera().apply {
            position.set(30f, 30f, 30f)
            lookAt(0f, 0f, 0f)
            near = 20f
            far = 120f
            update()
        }
        viewport = ExtendViewport(50f, 50f, camera)

        field = Field()

        gestureListener = GestureListener(camera)
        Gdx.input.inputProcessor = GestureDetector(gestureListener)
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
    }
}

inline fun <T : ModelBatch> T.use(camera: Camera, action: (T) -> Unit) {
    begin(camera)
    action(this)
    end()
}
