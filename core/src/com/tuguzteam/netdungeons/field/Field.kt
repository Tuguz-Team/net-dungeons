package com.tuguzteam.netdungeons.field

import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.ImmutableGridPoint2
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.assets.TextureAtlasAll
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

        private val floorAssets = listOf(
            TextureAtlasAll.Region.Wood,
            TextureAtlasAll.Region.Wood1,
        )
        private val wallAssets = listOf(
            TextureAtlasAll.Region.WallBrick,
            TextureAtlasAll.Region.WallBrick1,
            TextureAtlasAll.Region.WallBrick2,
        )
        private val doorAssets = listOf(
            TextureAtlasAll.Region.WallBrickDark,
        )
        private val mazeAssets = listOf(
            TextureAtlasAll.Region.WoodDark,
        )
        val assets = floorAssets + wallAssets + doorAssets + mazeAssets
    }

    val size = 31u

    private val _tiles = arrayListOf<Tile>()
    val tiles: List<Tile> = _tiles

    init {
        val matrix = Generator.generate(size, 300u)
        matrix.forEachIndexed { x, list ->
            list.forEachIndexed { y, tile ->
                _tiles += when (tile) {
                    TileType.Wall -> {
                        val position = immutableGridPoint2(x, y)
                        val asset = wallAssets.random(random)
                        val atlasRegion = gameScreen.textureAtlasAll[asset]
                        Wall(position, atlasRegion, height = 1u)
                    }
                    else -> {
                        val position = immutableGridPoint2(x, y)
                        val asset = when (tile) {
                            TileType.Door -> doorAssets.random(random)
                            TileType.Maze -> mazeAssets.random(random)
                            else -> floorAssets.random(random)
                        }
                        val atlasRegion = gameScreen.textureAtlasAll[asset]
                        Floor(position, atlasRegion)
                    }
                }
            }
        }
    }

    operator fun get(x: Int, y: Int): Tile? {
        if (x !in 0 until size.toInt() || y !in 0 until size.toInt()) return null
        return tiles[x * size.toInt() + y]
    }

    operator fun get(position: ImmutableGridPoint2) = get(position.x, position.y)

    override fun iterator() = tiles.iterator()

    override fun dispose() {
        tiles.dispose()
    }
}
