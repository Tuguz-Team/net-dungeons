package com.tuguzteam.netdungeons.field.rooms

import com.badlogic.gdx.math.collision.BoundingBox
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.Loader.Companion.random
import com.tuguzteam.netdungeons.assets.AssetManager
import com.tuguzteam.netdungeons.field.*
import com.tuguzteam.netdungeons.immutableVec3
import ktx.math.vec3

class Box(
    position: ImmutableVector3,
    type: Type,
    walls: Set<Direction>,
    assetManager: AssetManager,
    val width: UInt,
    val length: UInt,
    val height: UInt,
) : Room(position, type) {

    init {
        require(width > 0u) { "width must be positive: $width given" }
        require(length > 0u) { "length must be positive: $length given" }
        require(height > 0u) { "height must be positive: $height given" }
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
            repeat((end * height).toInt()) { index ->
                val asset = Field.walls.random(random)
                val texture = assetManager[asset]!!
                val wallPosition = when (direction) {
                    Direction.Forward -> immutableVec3(
                        x = -(index / height.toInt() + 1 - (width.toInt() + 1) / 2f) * cells[0].width,
                        y = texture.height * (index % height.toInt() + 0.5f),
                        z = length.toInt() * cells[0].height / 2f,
                    )
                    Direction.Back -> immutableVec3(
                        x = (index / height.toInt() + 1 - (width.toInt() + 1) / 2f) * cells[0].width,
                        y = texture.height * (index % height.toInt() + 0.5f),
                        z = -length.toInt() * cells[0].height / 2f,
                    )
                    Direction.Left -> immutableVec3(
                        x = width.toInt() * cells[0].width / 2f,
                        y = texture.height * (index % height.toInt() + 0.5f),
                        z = -(index / height.toInt() + 1 - (length.toInt() + 1) / 2f) * cells[0].height,
                    )
                    Direction.Right -> immutableVec3(
                        x = -width.toInt() * cells[0].width / 2f,
                        y = texture.height * (index % height.toInt() + 0.5f),
                        z = (index / height.toInt() + 1 - (length.toInt() + 1) / 2f) * cells[0].height,
                    )
                }
                mutWalls += Wall(wallPosition, texture, direction.inverse())
            }
        }
        this.walls = mutWalls.toTypedArray()
    }

    override val boundingBox = BoundingBox(
        vec3(
            x = -cells[0].width * width.toInt() / 2f,
            z = -cells[0].height * length.toInt() / 2f,
        ),
        vec3(
            x = cells[0].width * width.toInt() / 2f,
            z = cells[0].height * length.toInt() / 2f,
        ),
    )

    override fun iterator() = (cells.asSequence() + walls.asSequence()).iterator()
}
