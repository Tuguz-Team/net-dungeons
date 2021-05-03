package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import ktx.actors.KtxInputListener

class ButtonListener(private val onTouch: () -> Unit) : KtxInputListener() {
    private var wasPressed = false

    override fun touchDown(
        event: InputEvent,
        x: Float,
        y: Float,
        pointer: Int,
        button: Int
    ): Boolean {
        wasPressed = true
        return wasPressed
    }

    override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
        if (wasPressed) onTouch()
        wasPressed = false
    }

    override fun enter(event: InputEvent, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
        wasPressed = true
    }

    override fun exit(event: InputEvent, x: Float, y: Float, pointer: Int, toActor: Actor?) {
        wasPressed = false
    }
}
