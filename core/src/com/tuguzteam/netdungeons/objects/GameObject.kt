package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.utils.Disposable

abstract class GameObject : Disposable {
    companion object : Iterable<GameObject> {
        private val gameObjects = arrayListOf<GameObject>()
        override fun iterator() = gameObjects.iterator()
    }

    init {
        @Suppress("LeakingThis")
        gameObjects += this
    }

    override fun dispose() {
        gameObjects -= this
    }
}
