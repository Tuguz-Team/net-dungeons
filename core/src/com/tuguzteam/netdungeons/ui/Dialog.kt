package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisDialog
import com.tuguzteam.netdungeons.heightFraction

open class Dialog(title: String) : VisDialog(title, "noborder") {
    var isHidden = true
        private set

    override fun hide(action: Action?) {
        isHidden = true
        super.hide(action)
    }

    override fun show(stage: Stage?, action: Action?): VisDialog {
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

    fun pad(): Dialog {
        buttonsTable.cells.forEach { it.pad(heightFraction(.02f)) }
        contentTable.cells.forEach { it.pad(heightFraction(.02f)) }
        return this
    }

    fun size(): Dialog {
        buttonsTable.cells.forEach {
            it.width(0f).size(
                heightFraction(.15f), heightFraction(.075f)
            )
        }
        return this
    }
}
