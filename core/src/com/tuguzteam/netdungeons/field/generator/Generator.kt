package com.tuguzteam.netdungeons.field.generator

import com.badlogic.gdx.math.Vector2
import com.tuguzteam.netdungeons.Loader
import ktx.log.debug
import ktx.math.ImmutableVector2
import ktx.math.toImmutable
import kotlin.random.nextUInt

object Generator {
    private val random = Loader.random

    fun generate(width: UInt, height: UInt, attempts: UInt): List<List<TileType?>> {
        val matrix = List(width.toInt()) { MutableList<TileType?>(height.toInt()) { null } }
        // TODO: level generation
        // Create rooms
        val mid = (width + height) / 2u
        val rooms = arrayListOf<Rectangle>()
        repeat(attempts.toInt()) {
            val size = random.nextUInt(from = 2u, until = mid / 8u + 4u)
            var uWidth = size
            var uHeight = size
            val offset = random.nextUInt(until = 1u + size / 2u)
            if (random.nextBoolean()) {
                uWidth += offset
            } else {
                uHeight += offset
            }

            val room = Rectangle(
                x = random.nextUInt(from = uWidth, until = width - uWidth),
                y = random.nextUInt(from = uHeight, until = height - uHeight),
                width = uWidth,
                height = uHeight,
            ).apply {
                x -= 1u
                y -= 1u
                this.width += 2u
                this.height += 2u
            }
            if (rooms.none(room::overlaps)) {
                rooms += room
                room.apply {
                    x += 1u
                    y += 1u
                    this.width -= 2u
                    this.height -= 2u
                }
            }
        }
        // Fill the matrix
        rooms.forEach { room ->
            val x = room.x.toInt()
            val y = room.y.toInt()
            val iWidth = room.width.toInt()
            val iHeight = room.height.toInt()
            (x until x + iWidth).forEach { i ->
                (y until y + iHeight).forEach { j ->
                    matrix[i][j] = TileType.Floor
                }
            }
        }
        // Print into console
        val stringBuilder = StringBuilder()
        stringBuilder.append(' ')
        matrix.forEach { list ->
            stringBuilder.append("\n|")
            list.forEach {
                stringBuilder.append(
                    when (it) {
                        TileType.Floor -> 'f'
                        TileType.Wall -> 'w'
                        TileType.Chest -> 'c'
                        null -> '•'
                    }
                )
            }
            stringBuilder.append('|')
        }
        Loader.logger.debug { stringBuilder.toString() }
        return matrix
    }
}

data class Rectangle(var x: UInt, var y: UInt, var width: UInt, var height: UInt) {
    fun overlaps(rectangle: Rectangle) =
        x < rectangle.x + rectangle.width && x + width > rectangle.x &&
                y < rectangle.y + rectangle.height && y + height > rectangle.y

    fun contains(x: UInt, y: UInt) =
        this.x <= x && this.x + width >= x && this.y <= y && this.y + height >= y

    operator fun contains(vector2: Pair<UInt, UInt>) = contains(vector2.first, vector2.second)

    operator fun contains(vector2: ImmutableVector2) =
        x.toFloat() <= vector2.x && (x + width).toFloat() >= vector2.x &&
                y.toFloat() <= vector2.y && (y + height).toFloat() >= vector2.y

    operator fun contains(vector2: Vector2) = contains(vector2.toImmutable())
}
