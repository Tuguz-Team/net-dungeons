package com.tuguzteam.netdungeons.field.generator

import com.badlogic.gdx.math.Rectangle
import com.tuguzteam.netdungeons.Loader
import ktx.log.debug
import kotlin.random.nextUInt

object Generator {
    private val random = Loader.random

    fun generate(width: UInt, height: UInt, roomCount: UInt): List<List<TileType?>> {
        val matrix = List(width.toInt()) { MutableList<TileType?>(height.toInt()) { null } }
        // TODO: level generation
        // Create rooms
        val rooms = arrayListOf<Rectangle>().apply { ensureCapacity(roomCount.toInt()) }
        while (rooms.size < roomCount.toInt()) {
            val room = Rectangle().apply {
                val uWidth = random.nextUInt(from = 2u, until = width / 2u)
                val uHeight = random.nextUInt(from = 2u, until = height / 2u)
                this.width = uWidth.toFloat()
                this.height = uHeight.toFloat()
                this.x = random.nextUInt(from = uWidth, until = width - uWidth).toFloat()
                this.y = random.nextUInt(from = uHeight, until = height - uHeight).toFloat()
            }
            if (rooms.none(room::overlaps)) {
                rooms += room
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
        matrix.forEachIndexed { i, list ->
            val stringBuilder = StringBuilder()
            list.forEach {
                stringBuilder.append(
                    when (it) {
                        TileType.Floor -> 'f'
                        TileType.Wall -> 'w'
                        TileType.Chest -> 'c'
                        null -> ' '
                    }
                )
            }
            Loader.logger.debug { stringBuilder.append("| $i").toString() }
        }
        return matrix
    }
}
