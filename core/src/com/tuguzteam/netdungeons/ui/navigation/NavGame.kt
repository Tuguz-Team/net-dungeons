package com.tuguzteam.netdungeons.ui.navigation

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
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.SplitPane
import ktx.actors.plusAssign

class NavGame(loader: Loader, contentSplitPane: SplitPane, header: ContentHeader) {
    private val scrollGroup = VerticalGroup().apply {
        pad(
            heightFraction(.1f), heightFraction(1 / 8f),
            heightFraction(.1f), heightFraction(1 / 8f)
        ).space(heightFraction(1 / 8f))
    }
    private val content = VisScrollPane(scrollGroup).apply {
        setOverscroll(false, false)
        setScrollbarsVisible(true)
        fadeScrollBars = false
        setFlingTime(0f)
    }
    private val gameMode = NavGameElement(
        content, 0f, "Mode",
        "Choose game mode", listOf("Team Fight", "Slaughter")
    )
    private val gameSize = NavGameElement(
        content, .5f, "Size",
        "Choose map size", listOf("Medium", "Large", "Very Large")
    )
    private val gameType = NavGameElement(
        content, 1f, "Type",
        "Choose amounts of treasure", listOf("Mansion", "Castle", "Slum")
    )

    init {
        scrollGroup.apply {
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
    val navButton = VisImageTextButton(
        "Game",
        VisImageTextButtonStyle(null, null, null, BitmapFont())
    ).apply {
        addListener(ClickListener {
            contentSplitPane.setSecondWidget(content)
            header.setFirstWidget(headerSplitPane)
        })
    }

    fun uncheck() {
        gameMode.uncheck()
        gameSize.uncheck()
        gameType.uncheck()
    }
}
