package com.tuguzteam.netdungeons.field

import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.assets.AssetManager
import com.tuguzteam.netdungeons.assets.TextureAsset

class Field(val side: UInt, assetManager: AssetManager) : Disposable, Iterable<Cell> {
    private val wood = assetManager[TextureAsset.Wood]!!

    init {
        if (side % 2u == 0u) {
            throw IllegalArgumentException("Side of field must be positive and odd: $side given")
        }
    }

    private val cells = Array((side * side).toInt()) { i ->
        Cell(
            position = ImmutableVector3(
                x = ((i / side.toInt() - side.toInt() / 2) * Cell.width.toInt()).toFloat(),
                y = 0f,
                z = ((i % side.toInt() - side.toInt() / 2) * Cell.width.toInt()).toFloat(),
            ), texture = wood,
        )
    }

    operator fun get(i: UInt, j: UInt) = cells[(i * side + j).toInt()]

    override fun iterator() = cells.iterator()

    override fun dispose() {
        forEach { cell -> cell.dispose() }
    }
}
