package com.tuguzteam.netdungeons.ui.navigation

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.kotcrab.vis.ui.widget.VisTextButton
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.Dialog
import com.tuguzteam.netdungeons.ui.SplitPane
import com.tuguzteam.netdungeons.ui.YesNoDialog

class ContentHeader(stage: Stage, horGroup: HorizontalGroup?, splitAmount: Float) :
    SplitPane(horGroup, null, false, splitAmount) {
    private val yesNoDialog = YesNoDialog("Are you sure you want to exit?", Gdx.app::exit) {
        settingsDialog.show(stage)
    }
    private val exitButton: TextButton = VisTextButton("Exit Game").apply {
        addListener(ClickListener {
            yesNoDialog.show(stage)
        })
    }
    private val settingsDialog = Dialog("Settings").apply {
        button("Return").button(exitButton)
        pad()
    }

    init {
        setSecondWidget(ImageButton(
            null, null, null
        ).apply {
            addListener(ClickListener {
                settingsDialog.show(stage)
            })
        })
    }
}
