package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.scenes.scene2d.ui.*

class NavGameElement(skin: Skin, scrollPane: ScrollPane, percentage: Float,
                     private val labelName: String, windowTitle: String,
                     vararg buttonNames: String) {

    private val radioButton = RadioButton(false, skin, *buttonNames).apply {
        for (i in 0 until buttons.size)
            buttons[i].apply {
                addListener(ClickListener {
                    if (isChecked) innerLabel.setText(buttonNames[i])
                })
            }
    }
    val innerLabel = Label(labelName, skin).apply {
        addListener(ClickListener {
            scrollPane.cancel()
            scrollPane.layout()
            scrollPane.scrollPercentY = percentage
        })
    }
    val window = Window(windowTitle, skin, *radioButton.buttons.toTypedArray())

    fun uncheck() {
        innerLabel.setText(labelName)
        radioButton.uncheck()
    }
}