package com.tuguzteam.netdungeons.screens.main.game

import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisImageButton
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisTable
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.addActor
import com.tuguzteam.netdungeons.heightFraction
import com.tuguzteam.netdungeons.screens.GameScreen
import com.tuguzteam.netdungeons.screens.main.ContentHeader
import com.tuguzteam.netdungeons.screens.main.NavButton
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.SplitPane
import ktx.actors.plusAssign

class NavGame(loader: Loader, contentSplitPane: SplitPane, header: ContentHeader) {
    private val navPad = heightFraction(.1f)

    private val content = VerticalGroup().pad(navPad).space(navPad)

    private val contentScroll = VisScrollPane(content).apply {
        setOverscroll(false, false)
        fadeScrollBars = false
        setFlingTime(0f)
    }

    private val gameMode = NavGameElement(
        contentScroll, 0f, "Mode",
        "Choose game mode", listOf("Team Fight", "Slaughter"),
    )
    private val gameSize = NavGameElement(
        contentScroll, .5f, "Size",
        "Choose map size", listOf("Medium", "Large", "Very Large"),
    )
    private val gameType = NavGameElement(
        contentScroll, 1f, "Type",
        "Choose amounts of treasure", listOf("Mansion", "Castle", "Slum"),
    )

    init {
        content.apply {
            this += gameMode.window
            this += gameSize.window
            this += gameType.window
        }
    }

    private val headerContent = VisTable(false).apply {
        left().padRight(navPad / 2)

        addActor(this, gameMode.innerLabel)
        addActor(this, VisLabel("in", "medium")
            .apply { setAlignment(Align.center) }, navPad)
        addActor(this, gameSize.innerLabel)
        addActor(this, gameType.innerLabel)
    }

    private val imageButton =
        VisImageButton(null, null, null)

    private val playButton = Container(
        VisTable(false).apply {
            add(imageButton).size(heightFraction(.12f))
            add(VisLabel("Play", Align.center)).grow()
        }
    ).apply {
        fill().pad(navPad / 2.5f)

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
        headerContent, playButton, false, 0.75f
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
