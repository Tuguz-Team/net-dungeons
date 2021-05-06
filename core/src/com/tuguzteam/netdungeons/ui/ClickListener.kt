package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class ClickListener(private val onClick: () -> Unit) : ClickListener() {
    override fun clicked(event: InputEvent?, x: Float, y: Float) = onClick()
}
