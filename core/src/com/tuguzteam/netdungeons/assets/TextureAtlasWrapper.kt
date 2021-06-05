package com.tuguzteam.netdungeons.assets

import com.badlogic.gdx.graphics.g2d.TextureAtlas

sealed interface TextureAtlasWrapper {
    val textureRegions: List<TextureAtlas.AtlasRegion>

    sealed interface Region {
        val regionName: String
    }
}
