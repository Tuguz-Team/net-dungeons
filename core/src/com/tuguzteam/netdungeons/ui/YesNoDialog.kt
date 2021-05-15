package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align

class YesNoDialog(
    title: String,
    private val onYesOption: () -> Unit,
    private val onNoOption: () -> Unit = {}
) : Dialog(title) {
    init {
        button("Yes", true).button("No", false)
        buttonsTable.cells.forEach { cell ->
            (cell.actor as TextButton).label.setAlignment(Align.top)
        }
        pad()
    }

    override fun result(`object`: Any?) {
        super.result(`object`)
        if (`object` as Boolean) onYesOption()
        else onNoOption()
    }
}
