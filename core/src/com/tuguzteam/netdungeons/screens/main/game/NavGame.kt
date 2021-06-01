package com.tuguzteam.netdungeons.screens.main.game

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.kotcrab.vis.ui.widget.VisImageTextButton
import com.kotcrab.vis.ui.widget.VisImageTextButton.VisImageTextButtonStyle
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.heightFraction
import com.tuguzteam.netdungeons.screens.GameScreen
import com.tuguzteam.netdungeons.screens.main.ContentHeader
import com.tuguzteam.netdungeons.screens.main.NavButton
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.SplitPane
import ktx.actors.plusAssign

class NavGame(loader: Loader, contentSplitPane: SplitPane, header: ContentHeader) {
    private val content = VerticalGroup().apply {
        pad(heightFraction(.1f)).space(heightFraction(.25f))
    }
    private val contentScroll = VisScrollPane(content).apply {
        setOverscroll(false, false)
        fadeScrollBars = false
    }
    private val gameMode = NavGameElement(
        contentScroll, 0f, "Mode",
        "Choose game mode", listOf("Team Fight", "Slaughter")
    )
    private val gameSize = NavGameElement(
        contentScroll, .5f, "Size",
        "Choose map size", listOf("Medium", "Large", "Very Large")
    )
    private val gameType = NavGameElement(
        contentScroll, 1f, "Type",
        "Choose amounts of treasure", listOf("Mansion", "Castle", "Slum")
    )

    init {
        content.apply {
            this += gameMode.window
            this += gameSize.window
            this += gameType.window
        }
    }

    private val headerContent = HorizontalGroup().apply {
        center().space(heightFraction(.05f))
        this += gameMode.innerLabel
        this += VisLabel("in")
        this += gameSize.innerLabel
        this += gameType.innerLabel
    }
    private val playButton = VisImageTextButton(
        "Play",
        VisImageTextButtonStyle(null, null, null, BitmapFont())
    ).apply {
        addListener(ClickListener {
            when {
                !gameMode.anyChecked() -> gameMode.click()
                !gameSize.anyChecked() -> gameSize.click()
                !gameType.anyChecked() -> gameType.click()
                else -> loader.setScreen<GameScreen>()
            }
        })
    }
    private val headerSplitPane = SplitPane(
        headerContent, Container(playButton), false, 0.8f
    )
    val navButton = NavButton("Game", contentSplitPane, contentScroll) {
        contentSplitPane.setSecondWidget(contentScroll)
        header.setFirstWidget(headerSplitPane)
        uncheck()
    }

    private fun uncheck() {
        gameMode.uncheck()
        gameMode.click()
        gameSize.uncheck()
        gameType.uncheck()
    }
}
