package com.tuguzteam.netdungeons.field.rooms

import com.badlogic.gdx.math.collision.BoundingBox
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.field.Type

sealed class MultiRoom(
    position: ImmutableVector3,
    type: Type,
    private val rooms: Iterable<Room>,
) : Room(position, type) {

    override val boundingBox = BoundingBox().apply {
        rooms.forEach { room -> ext(room.boundingBox) }
    }

    override fun iterator() = rooms.iterator()
}
