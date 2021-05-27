package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.math.collision.Ray

interface Intersectable {
    fun intersectedBy(ray: Ray): Boolean
}

infix fun Ray.intersects(intersectable: Intersectable) = intersectable.intersectedBy(ray = this)
