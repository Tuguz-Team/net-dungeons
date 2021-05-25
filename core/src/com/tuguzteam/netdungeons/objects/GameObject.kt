package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.times
import com.tuguzteam.netdungeons.toMutable

abstract class GameObject(
    position: ImmutableVector3 = ImmutableVector3.ZERO,
    protected val model: Model,
) : Disposable {

    companion object : Iterable<GameObject> {
        private val gameObjects = arrayListOf<GameObject>()

        override fun iterator() = gameObjects.iterator()
    }

    var position = position
        set(value) {
            modelInstance.transform.setTranslation(value.toMutable())
            boundingBox *= modelInstance.transform
            field = value
        }

    protected val modelInstance = ModelInstance(model, position.toMutable())

    val renderableProvider
        get() = modelInstance as RenderableProvider

    var boundingBox =
        modelInstance.calculateBoundingBox(BoundingBox()) * modelInstance.transform
        private set

    init {
        @Suppress("LeakingThis")
        gameObjects.add(this)
    }

    override fun dispose() {
        gameObjects.remove(this)
    }
}

infix fun Ray.intersects(gameObject: GameObject) =
    Intersector.intersectRayBoundsFast(this, gameObject.boundingBox)
