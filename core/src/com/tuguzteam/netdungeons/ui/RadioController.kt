package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox

class RadioController(check: Boolean, vararg buttons: CheckBox) : ButtonGroup<CheckBox>(*buttons) {
    init {
        if (check) buttons[0].isChecked = true
    }
}
