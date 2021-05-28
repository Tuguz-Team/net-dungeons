package com.tuguzteam.netdungeons.field

import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.Loader.Companion.random
import com.tuguzteam.netdungeons.assets.AssetManager
import com.tuguzteam.netdungeons.assets.TextureAsset
import com.tuguzteam.netdungeons.immutableVec3
import com.tuguzteam.netdungeons.objects.GameObject

class Field(val side: UInt, assetManager: AssetManager) : Disposable, Iterable<GameObject> {
    companion object {
        val cells = arrayOf(TextureAsset.Wood, TextureAsset.Wood1)
        val walls = arrayOf(TextureAsset.Wood)
    }

    init {
        require(side % 2u == 1u) { "side of field must be positive and odd: $side given" }
    }

    val cells = Array((side * side).toInt()) { i ->
        val asset = Companion.cells.random(random)
        val texture = assetManager[asset]!!
        Cell(
            position = immutableVec3(
                x = (i / side.toInt() - side.toInt() / 2) * texture.width.toFloat(),
                z = (i % side.toInt() - side.toInt() / 2) * texture.height.toFloat(),
            ),
            texture,
        )
    }
    val walls = Array(1) {
        val asset = Companion.walls.random(random)
        val texture = assetManager[asset]!!
        Wall(
            position = immutableVec3(y = texture.height / 2f),
            texture, direction = Direction.Left,
        )
    }

    override fun iterator() = (cells.asSequence() + walls.asSequence()).iterator()

    override fun dispose() {
        forEach(GameObject::dispose)
    }
}
