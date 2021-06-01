package com.tuguzteam.netdungeons.field.generator

import com.badlogic.gdx.math.Vector2
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.plusAssign
import ktx.log.debug
import ktx.math.ImmutableVector2
import ktx.math.toImmutable
import kotlin.math.absoluteValue
import kotlin.random.nextUInt

object Generator {
    private val random = Loader.random

    fun generate(width: UInt, attempts: UInt): List<List<TileType>> {
        require(width > 10u) { "width must be greater than 10: $width given" }
        require(width % 2u == 1u) { "width must be odd: $width given" }
        val matrix = List(width.toInt()) { MutableList(width.toInt()) { TileType.Wall } }
        fun printMatrix() {
            val stringBuilder = StringBuilder().append(' ')
            matrix.forEach { list ->
                stringBuilder += "\n|"
                list.forEach { tile ->
                    stringBuilder += when (tile) {
                        TileType.Room -> 'r'
                        TileType.Maze -> '•'
                        TileType.Wall -> 'W'
                        TileType.Door -> 'd'
                    }
                }
                stringBuilder += '|'
            }
            Loader.logger.debug { stringBuilder.toString() }
        }
        // Create rooms
        val rooms = mutableListOf<Rectangle>()
        repeat(attempts.toInt()) {
            val size = random.nextUInt(from = 2u, until = width / 8u + 4u)
            var uWidth = size
            var uHeight = size
            val offset = random.nextUInt(until = 1u + size / 4u)
            if (random.nextBoolean()) {
                uWidth += offset
            } else {
                uHeight += offset
            }
            uWidth = uWidth or 1u
            uHeight = uHeight or 1u

            val room = Rectangle(
                x = random.nextUInt(from = uWidth - 1u, until = width - uWidth - 1u) or 1u,
                y = random.nextUInt(from = uHeight - 1u, until = width - uHeight - 1u) or 1u,
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
        // Create maze
        fun generateMaze(i: Int, j: Int) {
            val backtrace = mutableListOf(i to j)
            while (backtrace.isNotEmpty()) {
                val (x, y) = backtrace[0]
                matrix[x][y] = TileType.Maze
                val neighbors = mutableListOf<Pair<Int, Int>>().apply {
                    if (x - 2 >= 0 && matrix[x - 2][y] == TileType.Wall) {
                        this += x - 2 to y
                    }
                    if (x + 2 < width.toInt() && matrix[x + 2][y] == TileType.Wall) {
                        this += x + 2 to y
                    }
                    if (y - 2 >= 0 && matrix[x][y - 2] == TileType.Wall) {
                        this += x to y - 2
                    }
                    if (y + 2 < width.toInt() && matrix[x][y + 2] == TileType.Wall) {
                        this += x to y + 2
                    }
                }
                if (neighbors.isNotEmpty()) {
                    val neighbor = neighbors.random(random)
                    matrix[neighbor.first][neighbor.second] = TileType.Maze
                    val offsetI = (x + neighbor.first + 1) / 2
                    val offsetJ = (y + neighbor.second + 1) / 2
                    matrix[offsetI][offsetJ] = TileType.Maze
                    backtrace.add(0, neighbor)
                } else {
                    backtrace.removeAt(0)
                }
            }
        }
        for (i in 1 until width.toInt() step 2) {
            for (j in 1 until width.toInt() step 2) {
                if (matrix[i][j] == TileType.Wall) generateMaze(i, j)
            }
        }
        // Generate doors for rooms
        // 1) Find candidates to doors
        val verticalDoors = mutableListOf<Pair<Int, Int>>()
        val horizontalDoors = mutableListOf<Pair<Int, Int>>()
        matrix.forEachIndexed { i, list ->
            list.forEachIndexed { j, tile ->
                val notOnBorder = i > 0 && i + 1 < width.toInt()
                        && j > 0 && j + 1 < width.toInt()
                if (tile == TileType.Wall && notOnBorder) {
                    // Horizontal door candidates
                    val up = matrix[i - 1][j]
                    val down = matrix[i + 1][j]
                    val upNotWall = up != TileType.Wall && up != TileType.Door
                    val downNotWall = down != TileType.Wall && down != TileType.Door
                    val upDownRooms = up == TileType.Room && up == down
                    if (upNotWall && downNotWall && up != down || upDownRooms) {
                        horizontalDoors += i to j
                    }
                    // Vertical door candidates
                    val left = matrix[i][j - 1]
                    val right = matrix[i][j + 1]
                    val leftNotWall = left != TileType.Wall && left != TileType.Door
                    val rightNotWall = right != TileType.Wall && right != TileType.Door
                    val leftRightRooms = left == TileType.Room && left == right
                    if (leftNotWall && rightNotWall && left != right || leftRightRooms) {
                        verticalDoors += i to j
                    }
                }
            }
        }
        // 2) Get horizontal walls from candidates
        while (horizontalDoors.isNotEmpty()) {
            val wall = kotlin.run {
                val first = horizontalDoors.first()
                val row = horizontalDoors.asSequence().filter {
                    first.first == it.first
                }
                val list = mutableListOf(first)
                var temp = first
                row.forEach {
                    if (temp != it && (temp.second - it.second).absoluteValue < 2) {
                        list += it
                        temp = it
                    }
                }
                list
            }
            val door = wall.random(random)
            matrix[door.first][door.second] = TileType.Door
            horizontalDoors.removeAll(wall)
        }
        // 3) Get vertical walls from candidates
        while (verticalDoors.isNotEmpty()) {
            val wall = kotlin.run {
                val first = verticalDoors.first()
                val row = verticalDoors.asSequence().filter {
                    first.second == it.second
                }
                val list = mutableListOf(first)
                var temp = first
                row.forEach {
                    if (temp != it && (temp.first - it.first).absoluteValue < 2) {
                        list += it
                        temp = it
                    }
                }
                list
            }
            val door = wall.random(random)
            matrix[door.first][door.second] = TileType.Door
            verticalDoors.removeAll(wall)
        }
        // Remove dead ends from the maze
        matrix.forEachIndexed { x, list ->
            list.forEachIndexed inner@ { y, tile ->
                if (tile != TileType.Maze) return@inner
                var nextX = x
                var nextY = y
                fun neighbors() = mutableListOf<Pair<Int, Int>>().apply {
                    val up = matrix[nextX - 1][nextY]
                    if (up == TileType.Maze || up == TileType.Door) {
                        this += nextX - 1 to nextY
                    }
                    val down = matrix[nextX + 1][nextY]
                    if (down == TileType.Maze || down == TileType.Door) {
                        this += nextX + 1 to nextY
                    }
                    val left = matrix[nextX][nextY - 1]
                    if (left == TileType.Maze || left == TileType.Door) {
                        this += nextX to nextY - 1
                    }
                    val right = matrix[nextX][nextY + 1]
                    if (right == TileType.Maze || right == TileType.Door) {
                        this += nextX to nextY + 1
                    }
                }
                var neighbors = neighbors()
                while (neighbors.size == 1) {
                    matrix[nextX][nextY] = TileType.Wall
                    val neighbor = neighbors.first()
                    nextX = neighbor.first
                    nextY = neighbor.second
                    neighbors = neighbors()
                }
            }
        }
        // Print to console
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
