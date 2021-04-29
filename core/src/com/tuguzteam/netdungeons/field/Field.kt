package com.tuguzteam.netdungeons.field

import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.AssetManager
import com.tuguzteam.netdungeons.ImmutableVector3

class Field(val side: Int, val assetManager: AssetManager) : Disposable, Iterable<Cell> {
    init {
        if (side <= 0 || side % 2 == 0) {
            throw IllegalArgumentException("Side of field must be positive and odd: $side given")
        }
    }

    private val cells = Array(side * side) { i ->
        Cell(
            ImmutableVector3(
                x = (i / side - side / 2) * Cell.width,
                y = -Cell.height / 2,
                z = (i % side - side / 2) * Cell.width
            )
        )
    }

    operator fun get(i: Int, j: Int) = cells[i * side + j]

    override fun iterator() = cells.iterator()

    override fun dispose() {
        for (cell in this) {
            cell.dispose()
        }
    }
}
