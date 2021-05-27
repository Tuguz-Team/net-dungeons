package com.tuguzteam.netdungeons.field

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.assets.AssetManager
import com.tuguzteam.netdungeons.assets.TextureAsset
import com.tuguzteam.netdungeons.immutableVec3
import com.tuguzteam.netdungeons.objects.GameObject
import kotlin.random.asKotlinRandom

class Field(val side: UInt, assetManager: AssetManager) : Disposable, Iterable<GameObject> {
    companion object {
        val random = MathUtils.random.asKotlinRandom()

        val cells = arrayOf(TextureAsset.Wood, TextureAsset.Wood1)
        val walls = arrayOf(TextureAsset.Wood)
    }

    init {
        if (side % 2u == 0u) {
            throw IllegalArgumentException("Side of field must be positive and odd: $side given")
        }
    }

    val cells = Array((side * side).toInt()) { i ->
        val asset = Companion.cells.random(random)
        val cell = assetManager[asset]!!
        Cell(
            position = immutableVec3(
                x = (i / side.toInt() - side.toInt() / 2) * cell.width.toFloat(),
                z = (i % side.toInt() - side.toInt() / 2) * cell.height.toFloat(),
            ),
            texture = cell,
        )
    }
    val walls = Array(1) {
        val asset = Companion.walls.random(random)
        val wall = assetManager[asset]!!
        Wall(
            position = immutableVec3(y = wall.height / 2f),
            direction = Direction.Left,
            texture = wall,
        )
    }

    override fun iterator() = (cells.asSequence() + walls.asSequence()).iterator()

    override fun dispose() {
        forEach(GameObject::dispose)
    }
}
