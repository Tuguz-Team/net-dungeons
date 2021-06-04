package com.tuguzteam.netdungeons.field

import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.assets.TextureAsset
import com.tuguzteam.netdungeons.field.generator.Generator
import com.tuguzteam.netdungeons.field.generator.TileType
import com.tuguzteam.netdungeons.field.tile.Floor
import com.tuguzteam.netdungeons.field.tile.Tile
import com.tuguzteam.netdungeons.field.tile.Wall
import com.tuguzteam.netdungeons.immutableGridPoint2
import com.tuguzteam.netdungeons.screens.GameScreen
import ktx.assets.dispose

class Field(gameScreen: GameScreen) : Disposable, Iterable<Tile> {
    companion object {
        val random = Loader.random

        val floors = listOf(TextureAsset.Wood, TextureAsset.Wood1)
        val walls = listOf(TextureAsset.Wood)
    }

    val size = 11u
    private val matrix = Generator.generate(size, 300u)

    private val _tiles = mutableListOf<Tile>()
    val tiles: List<Tile> = _tiles

    init {
        matrix.forEachIndexed { x, list ->
            list.forEachIndexed { y, tile ->
                when (tile) {
                    TileType.Wall -> {
                        val position = immutableGridPoint2(x, y)
                        val asset = walls.random(random)
                        val texture = gameScreen.assetManager[asset]!!

                        val directions = mutableSetOf<Direction>()
                        if (x - 1 < 0 || matrix[x - 1][y] != TileType.Wall) {
                            directions += Direction.Right
                        }
                        if (x + 1 >= size.toInt() || matrix[x + 1][y] != TileType.Wall) {
                            directions += Direction.Left
                        }
                        if (y - 1 < 0 || matrix[x][y - 1] != TileType.Wall) {
                            directions += Direction.Back
                        }
                        if (y + 1 >= size.toInt() || matrix[x][y + 1] != TileType.Wall) {
                            directions += Direction.Forward
                        }

                        _tiles += Wall(position, 1u, texture, directions)
                    }
                    else -> {
                        val position = immutableGridPoint2(x, y)
                        val asset = floors.random(random)
                        val texture = gameScreen.assetManager[asset]!!
                        _tiles += Floor(position, texture)
                    }
                }
            }
        }
    }

    override fun iterator() = tiles.asSequence().iterator()

    override fun dispose() {
        tiles.dispose()
    }
}
