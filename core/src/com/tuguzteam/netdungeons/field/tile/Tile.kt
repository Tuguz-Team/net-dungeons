package com.tuguzteam.netdungeons.field.tile

import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.ImmutableGridPoint2
import com.tuguzteam.netdungeons.immutableVec3
import com.tuguzteam.netdungeons.objects.Blendable
import com.tuguzteam.netdungeons.objects.GameObject
import com.tuguzteam.netdungeons.objects.Renderable
import ktx.assets.dispose

sealed class Tile(val position: ImmutableGridPoint2) : GameObject(), Blendable, Renderable {
    companion object : Iterable<Tile>, Disposable {
        const val size = 2u

        private val tiles = arrayListOf<Tile>()
        override fun iterator() = tiles.iterator()

        override fun dispose() {
            tiles.dispose()
            tiles.clear()
        }

        fun toImmutableVec3(point: ImmutableGridPoint2) = immutableVec3(
            x = point.x.toFloat() * size.toInt(),
            z = point.y.toFloat() * size.toInt(),
        )
    }

    init {
        @Suppress("LeakingThis")
        tiles += this
    }

    override fun dispose() {
        tiles -= this
    }
}

fun Tile.toImmutableVec3() = Tile.toImmutableVec3(position)
