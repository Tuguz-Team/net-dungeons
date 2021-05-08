package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisWindow

class Window(title: String, isBorder: Boolean, vararg tables: Table) : VisWindow(title, isBorder) {
    init {
        titleLabel.setAlignment(Align.center)
        isResizable = false
        isMovable = false
        isModal = false
        tables.forEach { table ->
            add(table).pad(Gdx.graphics.height / 40f)
        }
    }
}
