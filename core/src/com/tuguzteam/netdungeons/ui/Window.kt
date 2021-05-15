package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisWindow
import com.tuguzteam.netdungeons.getHeightPerc

class Window(title: String, isBorder: Boolean, tables: Iterable<Table>) :
    VisWindow(title, isBorder) {

    init {
        titleLabel.setAlignment(Align.center)
        isResizable = false
        isMovable = false
        isModal = false
        tables.forEach { table ->
            add(table).pad(getHeightPerc(1 / 40f))
        }
    }
}
