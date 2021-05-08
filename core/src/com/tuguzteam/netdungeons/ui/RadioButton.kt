package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.kotcrab.vis.ui.widget.VisTextButton

class RadioButton(checked: Boolean, vararg buttonNames: String) {
    val buttons = mutableListOf<VisTextButton>()

    init {
        buttonNames.forEach { name ->
            buttons += VisTextButton(name, "toggle").apply {
                isFocusBorderEnabled = false
            }
        }
    }

    private val controller = ButtonGroup(*buttons.toTypedArray()).apply {
        if (checked) buttons[0].isChecked = true
    }

    fun uncheck() = controller.uncheckAll()

    fun anyChecked() = buttons.find { button -> button.isChecked } != null
}
