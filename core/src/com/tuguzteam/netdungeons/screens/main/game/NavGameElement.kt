package com.tuguzteam.netdungeons.screens.main.game

import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisImageButton
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisTable
import com.tuguzteam.netdungeons.heightFraction
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.RadioButtonGroup
import com.tuguzteam.netdungeons.ui.doClick
import com.tuguzteam.netdungeons.widthFraction

class NavGameElement(
    scrollPane: VisScrollPane, percentage: Float,
    private val labelName: String, windowTitle: String,
    buttonNames: List<String>
) {
    private val windowPad = heightFraction(.05f)

    private val radioButton = RadioButtonGroup(false, buttonNames, "-medium").apply {
        groupButtons.forEachIndexed { index, button ->
            button.addListener(ClickListener {
                if (innerLabel.textEquals(buttonNames[index]))
                    this@NavGameElement.uncheck()
                else
                    innerLabel.setText(buttonNames[index])
            })
        }
    }
    val innerLabel = VisLabel(labelName, "medium").apply {
        setAlignment(Align.center)
        addListener(ClickListener {
            scrollPane.scrollPercentY = percentage
        })
    }

    private val buttons = mutableListOf<VisTable>().apply {
        radioButton.groupButtons.forEach { radioButton ->
            add(VisTable(true).apply {
                add(VisImageButton(null, null, null))
                    .height(windowPad * 5).grow().row()
                add(radioButton).padTop(windowPad)
                    .size(windowPad * 5, windowPad * 2)

                radioButton.listeners.forEach { listener ->
                    addListener(listener)
                }
            })
        }
    }

    val window = VisTable(false).apply {
        add(VisLabel(windowTitle, Align.center))
            .colspan(buttons.size).padTop(windowPad)
            .width(widthFraction(.625f)).row()

        buttons.forEach { button ->
            add(button).grow().pad(windowPad)
        }
    }

    fun anyChecked() = radioButton.anyChecked()

    fun uncheck() {
        innerLabel.setText(labelName)
        radioButton.uncheck()
    }

    fun click() = doClick(innerLabel)
}
