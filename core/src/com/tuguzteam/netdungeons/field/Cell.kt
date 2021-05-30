package com.tuguzteam.netdungeons.field

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.objects.Focusable
import com.tuguzteam.netdungeons.objects.TextureObject

class Cell(
    position: ImmutableVector3,
    texture: Texture,
) : TextureObject(position, texture, width, length), Focusable {

    companion object {
        const val width = 1u
        const val length = width
    }

    init {
        transform.rotate(Vector3.X, -90f)
    }

    private val initialColor = color

    override fun focus() {
        color = Color.RED
    }

    override fun unfocus() {
        color = initialColor
    }
}
