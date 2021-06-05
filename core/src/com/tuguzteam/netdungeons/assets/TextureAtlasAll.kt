package com.tuguzteam.netdungeons.assets

import com.badlogic.gdx.graphics.g2d.TextureAtlas

class TextureAtlasAll internal constructor(
    private val textureAtlas: TextureAtlas,
) : TextureAtlasWrapper {

    private val mapTextureRegions = Region.values().map { region ->
        region to textureAtlas.findRegion(region.regionName, region.index)
    }.toMap()

    override val textureRegions = mapTextureRegions.values.toList()

    operator fun get(region: Region) = mapTextureRegions[region]!!

    enum class Region(
        override val regionName: String,
        val index: Int = -1,
    ) : TextureAtlasWrapper.Region {
        LogoLibGDX("logo_libGDX"),

        Wood("wood", 0),
        Wood1("wood", 1),
        WoodDark("wood_dark"),

        WallBrick("wall_brick", 0),
        WallBrick1("wall_brick", 1),
        WallBrick2("wall_brick", 2),
        WallBrickDark("wall_brick_dark"),
    }
}
