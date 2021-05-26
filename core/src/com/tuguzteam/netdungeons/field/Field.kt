package com.tuguzteam.netdungeons.field

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.assets.AssetManager
import com.tuguzteam.netdungeons.assets.TextureAsset
import kotlin.random.asKotlinRandom

class Field(val side: UInt, assetManager: AssetManager) : Disposable, Iterable<Cell> {
    private val woods = arrayListOf(
        assetManager[TextureAsset.Wood]!!,
        assetManager[TextureAsset.Wood1]!!,
    )

    init {
        if (side % 2u == 0u) {
            throw IllegalArgumentException("Side of field must be positive and odd: $side given")
        }
    }

    private val cells = Array((side * side).toInt()) { i ->
        val wood = woods.random(MathUtils.random.asKotlinRandom())
        Cell(
            position = ImmutableVector3(
                x = ((i / side.toInt() - side.toInt() / 2) * wood.width).toFloat(),
                y = 0f,
                z = ((i % side.toInt() - side.toInt() / 2) * wood.height).toFloat(),
            ), texture = wood,
        )
    }

    operator fun get(i: UInt, j: UInt) = cells[(i * side + j).toInt()]

    override fun iterator() = cells.iterator()

    override fun dispose() {
        forEach { cell -> cell.dispose() }
    }
}
