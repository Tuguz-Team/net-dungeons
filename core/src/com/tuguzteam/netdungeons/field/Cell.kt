package com.tuguzteam.netdungeons.field

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.objects.Cube
import com.tuguzteam.netdungeons.objects.Focusable
import ktx.graphics.color

class Cell(position: ImmutableVector3) : Focusable, Cube(
    dimensions = ImmutableVector3(x = width, y = height, z = width),
    color(red = MathUtils.random(), green = MathUtils.random(), blue = MathUtils.random()),
    position
) {
    companion object {
        const val width = 5f
        const val height = 2f
    }

    private var initialColor = color

    override fun focus() {
        color = Color.RED
    }

    override fun unfocus() {
        color = initialColor
    }
}
