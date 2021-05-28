package com.tuguzteam.netdungeons.field.rooms

import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.field.Type
import com.tuguzteam.netdungeons.objects.GameObject

sealed class Room(
    position: ImmutableVector3,
    val type: Type,
) : GameObject(position), Iterable<GameObject> {

    override fun dispose() {
        super.dispose()
        forEach(GameObject::dispose)
    }
}
