package com.tuguzteam.netdungeons.input

import com.badlogic.gdx.Gdx
import com.tuguzteam.netdungeons.KtxGestureAdapter
import com.tuguzteam.netdungeons.field.Cell
import com.tuguzteam.netdungeons.immutableVec2
import com.tuguzteam.netdungeons.screens.GameScreen

class MovementGestureListener(private val gameScreen: GameScreen) : KtxGestureAdapter {
    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
        val vX = velocityX / Gdx.graphics.width
        val vY = velocityY / Gdx.graphics.height
        val offset = when {
            vX > 0.2f -> immutableVec2(x = Cell.width.toFloat())
            vX < -0.2f -> immutableVec2(x = -Cell.width.toFloat())
            vY > 0.2f -> immutableVec2(y = Cell.width.toFloat())
            vY < -0.2f -> immutableVec2(y = -Cell.width.toFloat())
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
