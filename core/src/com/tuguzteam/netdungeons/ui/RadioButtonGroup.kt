package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.kotcrab.vis.ui.widget.VisTextButton

class RadioButtonGroup private constructor(private val ifChecked: Boolean) {
    private lateinit var controller: ButtonGroup<VisTextButton>
    val groupButtons = mutableListOf<VisTextButton>()

    constructor(checked: Boolean, buttonNames: Iterable<String>) : this(checked) {
        buttonNames.forEach { name ->
            groupButtons += VisTextButton(name, "toggle").apply {
                isFocusBorderEnabled = false
            }
        }
        initController()
    }

    constructor(
        checked: Boolean,
        clicked: Boolean,
        buttons: Iterable<VisTextButton>
    ) : this(checked) {
        buttons.forEach { button ->
            groupButtons += button.apply {
                isFocusBorderEnabled = false
            }
        }
        initController()
        if (clicked) doClick(groupButtons[0])
    }

    private fun initController() {
        controller = ButtonGroup(*groupButtons.toTypedArray()).apply {
            if (ifChecked && groupButtons.isNotEmpty()) groupButtons[0].isChecked = true
        }
    }

    fun uncheck() = controller.uncheckAll()

    fun anyChecked() = groupButtons.find { button -> button.isChecked } != null
}
