package com.tuguzteam.netdungeons.field

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.objects.TextureObject

class Wall(
    position: ImmutableVector3,
    texture: Texture,
    direction: Direction,
) : TextureObject(position, texture) {
    init {
        transform.rotate(Vector3.Y, direction.degrees)
    }
}
