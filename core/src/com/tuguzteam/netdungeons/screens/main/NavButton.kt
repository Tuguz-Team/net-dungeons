package com.tuguzteam.netdungeons.screens.main

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.kotcrab.vis.ui.widget.VisImageTextButton
import com.tuguzteam.netdungeons.ui.ClickListener

class NavButton(text: String, checkParent: WidgetGroup, checkChild: Actor, body: () -> Unit) :
    VisImageTextButton(
        text, VisImageTextButtonStyle(null, null, null, BitmapFont())
    ) {

    private var wasPressed = false

    init {
        addListener(ClickListener {
            wasPressed = false
            if (checkParent.children.contains(checkChild))
                wasPressed = true

            if (!wasPressed) body()
        })
    }
}