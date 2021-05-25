package com.tuguzteam.netdungeons.field

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.objects.DecalObject
import com.tuguzteam.netdungeons.objects.Focusable

class Cell(
    position: ImmutableVector3,
    texture: Texture
) : Focusable, DecalObject(position, texture) {

    companion object {
        const val width = 15u
    }

    var color: Color = Color.WHITE
        set(value) {
            decal.color = value
            field = value
        }

    init {
        decal.rotateX(90f)
    }

    override fun focus() {
        color = Color.RED
    }

    override fun unfocus() {
        color = Color.WHITE
    }
}
