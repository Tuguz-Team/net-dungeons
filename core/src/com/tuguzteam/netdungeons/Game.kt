package com.tuguzteam.netdungeons

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.tuguzteam.netdungeons.input.GestureListener
import com.tuguzteam.netdungeons.objects.GameObject
import com.tuguzteam.netdungeons.objects.rayTest
import ktx.app.KtxApplicationAdapter
import ktx.app.clearScreen
import ktx.graphics.color
import ktx.log.debug
import ktx.log.logger
import ktx.math.plus
import ktx.math.vec3


class Game : KtxApplicationAdapter, KtxGestureAdapter {
    private lateinit var collisionConfiguration: btCollisionConfiguration
    private lateinit var collisionDispatcher: btCollisionDispatcher
    private lateinit var collisionWorld: btCollisionWorld
    private lateinit var broadphase: btBroadphaseInterface

    private lateinit var modelBatch: ModelBatch
    private lateinit var environment: Environment

    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: Viewport

    private lateinit var gestureListener: GestureListener

    private lateinit var assetManager: AssetManager
    private lateinit var field: Field

    private val modelInstances: ArrayList<ModelInstance> = ArrayList()

    private companion object {
        private val logger = logger<Game>()
    }

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG

        Bullet.init()
        collisionConfiguration = btDefaultCollisionConfiguration()
        collisionDispatcher = btCollisionDispatcher(collisionConfiguration)
        broadphase = btDbvtBroadphase()
        collisionWorld = btCollisionWorld(collisionDispatcher, broadphase, collisionConfiguration)

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

        gestureListener = GestureListener(camera)
        val multiplexer = InputMultiplexer(
                GestureDetector(gestureListener),
                GestureDetector(this)
        )
        Gdx.input.inputProcessor = multiplexer

        assetManager = AssetManager()
    }

    private fun doneLoading() {
        field = Field(side = 9, assetManager)
        modelInstances.run {
            addAll(field.asSequence().map {
                it.modelInstance
            })
            trimToSize()
        }
        field.forEach {
            collisionWorld.addCollisionObject(it.collision)
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun render() {
        clearScreen(red = 0f, green = 0f, blue = 0f)

        if (assetManager.isFinished) {
            gestureListener.update()
            modelBatch.use(camera) {
                it.render(modelInstances, environment)
                if (line != null) {
                    it.render(line)
                }
            }
        } else {
            logger.debug { "Asset loading progress: ${assetManager.progress}" }
            if (assetManager.update()) {
                doneLoading()
            }
        }
    }

    private var line: ModelInstance? = null
    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
        val ray = viewport.getPickRay(x, y)

        line?.model?.dispose()
        val modelBuilder = ModelBuilder()
        modelBuilder.begin()
        val builder = modelBuilder.part("line", 1, 3, Material())
        builder.setColor(Color.RED)
        builder.line(ray.origin, ray.direction.cpy().scl(50f) + ray.origin)
        val lineModel = modelBuilder.end()
        line = ModelInstance(lineModel)

        val collisionBody = rayTest(collisionWorld, ray, 100f)
        val gameObject = GameObject.find {
            it.collision == collisionBody
        }
        gameObject?.modelInstance?.materials?.get(0)?.set(
            ColorAttribute(ColorAttribute.Diffuse, Color.RED)
        )
        return gameObject != null
    }

    override fun dispose() {
        collisionConfiguration.dispose()
        collisionDispatcher.dispose()
        collisionWorld.dispose()
        modelBatch.dispose()
        field.dispose()
        assetManager.dispose()
    }
}
