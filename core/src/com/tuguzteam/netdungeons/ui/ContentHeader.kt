package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup

class ContentHeader(stage: Stage, horGroup: HorizontalGroup, skin: Skin) :
    SplitPane(horGroup, null, false, skin) {
    private val yesNoDialog = YesNoDialog("Are you sure you want to exit?", skin, Gdx.app::exit) {
        settingsDialog.show(stage)
    }
    private val exitButton: TextButton = TextButton("Exit Game", skin).apply {
        addListener(ClickListener {
            yesNoDialog.show(stage)
        })
    }
    private val settingsDialog = Dialog("Settings", skin).apply {
        button("Return").button(exitButton)
        buttonSpaceRight(0)
    }
    private val settingsButton = ImageButton(
        null, null, null).apply {
        addListener(ClickListener {
            settingsDialog.show(stage)
        })
    }

    init {
        minSplitAmount = 0.9f
        setSecondWidget(settingsButton)
    }
}
