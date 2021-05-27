package com.tuguzteam.netdungeons.field

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.assets.AssetManager
import com.tuguzteam.netdungeons.assets.TextureAsset
import com.tuguzteam.netdungeons.immutableVec3
import com.tuguzteam.netdungeons.objects.ModelObject
import kotlin.random.asKotlinRandom

class Field(val side: UInt, assetManager: AssetManager) : Disposable, Iterable<ModelObject> {
    private val random = MathUtils.random.asKotlinRandom()
    private val cellModels = arrayListOf(
        assetManager[TextureAsset.Wood]!!,
        assetManager[TextureAsset.Wood1]!!,
    )
    private val wallModels = arrayListOf(
        assetManager[TextureAsset.Wood]!!,
    )

    init {
        if (side % 2u == 0u) {
            throw IllegalArgumentException("Side of field must be positive and odd: $side given")
        }
    }

    private val cells = Array((side * side).toInt()) { i ->
        val wood = cellModels.random(random)
        Cell(
            position = immutableVec3(
                x = ((i / side.toInt() - side.toInt() / 2) * wood.width).toFloat(),
                z = ((i % side.toInt() - side.toInt() / 2) * wood.height).toFloat(),
            ),
            texture = wood,
        )
    }
    private val walls = Array(4) {
        val wall = wallModels.random(random)
        Wall(
            position = immutableVec3(y = wall.height / 2f),
            direction = Direction.Left,
            texture = wall,
        )
    }

    operator fun get(i: UInt, j: UInt) = cells[(i * side + j).toInt()]

    override fun iterator() = (cells.asSequence() + walls.asSequence()).iterator()

    override fun dispose() {
        forEach(ModelObject::dispose)
    }
}
