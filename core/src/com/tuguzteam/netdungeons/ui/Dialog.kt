package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align

open class Dialog(title: String, skin: Skin) : Dialog(title, skin) {
    var isHidden = true
        private set

    fun buttonSpaceRight(index: Int) {
        buttonTable.cells[index].spaceRight(50f)
    }

    override fun hide(action: Action?) {
        isHidden = true
        super.hide(action)
    }

    override fun show(stage: Stage?, action: Action?): Dialog {
        isHidden = false
        return super.show(stage, action)
    }

    override fun result(`object`: Any?) {
        isHidden = true
    }

    init {
        titleLabel.setAlignment(Align.top)
        isResizable = false
        isMovable = false
    }
}
