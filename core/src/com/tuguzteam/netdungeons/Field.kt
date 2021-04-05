package com.tuguzteam.netdungeons

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.objects.Cube
import com.tuguzteam.netdungeons.objects.Focusable
import ktx.graphics.color

class Field(val side: Int, val assetManager: AssetManager) : Disposable, Iterable<Field.Cell> {
    init {
        if (side <= 0 || side % 2 == 0) {
            throw IllegalArgumentException("Side of field must be positive and odd: $side given")
        }
    }

    private val array = Array(side * side) { i ->
        Cell(
            ImmutableVector3(
                x = (i / side - side / 2) * Cell.width,
                y = -Cell.height / 2,
                z = (i % side - side / 2) * Cell.width
            )
        )
    }

    class Cell(position: ImmutableVector3) : Focusable, Cube(
        dimensions = ImmutableVector3(x = width, y = height, z = width),
        color(red = random(), green = random(), blue = random()),
        position
    ) {
        companion object {
            const val width = 5f
            const val height = 2f
        }

        var initialColor = color

        override fun onAcquireFocus() {
            color = Color.RED
        }

        override fun onLoseFocus() {
            color = initialColor
        }
    }

    operator fun get(i: Int, j: Int) = array[i * side + j]

    override fun iterator() = array.iterator()

    override fun dispose() {
        for (cell in this) {
            cell.dispose()
        }
    }
}
