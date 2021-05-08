package com.tuguzteam.netdungeons.ui.navigation

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.RadioButton
import com.tuguzteam.netdungeons.ui.Window

class NavGameElement(
    skin: Skin, scrollPane: ScrollPane, percentage: Float,
    private val labelName: String, windowTitle: String,
    vararg buttonNames: String
) {
    private val radioButton = RadioButton(false, skin, *buttonNames).apply {
        buttons.forEachIndexed { index, checkBox ->
            checkBox.addListener(ClickListener {
                if (checkBox.isChecked) innerLabel.setText(buttonNames[index])
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
