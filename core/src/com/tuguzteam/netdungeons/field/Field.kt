package com.tuguzteam.netdungeons.field

import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.assets.TextureAsset
import com.tuguzteam.netdungeons.field.generator.Generator
import com.tuguzteam.netdungeons.field.rooms.Box
import com.tuguzteam.netdungeons.field.rooms.MultiRoom
import com.tuguzteam.netdungeons.field.rooms.Room
import com.tuguzteam.netdungeons.immutableVec3
import com.tuguzteam.netdungeons.objects.GameObject
import com.tuguzteam.netdungeons.screens.GameScreen
import ktx.assets.dispose

class Field(
    gameScreen: GameScreen,
) : Disposable, Iterable<GameObject> {

    companion object {
        val cells = listOf(TextureAsset.Wood, TextureAsset.Wood1)
        val walls = listOf(TextureAsset.Wood)
    }

    init {
        Generator.generate(20u, 20u, 5u)
    }

    private val rooms = listOf(
        Box(
            position = immutableVec3(), type = Type.Slum,
            walls = setOf(Direction.Forward, Direction.Back, Direction.Right, Direction.Left),
            gameScreen.assetManager, width = 3u, length = 7u, height = 2u,
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
