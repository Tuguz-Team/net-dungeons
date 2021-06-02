package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.utils.Array
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.times
import com.tuguzteam.netdungeons.toImmutable
import com.tuguzteam.netdungeons.toMutable
import ktx.math.vec3

abstract class ModelObject(
    position: ImmutableVector3,
    protected val model: Model,
) : GameObject(), Renderable, Intersectable {

    companion object {
        internal val modelBuilder = ModelBuilder()
    }

    open var position = position
        set(value) {
            transform.setTranslation(value.toMutable())
            boundingBox *= transform
            field = value
        }

    private val modelInstance = ModelInstance(model, position.toMutable())

    override val renderableProviders = sequenceOf(modelInstance as RenderableProvider)
    val transform: Matrix4
        get() = modelInstance.transform
    val materials: Array<Material>
        get() = modelInstance.materials

    private var boundingBox = modelInstance.calculateBoundingBox() * transform

    override fun intersectedBy(ray: Ray) = Intersector.intersectRayBoundsFast(ray, boundingBox)

    override fun intersectionPoint(ray: Ray) = vec3()
        .takeIf { Intersector.intersectRayBounds(ray, boundingBox, it) }
        ?.toImmutable()
}

fun ModelInstance.calculateBoundingBox(): BoundingBox = this.calculateBoundingBox(BoundingBox())
