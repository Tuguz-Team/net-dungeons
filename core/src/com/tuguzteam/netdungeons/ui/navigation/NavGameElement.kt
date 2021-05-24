package com.tuguzteam.netdungeons.ui.navigation

import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.RadioButtonGroup
import com.tuguzteam.netdungeons.ui.Window
import com.tuguzteam.netdungeons.ui.doClick

class NavGameElement(
    scrollPane: VisScrollPane, percentage: Float,
    private val labelName: String, windowTitle: String,
    buttonNames: List<String>
) {
    private val radioButton = RadioButtonGroup(false, buttonNames).apply {
        groupButtons.forEachIndexed { index, button ->
            button.addListener(ClickListener {
                if (innerLabel.textEquals(buttonNames[index]))
                    this@NavGameElement.uncheck()
                else
                    innerLabel.setText(buttonNames[index])
            })
        }
    }
    val innerLabel = VisLabel(labelName).apply {
        addListener(ClickListener {
            scrollPane.cancel()
            scrollPane.layout()
            scrollPane.scrollPercentY = percentage
        })
    }
    val window = Window(windowTitle, false, radioButton.groupButtons)

    fun anyChecked() = radioButton.anyChecked()

    fun uncheck() {
        innerLabel.setText(labelName)
        radioButton.uncheck()
    }

    fun click() = doClick(innerLabel)
}
