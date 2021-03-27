package com.tuguzteam.netdungeons

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.objects.Cube
import com.tuguzteam.netdungeons.objects.GameObject
import ktx.math.vec3

class Field : Disposable, Iterable<GameObject> {
    private companion object {
        private const val SIZE = 4
        private const val dimension = SIZE * 2 + 1
    }

    private val array = Array(dimension * dimension) { i ->
        Cell(vec3(
                x = (i / dimension - SIZE) * Cell.width,
                y = -1f,
                z = (i % dimension - SIZE) * Cell.width
        ))
    }

    class Cell(position: Vector3) : Cube(vec3(x = width, y = 2f, z = width), position) {
        companion object {
            const val width = 5f
        }
    }

    operator fun get(i: Int, j: Int): Cell = array[i * dimension + j]

    override fun iterator(): Iterator<Cell> = array.iterator()

    override fun dispose() {
        for (cell in this) {
            cell.dispose()
        }
    }
}
