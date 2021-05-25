package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.math.collision.Ray
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.times
import com.tuguzteam.netdungeons.toMutable

abstract class ModelObject(
    position: ImmutableVector3 = ImmutableVector3.ZERO,
    protected val model: Model,
) : GameObject(position) {

    override var position = position
        set(value) {
            modelInstance.transform.setTranslation(value.toMutable())
            boundingBox *= modelInstance.transform
            field = value
        }

    protected val modelInstance = ModelInstance(model, position.toMutable())

    val renderableProvider: RenderableProvider = modelInstance

    private var boundingBox =
        modelInstance.calculateBoundingBox(BoundingBox()) * modelInstance.transform

    override fun intersectedBy(ray: Ray) = Intersector.intersectRayBoundsFast(ray, boundingBox)
}
