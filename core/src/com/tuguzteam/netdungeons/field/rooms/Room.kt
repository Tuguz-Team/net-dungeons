package com.tuguzteam.netdungeons.field.rooms

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.math.collision.Ray
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.field.Type
import com.tuguzteam.netdungeons.objects.GameObject
import com.tuguzteam.netdungeons.objects.Intersectable
import com.tuguzteam.netdungeons.toImmutable
import ktx.math.vec3

sealed class Room(
    position: ImmutableVector3,
    val type: Type,
) : GameObject(position), Iterable<GameObject>, Intersectable {

    companion object : Iterable<Room> {
        private val rooms = arrayListOf<Room>()

        override fun iterator() = rooms.iterator()
    }

    internal abstract val boundingBox: BoundingBox

    init {
        @Suppress("LeakingThis")
        rooms.add(element = this)
    }

    override fun intersectedBy(ray: Ray) = Intersector.intersectRayBoundsFast(ray, boundingBox)

    override fun intersectionPoint(ray: Ray) = vec3()
        .takeIf { Intersector.intersectRayBounds(ray, boundingBox, it) }
        ?.toImmutable()

    override fun dispose() {
        super.dispose()
        rooms.remove(element = this)
        forEach(GameObject::dispose)
    }
}
