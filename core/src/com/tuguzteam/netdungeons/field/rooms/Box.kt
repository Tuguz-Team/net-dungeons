package com.tuguzteam.netdungeons.field.rooms

import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.Loader.Companion.random
import com.tuguzteam.netdungeons.assets.AssetManager
import com.tuguzteam.netdungeons.field.*
import com.tuguzteam.netdungeons.immutableVec3

class Box(
    position: ImmutableVector3,
    type: Type,
    walls: Set<Direction>,
    assetManager: AssetManager,
    val width: UInt,
    val length: UInt,
) : Room(position, type) {

    init {
        require(width > 0u && length > 0u) {
            "width and height must be positive: $width and $length given"
        }
    }

    val cells = Array((width * length).toInt()) { index ->
        val asset = Field.cells.random(random)
        val texture = assetManager[asset]!!
        val i = index % width.toInt()
        val j = index / width.toInt()
        Cell(
            position = immutableVec3(
                x = (i - (width.toInt() - 1) / 2f) * texture.width,
                z = (j - (length.toInt() - 1) / 2f) * texture.height,
            ),
            texture,
        )
    }
    val walls: Array<Wall>

    init {
        val mutWalls = arrayListOf<Wall>()
        walls.forEach { direction ->
            val end = when (direction) {
                Direction.Forward, Direction.Back -> width
                else -> length
            }
            for (index in 1..end.toInt()) {
                val asset = Field.walls.random(random)
                val texture = assetManager[asset]!!
                val wallPosition = when (direction) {
                    Direction.Forward -> immutableVec3(
                        x = -(index - (width.toInt() + 1) / 2f) * cells[0].width,
                        y = texture.height / 2f,
                        z = length.toInt() * cells[0].height / 2f,
                    )
                    Direction.Back -> immutableVec3(
                        x = (index - (width.toInt() + 1) / 2f) * cells[0].width,
                        y = texture.height / 2f,
                        z = -length.toInt() * cells[0].height / 2f,
                    )
                    Direction.Left -> immutableVec3(
                        x = width.toInt() * cells[0].width / 2f,
                        y = texture.height / 2f,
                        z = -(index - (length.toInt() + 1) / 2f) * cells[0].height,
                    )
                    Direction.Right -> immutableVec3(
                        x = -width.toInt() * cells[0].width / 2f,
                        y = texture.height / 2f,
                        z = (index - (length.toInt() + 1) / 2f) * cells[0].height,
                    )
                }
                mutWalls += Wall(wallPosition, texture, direction.inverse())
            }
        }
        this.walls = mutWalls.toTypedArray()
        println(this.walls.size)
    }

    override fun iterator() = (cells.asSequence() + walls.asSequence()).iterator()
}
