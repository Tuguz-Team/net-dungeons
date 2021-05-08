package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.utils.Align

class Window(title: String, skin: Skin, vararg tables: Table) : Window(title, skin) {
    init {
//        padLeft(Gdx.graphics.height / 2f)
//        padRight(Gdx.graphics.height / 2f)
//        padBottom(Gdx.graphics.height / 2f)
        titleLabel.setAlignment(Align.center)
        isResizable = false
        isMovable = false
        isModal = false
        for (table in tables)
            add(table)
    }
}
