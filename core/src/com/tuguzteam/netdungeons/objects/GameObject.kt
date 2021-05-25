package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.ImmutableVector3

abstract class GameObject(
    open var position: ImmutableVector3 = ImmutableVector3.ZERO,
) : Disposable {

    companion object : Iterable<GameObject> {
        private val gameObjects = arrayListOf<GameObject>()

        override fun iterator() = gameObjects.iterator()
    }

    init {
        @Suppress("LeakingThis")
        gameObjects.add(element = this)
    }

    abstract fun intersectedBy(ray: Ray): Boolean

    override fun dispose() {
        gameObjects.remove(element = this)
    }
}

infix fun Ray.intersects(gameObject: GameObject) = gameObject.intersectedBy(ray = this)
