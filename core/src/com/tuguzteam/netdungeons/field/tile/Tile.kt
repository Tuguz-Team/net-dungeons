package com.tuguzteam.netdungeons.field.tile

import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.ImmutableGridPoint2
import com.tuguzteam.netdungeons.immutableVec3
import com.tuguzteam.netdungeons.objects.Blendable
import com.tuguzteam.netdungeons.objects.GameObject
import ktx.assets.dispose

sealed class Tile(val position: ImmutableGridPoint2) : GameObject(), Blendable {
    companion object : Iterable<Tile>, Disposable {
        const val size = 1u

        private val tiles = arrayListOf<Tile>()
        override fun iterator() = tiles.iterator()

        override fun dispose() {
            tiles.dispose()
            tiles.clear()
        }
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
