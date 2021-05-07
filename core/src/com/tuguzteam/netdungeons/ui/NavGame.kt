package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.screens.GameScreen
import ktx.actors.plusAssign

class NavGame(loader: Loader, skin: Skin, contentSplitPane: SplitPane, header: ContentHeader) {
    private val scrollGroup = VerticalGroup().apply {
        pad(Gdx.graphics.height / 6f).space(Gdx.graphics.height / 6f)
    }
    private val content = ScrollPane(scrollGroup).apply {
        setOverscroll(false, false)
        setFlingTime(0f)
    }
    private val gameMode = NavGameElement(skin, content, 0f, "Mode",
        "Choose game mode", "Team Fight", "Slaughter")
    private val gameSize = NavGameElement(skin, content, .5f, "Size",
        "Choose map size", "Medium", "Large", "Very Large")
    private val gameType = NavGameElement(skin, content, 1f, "Type",
        "Choose amounts of treasure", "Mansion", "Castle", "Slum")
    init {
        scrollGroup.apply {
            this += gameMode.window
            this += gameSize.window
            this += gameType.window
        }
    }

    private val headerContent = HorizontalGroup().apply {
        center().space(Gdx.graphics.height / 20f)
        this += gameMode.innerLabel
        this += Label("in", skin)
        this += gameSize.innerLabel
        this += gameType.innerLabel
    }
    private val playButton = ImageTextButton("Play",
        ImageTextButtonStyle(null, null, null, BitmapFont())).apply {
        addListener(ClickListener {
            loader.setScreen<GameScreen>()
        })
    }
    private val headerSplitPane = SplitPane(headerContent, Container(playButton),
        false, skin).apply {
        maxSplitAmount = 0.85f
        minSplitAmount = 0.85f
    }
    val navButton = ImageTextButton("Game",
        ImageTextButtonStyle(null, null, null, BitmapFont())).apply {
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