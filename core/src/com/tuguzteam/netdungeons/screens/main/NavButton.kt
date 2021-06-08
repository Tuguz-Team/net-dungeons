package com.tuguzteam.netdungeons.screens.main

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisImageButton
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.tuguzteam.netdungeons.heightFraction
import com.tuguzteam.netdungeons.ui.ClickListener

class NavButton(
    text: String, checkParent: WidgetGroup, checkChild: Actor, body: () -> Unit
) : Container<Actor>() {

    private val imageButton =
        VisImageButton(null, null, null)

    init {
        this.fill().pad(heightFraction(.05f))

        actor = VisTable(false).apply {
            add(imageButton).grow().row()
            add(VisLabel(text, Align.center)).grow()
        }

        addListener(ClickListener {
            imageButton.isDisabled = false
            if (checkParent.children.contains(checkChild))
                imageButton.isDisabled = true

            if (!imageButton.isDisabled) body()
        })
    }
}
