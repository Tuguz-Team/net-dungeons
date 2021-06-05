package com.tuguzteam.netdungeons.input

import com.badlogic.gdx.Gdx
import com.tuguzteam.netdungeons.KtxGestureAdapter
import com.tuguzteam.netdungeons.field.tile.Tile
import com.tuguzteam.netdungeons.field.tile.Wall
import com.tuguzteam.netdungeons.immutableGridPoint2
import com.tuguzteam.netdungeons.screens.GameScreen

class MovementGestureListener(private val gameScreen: GameScreen) : KtxGestureAdapter {
    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
        val field = gameScreen.field ?: return false
        val vX = velocityX / Gdx.graphics.width
        val vY = velocityY / Gdx.graphics.height
        val offset = when {
            vX > 0.2f -> immutableGridPoint2(x = 1)
            vX < -0.2f -> immutableGridPoint2(x = -1)
            vY > 0.2f -> immutableGridPoint2(y = 1)
            vY < -0.2f -> immutableGridPoint2(y = -1)
            else -> null
        }
        offset?.let {
            val newPosition = gameScreen.playerPosition + offset
            if (field[newPosition] is Wall) {
                return false
            }
            gameScreen.apply {
                playerPosition = newPosition
                updateVisibleObjects()
            }
            return true
        }
        return false
    }
}
