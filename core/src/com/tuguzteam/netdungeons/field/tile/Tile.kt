package com.tuguzteam.netdungeons.field.tile

import com.tuguzteam.netdungeons.ImmutableGridPoint2
import com.tuguzteam.netdungeons.immutableVec3
import com.tuguzteam.netdungeons.objects.GameObject

sealed class Tile(val position: ImmutableGridPoint2) : GameObject() {
    companion object : Iterable<Tile> {
        const val size = 1u

        private val tiles = arrayListOf<Tile>()
        override fun iterator() = tiles.iterator()
    }

    init {
        @Suppress("LeakingThis")
        tiles += this
    }

    override fun dispose() {
        tiles -= this
    }
}

fun Tile.vec3Position() = immutableVec3(x = position.x.toFloat(), z = position.y.toFloat())
