package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.ImmutableVector3

abstract class GameObject(open var position: ImmutableVector3) : Disposable {
    companion object : Iterable<GameObject> {
        private val gameObjects = arrayListOf<GameObject>()

        override fun iterator() = gameObjects.iterator()
    }

    init {
        @Suppress("LeakingThis")
        gameObjects.add(element = this)
    }

    override fun dispose() {
        gameObjects.remove(element = this)
    }
}
