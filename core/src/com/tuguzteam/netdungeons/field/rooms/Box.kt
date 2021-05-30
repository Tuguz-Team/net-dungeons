package com.tuguzteam.netdungeons.field.rooms

import com.badlogic.gdx.math.collision.BoundingBox
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.Loader.Companion.random
import com.tuguzteam.netdungeons.assets.AssetManager
import com.tuguzteam.netdungeons.field.*
import com.tuguzteam.netdungeons.immutableVec3
import ktx.math.vec3

@Suppress("unused")
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
        require(width > 0u) { "width must be positive" }
        require(length > 0u) { "length must be positive" }
        require(height > 0u) { "height must be positive" }
    }

    val cells = Array((width * length).toInt()) { index ->
        val asset = Field.cells.random(random)
        val texture = assetManager[asset]!!
        val i = index % width.toInt()
        val j = index / width.toInt()
        Cell(
            position = immutableVec3(
                x = (i - (width.toInt() - 1) / 2f) * Cell.width.toInt(),
                z = (j - (length.toInt() - 1) / 2f) * Cell.length.toInt(),
            ),
            texture,
        )
    }

    val walls: Array<Wall>
    init {
        val mutWalls = mutableListOf<Wall>()
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
                        x = -Cell.width.toInt() * (index / height.toInt() + 1 - (width.toInt() + 1) / 2f),
                        y = Wall.height.toInt() * (index % height.toInt() + 0.5f),
                        z = (length * Cell.length).toInt() / 2f,
                    )
                    Direction.Back -> immutableVec3(
                        x = Cell.width.toInt() * (index / height.toInt() + 1 - (width.toInt() + 1) / 2f),
                        y = Wall.height.toInt() * (index % height.toInt() + 0.5f),
                        z = -(length * Cell.length).toInt() / 2f,
                    )
                    Direction.Left -> immutableVec3(
                        x = (width * Cell.width).toInt() / 2f,
                        y = Wall.height.toInt() * (index % height.toInt() + 0.5f),
                        z = -Cell.length.toInt() * (index / height.toInt() + 1 - (length.toInt() + 1) / 2f),
                    )
                    Direction.Right -> immutableVec3(
                        x = -(width * Cell.width).toInt() / 2f,
                        y = Wall.height.toInt() * (index % height.toInt() + 0.5f),
                        z = Cell.length.toInt() * (index / height.toInt() + 1 - (length.toInt() + 1) / 2f),
                    )
                }
                mutWalls += Wall(wallPosition, texture, direction.inverse())
            }
        }
        this.walls = mutWalls.toTypedArray()
    }

    val forwardWall = this.walls.filterTo(ArrayList((width * height).toInt())) { wall ->
        wall.direction == Direction.Back
    }
    val backWall = this.walls.filterTo(ArrayList((width * height).toInt())) { wall ->
        wall.direction == Direction.Forward
    }
    val leftWall = this.walls.filterTo(ArrayList((length * height).toInt())) { wall ->
        wall.direction == Direction.Right
    }
    val rightWall = this.walls.filterTo(ArrayList((length * height).toInt())) { wall ->
        wall.direction == Direction.Left
    }

    override val boundingBox = BoundingBox(
        vec3(
            x = -(Cell.width * width).toInt() / 2f,
            z = -(Cell.length * length).toInt() / 2f,
        ),
        vec3(
            x = (Cell.width * width).toInt() / 2f,
            z = (Cell.length * length).toInt() / 2f,
        ),
    )

    override fun iterator() = (cells.asSequence() + walls.asSequence()).iterator()
}
