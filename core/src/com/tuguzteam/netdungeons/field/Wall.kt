package com.tuguzteam.netdungeons.field

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.objects.TextureObject

class Wall(
    position: ImmutableVector3,
    texture: Texture,
    direction: Direction,
) : TextureObject(position, texture, width, height) {

    companion object {
        const val width = 1u
        const val height = width
    }

    init {
        transform.rotate(Vector3.Y, direction.degrees)
    }
}
