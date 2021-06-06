package com.tuguzteam.netdungeons.screens.main.profile

import com.kotcrab.vis.ui.widget.VisLabel
import com.tuguzteam.netdungeons.ui.YesNoDialog

class SkillWindow(onYesOption: () -> Unit, onNoOption: () -> Unit)
    : YesNoDialog("", onYesOption, onNoOption) {

    val description = VisLabel("").apply { wrap = true }

    init {
        buttonsTable.add()

        addCloseButton()
    }
}
