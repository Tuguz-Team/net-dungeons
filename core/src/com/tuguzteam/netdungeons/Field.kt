package com.tuguzteam.netdungeons

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.objects.Cube
import ktx.math.vec3

class Field(val side: Int, val assetManager: AssetManager) : Disposable, Iterable<Field.Cell> {
    init {
        if (side <= 0 || side % 2 == 0) {
            throw IllegalArgumentException("Side of field must be positive and odd: $side given")
        }
    }

    private val array = Array(side * side) { i ->
        Cell(vec3(
                x = (i / side - side / 2) * Cell.width,
                y = -Cell.height / 2,
                z = (i % side - side / 2) * Cell.width
        ))
    }

    class Cell(position: Vector3) : Cube(vec3(x = width, y = height, z = width), position) {
        companion object {
            const val width = 5f
            const val height = 2f
        }
    }

    operator fun get(i: Int, j: Int): Cell = array[i * side + j]

    override fun iterator(): Iterator<Cell> = array.iterator()

    override fun dispose() {
        for (cell in this) {
            cell.dispose()
        }
    }
}
