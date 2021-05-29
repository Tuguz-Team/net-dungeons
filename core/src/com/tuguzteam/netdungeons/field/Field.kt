package com.tuguzteam.netdungeons.field

import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.assets.AssetManager
import com.tuguzteam.netdungeons.assets.TextureAsset
import com.tuguzteam.netdungeons.field.rooms.Box
import com.tuguzteam.netdungeons.field.rooms.MultiRoom
import com.tuguzteam.netdungeons.field.rooms.Room
import com.tuguzteam.netdungeons.immutableVec3
import com.tuguzteam.netdungeons.objects.GameObject
import ktx.assets.dispose

class Field(
    val side: UInt,
    assetManager: AssetManager,
) : Disposable, Iterable<GameObject> {

    companion object {
        val cells = arrayOf(TextureAsset.Wood, TextureAsset.Wood1)
        val walls = arrayOf(TextureAsset.Wood)
    }

    init {
        require(side % 2u == 1u) { "side of field must be positive and odd: $side given" }
    }

    private val rooms = arrayOf(
        Box(
            position = immutableVec3(), type = Type.Slum,
            walls = setOf(Direction.Forward, Direction.Back, Direction.Right, Direction.Left),
            assetManager, width = 4u, length = 7u, height = 2u,
        ),
    )

    override fun iterator() = rooms.asSequence().map(::roomObjects).flatten().iterator()

    private fun roomObjects(room: Room) = when (room) {
        is Box -> room.asSequence()
        is MultiRoom -> room.asSequence().map { (it as Room).asSequence() }.flatten()
    }

    override fun dispose() {
        rooms.dispose()
    }
}
