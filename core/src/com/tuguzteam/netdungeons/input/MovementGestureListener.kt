package com.tuguzteam.netdungeons.input

import com.badlogic.gdx.Gdx
import com.tuguzteam.netdungeons.ImmutableGridPoint2
import com.tuguzteam.netdungeons.KtxGestureAdapter
import com.tuguzteam.netdungeons.field.Direction
import com.tuguzteam.netdungeons.field.tile.Wall
import com.tuguzteam.netdungeons.immutableGridPoint2
import com.tuguzteam.netdungeons.screens.GameScreen

class MovementGestureListener(private val gameScreen: GameScreen) : KtxGestureAdapter {
    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
        val field = gameScreen.field ?: return false
        val vX = velocityX / Gdx.graphics.width
        val vY = velocityY / Gdx.graphics.height

        val offset: ImmutableGridPoint2
        val direction: Direction
        when {
            vX > 0.2f -> {
                offset = immutableGridPoint2(x = 1)
                direction = Direction.Left
            }
            vX < -0.2f -> {
                offset = immutableGridPoint2(x = -1)
                direction = Direction.Right
            }
            vY > 0.2f -> {
                offset = immutableGridPoint2(y = 1)
                direction = Direction.Forward
            }
            vY < -0.2f -> {
                offset = immutableGridPoint2(y = -1)
                direction = Direction.Back
            }
            else -> return false
        }
        val newPosition = gameScreen.playerPosition + offset
        if (field[newPosition] is Wall) {
            return false
        }
        gameScreen.apply {
            playerPosition = newPosition
            playerDirection = direction
            updateVisibleObjects()
        }
        return true
    }
}
