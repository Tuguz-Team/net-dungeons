package com.tuguzteam.netdungeons.field.rooms

import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.Loader.Companion.random
import com.tuguzteam.netdungeons.assets.AssetManager
import com.tuguzteam.netdungeons.field.Cell
import com.tuguzteam.netdungeons.field.Field
import com.tuguzteam.netdungeons.immutableVec3

class Corridor(
    position: ImmutableVector3,
    assetManager: AssetManager,
    private val width: UInt,
    private val length: UInt,
) : Room(position) {

    val cells = Array((width * length).toInt()) { index ->
        val asset = Field.cells.random(random)
        val cell = assetManager[asset]!!
        val i = index % width.toInt()
        val j = index / width.toInt()
        Cell(
            position = immutableVec3(
                x = (i - width.toInt() / 2f + 0.5f) * cell.width,
                z = (j - length.toInt() / 2f + 0.5f) * cell.height,
            ),
            texture = cell,
        )
    }

    override fun iterator() = cells.iterator()
}
