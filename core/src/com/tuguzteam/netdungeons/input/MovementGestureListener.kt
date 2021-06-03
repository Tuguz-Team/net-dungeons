package com.tuguzteam.netdungeons.input

import com.badlogic.gdx.Gdx
import com.tuguzteam.netdungeons.KtxGestureAdapter
import com.tuguzteam.netdungeons.field.tile.Tile
import com.tuguzteam.netdungeons.immutableGridPoint2
import com.tuguzteam.netdungeons.screens.GameScreen

class MovementGestureListener(private val gameScreen: GameScreen) : KtxGestureAdapter {
    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
        val vX = velocityX / Gdx.graphics.width
        val vY = velocityY / Gdx.graphics.height
        val offset = when {
            vX > 0.2f -> immutableGridPoint2(x = Tile.size.toInt())
            vX < -0.2f -> immutableGridPoint2(x = -Tile.size.toInt())
            vY > 0.2f -> immutableGridPoint2(y = Tile.size.toInt())
            vY < -0.2f -> immutableGridPoint2(y = -Tile.size.toInt())
            else -> null
        }
        offset?.let {
            gameScreen.apply {
                playerPosition += offset
                updateVisibleObjects()
            }
            return true
        }
        return false
    }
}
