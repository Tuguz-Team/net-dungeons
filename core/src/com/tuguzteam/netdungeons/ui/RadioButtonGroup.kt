package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.kotcrab.vis.ui.widget.VisTextButton

class RadioButtonGroup(
    checked: Boolean,
    vararg buttonNames: String
) {
    val groupButtons = mutableListOf<VisTextButton>()

//    constructor(checked: Boolean, vararg buttonNames: String) : this(checked) {
//        buttonNames.forEach { name ->
//            groupButtons += VisTextButton(name, "toggle").apply {
//                isFocusBorderEnabled = false
//            }
//        }
//    }
//
//    constructor(checked: Boolean, clicked: Boolean, vararg buttons: VisTextButton) : this(checked) {
//        buttons.forEach { button ->
//            groupButtons += button.apply {
//                isFocusBorderEnabled = false
//            }
//        }
//        if (clicked) doClick(groupButtons[0])
//    }

    init {
        buttonNames.forEach { name ->
            groupButtons += VisTextButton(name, "toggle").apply {
                isFocusBorderEnabled = false
            }
        }
    }

    constructor(checked: Boolean, clicked: Boolean, vararg buttonPairs: Pair<String, ClickListener>)
            : this(checked, *buttonPairs.map { button -> button.first }.toTypedArray()) {
        buttonPairs.forEachIndexed { index, pair ->
            groupButtons[index].addListener(pair.second)
        }
        if (clicked) doClick(groupButtons[0])
    }

    private val controller = ButtonGroup(*groupButtons.toTypedArray()).apply {
        if (checked && groupButtons.isNotEmpty()) groupButtons[0].isChecked = true
    }

    fun uncheck() = controller.uncheckAll()

    fun anyChecked() = groupButtons.find { button -> button.isChecked } != null
}
