package com.tuguzteam.netdungeons.screens.main

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.kotcrab.vis.ui.widget.VisImageButton
import com.kotcrab.vis.ui.widget.VisTextButton
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.Dialog
import com.tuguzteam.netdungeons.ui.SplitPane
import com.tuguzteam.netdungeons.ui.YesNoDialog
import ktx.actors.centerPosition

class ContentHeader(stage: Stage, horGroup: WidgetGroup?, splitAmount: Float) :
    SplitPane(horGroup, null, false, splitAmount) {
    private val yesNoDialog = YesNoDialog(
        "Are you sure you want to exit?",
        onYesOption = Gdx.app::exit,
        onNoOption = { settingsDialog.show(stage) }
    )
    private val exitButton: VisTextButton = VisTextButton("Exit Game").apply {
        addListener(ClickListener {
            yesNoDialog.show(stage)
        })
    }
    private val settingsDialog = Dialog("Settings").apply {
        button("Return").button(exitButton)
        pad()
    }

    init {
        horGroup?.centerPosition()
        setSecondWidget(VisImageButton(null, null, null).apply {
            addListener(ClickListener {
                settingsDialog.show(stage)
            })
        })
    }
}
