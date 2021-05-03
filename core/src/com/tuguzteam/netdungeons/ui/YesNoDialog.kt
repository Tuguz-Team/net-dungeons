package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align

class YesNoDialog(title: String, skin: Skin, private val onYesOption: () -> Unit) :
    Dialog(title, skin) {
    init {
        button("Yes", true).button("No", false)
        buttonTable.cells[0].spaceRight(50f)
        for (cell in buttonTable.cells) {
            (cell.actor as TextButton).label.setAlignment(Align.top)
        }
        isMovable = false
        isResizable = false
    }

    override fun result(`object`: Any?) {
        if (`object` as Boolean) {
            onYesOption()
        }
    }
}
