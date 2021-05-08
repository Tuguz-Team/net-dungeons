package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align

class YesNoDialog(
    title: String, skin: Skin, private val onYesOption: () -> Unit,
    private val onNoOption: () -> Unit = {}
) : Dialog(title, skin) {
    init {
        button("Yes", true).button("No", false)
        buttonSpaceRight(0)
        buttonTable.cells.forEach { cell ->
            (cell.actor as TextButton).label.setAlignment(Align.top)
        }
    }

    override fun result(`object`: Any?) {
        if (`object` as Boolean) onYesOption()
        else onNoOption()
    }
}
