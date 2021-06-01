package com.tuguzteam.netdungeons.field

import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.assets.TextureAsset
import com.tuguzteam.netdungeons.field.generator.Generator
import com.tuguzteam.netdungeons.field.generator.Rectangle
import com.tuguzteam.netdungeons.field.generator.TileType
import com.tuguzteam.netdungeons.field.rooms.Box
import com.tuguzteam.netdungeons.field.rooms.MultiRoom
import com.tuguzteam.netdungeons.field.rooms.Room
import com.tuguzteam.netdungeons.immutableVec3
import com.tuguzteam.netdungeons.objects.GameObject
import com.tuguzteam.netdungeons.screens.GameScreen
import ktx.assets.dispose

class Field(gameScreen: GameScreen) : Disposable, Iterable<GameObject> {
    companion object {
        val cells = listOf(TextureAsset.Wood, TextureAsset.Wood1)
        val walls = listOf(TextureAsset.Wood)
    }

    private val rooms = ArrayList<Room>(100)

    init {
        val matrixWidth = 30u
        val matrixHeight = 30u
        val matrix = Generator.generate(matrixWidth, matrixHeight, 300u)
        val rectangles = arrayListOf<Rectangle>()
        matrix.forEachIndexed { i, list ->
            list.forEachIndexed { j, tile ->
                val notContains = rectangles.none { it.contains(i.toUInt(), j.toUInt()) }
                if (tile == TileType.Room && notContains) {
                    var iTemp = i
                    do {
                        iTemp++
                    } while (iTemp.toUInt() < matrixWidth && matrix[iTemp][j] == TileType.Room)
                    val width = (iTemp - i).toUInt()

                    var jTemp = j
                    do {
                        jTemp++
                    } while (jTemp.toUInt() < matrixHeight && list[jTemp] == TileType.Room)
                    val height = (jTemp - j).toUInt()

                    rectangles += Rectangle(x = i.toUInt(), y = j.toUInt(), width, height)
                }
            }
        }
        rooms += rectangles.map {
            val x = it.x.toFloat() - (matrixWidth.toInt() - it.width.toInt()) / 2f + 0.5f
            val z = it.y.toFloat() - (matrixHeight.toInt() - it.height.toInt()) / 2f + 0.5f
            Box(
                position = immutableVec3(x = x, z = z), type = Type.Slum,
                walls = setOf(Direction.Forward, Direction.Back, Direction.Right, Direction.Left),
                assetManager = gameScreen.assetManager,
                width = it.width, length = it.height, height = 2u,
            )
        }
    }

    override fun iterator() = rooms.asSequence().map(::roomObjects).flatten().iterator()

    private fun roomObjects(room: Room) = when (room) {
        is Box -> room.asSequence()
        is MultiRoom -> room.asSequence().map { (it as Room).asSequence() }.flatten()
    }

    override fun dispose() {
        rooms.dispose()
    }
}
