package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld
import ktx.math.plus

fun rayTest(collisionWorld: btCollisionWorld, ray: Ray, rayLength: Float): btCollisionObject? {
    val rayFrom = ray.origin.cpy()
    val rayTo = ray.direction.cpy().scl(rayLength) + rayFrom
    val callback = ClosestRayResultCallback(rayFrom, rayTo).apply {
        collisionObject = null
        closestHitFraction = 1f
    }
    collisionWorld.rayTest(rayFrom, rayTo, callback)
    if (callback.hasHit()) {
        return callback.collisionObject
    }
    return null
}
