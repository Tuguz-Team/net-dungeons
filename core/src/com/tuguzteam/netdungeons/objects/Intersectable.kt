package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.math.collision.Ray
import com.tuguzteam.netdungeons.ImmutableVector3

interface Intersectable {
    fun intersectedBy(ray: Ray): Boolean
    fun intersectionPoint(ray: Ray): ImmutableVector3?
}

infix fun Ray.intersects(intersectable: Intersectable) = intersectable.intersectedBy(this)

@Suppress("unused")
infix fun Ray.intersectionOf(intersectable: Intersectable) = intersectable.intersectionPoint(this)
