package com.tuguzteam.netdungeons.field.generator

import com.badlogic.gdx.math.Vector2
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.plusAssign
import ktx.log.debug
import ktx.math.ImmutableVector2
import ktx.math.toImmutable
import kotlin.math.absoluteValue

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
            val size = random.nextInt(from = 2, until = width.toInt() / 8 + 4)
            var uWidth = size
            var uHeight = size
            val offset = random.nextInt(until = 1 + size / 4)
            if (random.nextBoolean()) {
                uWidth += offset
            } else {
                uHeight += offset
            }
            uWidth = uWidth or 1
            uHeight = uHeight or 1

            val room = Rectangle(
                x = random.nextInt(from = uWidth - 1, until = width.toInt() - uWidth - 1) or 1,
                y = random.nextInt(from = uHeight - 1, until = width.toInt() - uHeight - 1) or 1,
                width = uWidth,
                height = uHeight,
            ).apply {
                x -= 1
                y -= 1
                this.width += 2
                this.height += 2
            }
            if (rooms.none(room::overlaps)) {
                rooms += room
                room.apply {
                    x += 1
                    y += 1
                    this.width -= 2
                    this.height -= 2
                }
            }
        }
        // Fill the matrix with rooms and walls
        rooms.forEach { room ->
            val x = room.x
            val y = room.y
            val iWidth = room.width
            val iHeight = room.height
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
            val backtrace = mutableListOf(Point(i, j))
            while (backtrace.isNotEmpty()) {
                val point = backtrace.first()
                val x = point.x
                val y = point.y
                matrix[x][y] = TileType.Maze
                val neighbors = mutableListOf<Point>().apply {
                    if (x - 2 >= 0 && matrix[x - 2][y] == TileType.Wall) {
                        this += Point(x - 2, y)
                    }
                    if (x + 2 < width.toInt() && matrix[x + 2][y] == TileType.Wall) {
                        this += Point(x + 2, y)
                    }
                    if (y - 2 >= 0 && matrix[x][y - 2] == TileType.Wall) {
                        this += Point(x, y - 2)
                    }
                    if (y + 2 < width.toInt() && matrix[x][y + 2] == TileType.Wall) {
                        this += Point(x, y + 2)
                    }
                }
                if (neighbors.isNotEmpty()) {
                    val neighbor = neighbors.random(random)
                    matrix[neighbor.x][neighbor.y] = TileType.Maze
                    val offsetI = (x + neighbor.x + 1) / 2
                    val offsetJ = (y + neighbor.y + 1) / 2
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
        val verticalDoors = mutableListOf<Point>()
        val horizontalDoors = mutableListOf<Point>()
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
                        horizontalDoors += Point(i, j)
                    }
                    // Vertical door candidates
                    val left = matrix[i][j - 1]
                    val right = matrix[i][j + 1]
                    val leftNotWall = left != TileType.Wall && left != TileType.Door
                    val rightNotWall = right != TileType.Wall && right != TileType.Door
                    val leftRightRooms = left == TileType.Room && left == right
                    if (leftNotWall && rightNotWall && left != right || leftRightRooms) {
                        verticalDoors += Point(i, j)
                    }
                }
            }
        }
        // 2) Get horizontal walls from candidates
        while (horizontalDoors.isNotEmpty()) {
            val wall = kotlin.run {
                val first = horizontalDoors.first()
                val row = horizontalDoors.asSequence().filter { first.x == it.x }
                val list = mutableListOf(first)
                var temp = first
                row.forEach {
                    if (temp != it && (temp.y - it.y).absoluteValue < 2) {
                        list += it
                        temp = it
                    }
                }
                list
            }
            val door = wall.random(random)
            matrix[door.x][door.y] = TileType.Door
            horizontalDoors.removeAll(wall)
        }
        // 3) Get vertical walls from candidates
        while (verticalDoors.isNotEmpty()) {
            val wall = kotlin.run {
                val first = verticalDoors.first()
                val row = verticalDoors.asSequence().filter { first.y == it.y }
                val list = mutableListOf(first)
                var temp = first
                row.forEach {
                    if (temp != it && (temp.x - it.x).absoluteValue < 2) {
                        list += it
                        temp = it
                    }
                }
                list
            }
            val door = wall.random(random)
            matrix[door.x][door.y] = TileType.Door
            verticalDoors.removeAll(wall)
        }
        // Remove dead ends from the maze
        matrix.forEachIndexed { x, list ->
            list.forEachIndexed inner@ { y, tile ->
                if (tile != TileType.Maze) return@inner
                var nextX = x
                var nextY = y
                fun neighbors() = mutableListOf<Point>().apply {
                    val up = matrix[nextX - 1][nextY]
                    if (up == TileType.Maze || up == TileType.Door) {
                        this += Point(nextX - 1, nextY)
                    }
                    val down = matrix[nextX + 1][nextY]
                    if (down == TileType.Maze || down == TileType.Door) {
                        this += Point(nextX + 1, nextY)
                    }
                    val left = matrix[nextX][nextY - 1]
                    if (left == TileType.Maze || left == TileType.Door) {
                        this += Point(nextX, nextY - 1)
                    }
                    val right = matrix[nextX][nextY + 1]
                    if (right == TileType.Maze || right == TileType.Door) {
                        this += Point(nextX, nextY + 1)
                    }
                }
                var neighbors = neighbors()
                while (neighbors.size == 1) {
                    matrix[nextX][nextY] = TileType.Wall
                    val neighbor = neighbors.first()
                    nextX = neighbor.x
                    nextY = neighbor.y
                    neighbors = neighbors()
                }
            }
        }
        // Print to console
        printMatrix()
        return matrix
    }
}

data class Point(var x: Int, var y: Int)

data class Rectangle(var x: Int, var y: Int, var width: Int, var height: Int) {
    fun overlaps(rectangle: Rectangle) =
        x < rectangle.x + rectangle.width && x + width > rectangle.x &&
                y < rectangle.y + rectangle.height && y + height > rectangle.y

    fun contains(x: Int, y: Int) =
        this.x <= x && this.x + width >= x && this.y <= y && this.y + height >= y

    operator fun contains(vector2: Pair<Int, Int>) = contains(vector2.first, vector2.second)

    operator fun contains(vector2: ImmutableVector2) =
        x.toFloat() <= vector2.x && (x + width).toFloat() >= vector2.x &&
                y.toFloat() <= vector2.y && (y + height).toFloat() >= vector2.y

    operator fun contains(vector2: Vector2) = contains(vector2.toImmutable())
}
