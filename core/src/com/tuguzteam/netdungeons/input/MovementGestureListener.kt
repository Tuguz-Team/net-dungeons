package com.tuguzteam.netdungeons.input

import com.tuguzteam.netdungeons.KtxGestureAdapter
import com.tuguzteam.netdungeons.field.Cell
import com.tuguzteam.netdungeons.immutableVec2
import com.tuguzteam.netdungeons.screens.GameScreen

class MovementGestureListener(private val gameScreen: GameScreen) : KtxGestureAdapter {
    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
        gameScreen.playerPosition += immutableVec2(x = Cell.width.toFloat())
        return false
    }
}
