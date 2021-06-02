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

    val size = 11u
    val matrix = Generator.generate(size, 300u)
    private val rooms = ArrayList<Room>(100)

    init {
        val rectangles = arrayListOf<Rectangle>()
        matrix.forEachIndexed { i, list ->
            list.forEachIndexed { j, tile ->
                val notContains = rectangles.none { it.contains(i, j) }
                if (tile == TileType.Room && notContains) {
                    var iTemp = i
                    do {
                        iTemp++
                    } while (iTemp.toUInt() < size && matrix[iTemp][j] == TileType.Room)
                    val width = (iTemp - i).toUInt()

                    var jTemp = j
                    do {
                        jTemp++
                    } while (jTemp.toUInt() < size && list[jTemp] == TileType.Room)
                    val height = (jTemp - j).toUInt()

                    rectangles += Rectangle(x = i, y = j, width.toInt(), height.toInt())
                }
            }
        }
        rooms += rectangles.map {
            val x = it.x + it.width / 2f
            val z = it.y + it.height / 2f
            Box(
                position = immutableVec3(x = x, z = z), type = Type.Slum,
                walls = setOf(Direction.Forward, Direction.Back, Direction.Right, Direction.Left),
                assetManager = gameScreen.assetManager,
                width = it.width.toUInt(), length = it.height.toUInt(), height = 2u,
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
