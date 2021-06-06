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
        titleLabel.setAlignment(Align.center)
        titleTable.getCell(titleLabel)
            .pad(heightFraction(.1f))
        titleTable.padTop(heightFraction(.05f))
        contentTable.padTop(heightFraction(.025f))

        isResizable = false
        isMovable = false
    }

    fun pad(): Dialog {
        buttonsTable.cells.forEach { it.pad(heightFraction(.02f)) }
        return this
    }

    fun size(): Dialog {
        val width = buttonsTable.cells.maxByOrNull { it.actor.width }
            ?.actor?.width ?: 0f

        buttonsTable.cells.forEach {
            it.size(
                width + heightFraction(.075f),
                heightFraction(.075f),
            )
        }
        return this
    }
}
