package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align

class OkDialog(title: String, private val onOkOption: () -> Unit = {}) : Dialog(title) {
    init {
        button("OK")
        buttonsTable.cells.forEach { cell ->
            (cell.actor as TextButton).label.setAlignment(Align.top)
        }
        pad()
    }

    override fun result(`object`: Any?) {
        super.result(`object`)
        onOkOption()
    }
}
