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
        fun printMatrix() {
            val stringBuilder = StringBuilder()
            stringBuilder.append(' ')
            matrix.forEach { list ->
                stringBuilder.append("\n|")
                list.forEach {
                    stringBuilder.append(
                        when (it) {
                            TileType.Room -> 'r'
                            TileType.Maze -> '•'
                            TileType.Wall -> 'W'
                            TileType.Door -> 'd'
                            null -> ' '
                        }
                    )
                }
                stringBuilder.append('|')
            }
            Loader.logger.debug { stringBuilder.toString() }
        }
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
        // Fill the matrix with rooms and walls
        rooms.forEach { room ->
            val x = room.x.toInt()
            val y = room.y.toInt()
            val iWidth = room.width.toInt()
            val iHeight = room.height.toInt()
            (x - 1..x + iWidth).forEach { i ->
                (y - 1..y + iHeight).forEach inner@ { j ->
                    if (i == x - 1 || j == y - 1 || i == x + iWidth || j == y + iHeight) {
                        matrix[i][j] = TileType.Wall
                        return@inner
                    }
                    matrix[i][j] = TileType.Room
                }
            }
        }
        // TODO: maze generation...
        // Create mazes
        matrix.forEachIndexed { i, list ->
            list.forEachIndexed { j, tile ->
                if (tile == null) {
                    // Start maze generator
                    val backtrace = mutableListOf(i to j)
                    while (backtrace.isNotEmpty()) {
                        val (i, j) = backtrace.removeAt(0)
                        matrix[i][j] = TileType.Maze
                        val neighbors = mutableListOf<Pair<Int, Int>>().apply {
                            if (i - 1 >= 0 && matrix[i - 1][j] == null) {
                                this += i - 1 to j
                            }
                            if (i + 1 < width.toInt() && matrix[i + 1][j] == null) {
                                this += i + 1 to j
                            }
                            if (j - 1 >= 0 && matrix[i][j - 1] == null) {
                                this += i to j - 1
                            }
                            if (j + 1 < height.toInt() && matrix[i][j + 1] == null) {
                                this += i to j + 1
                            }
                        }
                        if (neighbors.isNotEmpty()) {
                            val neighbor = neighbors.random(random)
                            matrix[neighbor.first][neighbor.second] = TileType.Maze
                            // TODO place walls
                            if (i - 1 >= 0 && matrix[i - 1][j] == null) {
                                matrix[i - 1][j] = TileType.Wall
                            }
                            if (i + 1 < width.toInt() && matrix[i + 1][j] == null) {
                                matrix[i + 1][j] = TileType.Wall
                            }
                            if (j - 1 >= 0 && matrix[i][j - 1] == null) {
                                matrix[i][j - 1] = TileType.Wall
                            }
                            if (j + 1 < height.toInt() && matrix[i][j + 1] == null) {
                                matrix[i][j + 1] = TileType.Wall
                            }
                            backtrace.add(0, neighbor)
                        }
                    }
                }
            }
        }
        // Print into console
        printMatrix()
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
