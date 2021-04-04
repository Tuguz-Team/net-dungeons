package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.toMutable

abstract class GameObject(position: ImmutableVector3) : Disposable {
    companion object : Iterable<GameObject> {
        private val gameObjects = arrayListOf<GameObject>()

        override fun iterator(): Iterator<GameObject> = gameObjects.iterator()
    }

    var position: ImmutableVector3 = position
        set(value) {
            field = value
            modelInstance.transform?.setTranslation(field.toMutable())
            collision?.worldTransform = modelInstance.transform
        }

    lateinit var modelInstance: ModelInstance

    var collision: btCollisionObject? = null
        protected set

    init {
        gameObjects.add(this)
    }

    override fun dispose() {
        gameObjects.remove(this)
        collision?.dispose()
    }
}
