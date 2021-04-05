package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.toMutable

abstract class GameObject(position: ImmutableVector3) : Disposable {
    companion object : Iterable<GameObject> {
        private val gameObjects = arrayListOf<GameObject>()

        override fun iterator() = gameObjects.iterator()
    }

    var position = position
        set(value) {
            field = value
            modelInstance.transform!!.setTranslation(field.toMutable())
            boundingBox.mul(modelInstance.transform)
        }

    protected lateinit var modelInstance: ModelInstance

    val renderableProvider
        get() = modelInstance as RenderableProvider

    lateinit var boundingBox: BoundingBox
        protected set

    init {
        gameObjects.add(this)
    }

    override fun dispose() {
        gameObjects.remove(this)
    }
}
