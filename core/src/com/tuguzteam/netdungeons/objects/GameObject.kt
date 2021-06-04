package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.utils.Disposable
import ktx.assets.dispose

abstract class GameObject : Disposable {
    companion object : Iterable<GameObject>, Disposable {
        private val gameObjects = arrayListOf<GameObject>()
        override fun iterator() = gameObjects.iterator()

        override fun dispose() {
            TextureObject.dispose()
            gameObjects.dispose()
            gameObjects.clear()
        }
    }

    init {
        @Suppress("LeakingThis")
        gameObjects += this
    }

    override fun dispose() {
        gameObjects -= this
    }
}
