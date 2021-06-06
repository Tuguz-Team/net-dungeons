package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.math.collision.Ray
import com.tuguzteam.netdungeons.toImmutable
import ktx.math.vec3

interface Bounded : Intersectable {
    val boundingBox: BoundingBox

    override fun intersectedBy(ray: Ray) = Intersector.intersectRayBoundsFast(ray, boundingBox)

    override fun intersectionPoint(ray: Ray) = vec3()
        .takeIf { Intersector.intersectRayBounds(ray, boundingBox, it) }
        ?.toImmutable()
}
