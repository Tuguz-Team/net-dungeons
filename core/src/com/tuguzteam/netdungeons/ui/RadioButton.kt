package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Skin

class RadioButton(checked: Boolean, skin: Skin, vararg buttonNames: String) {
    val buttons = mutableListOf<CheckBox>()
    init {
        for (name in buttonNames)
            buttons += CheckBox(name, skin)
    }
    private val controller = ButtonGroup(*buttons.toTypedArray()).apply {
        if (checked) buttons[0].isChecked = true
    }

    fun uncheck() {
        controller.uncheckAll()
    }
}