package com.tuguzteam.netdungeons.field.rooms

import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.objects.GameObject

sealed class Room(position: ImmutableVector3) : GameObject(position), Iterable<GameObject> {
    override fun dispose() {
        super.dispose()
        forEach(GameObject::dispose)
    }
}
