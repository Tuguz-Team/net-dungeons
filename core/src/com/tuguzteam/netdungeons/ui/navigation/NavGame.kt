package com.tuguzteam.netdungeons.ui.navigation

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.kotcrab.vis.ui.widget.VisImageTextButton
import com.kotcrab.vis.ui.widget.VisImageTextButton.VisImageTextButtonStyle
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.heightFraction
import com.tuguzteam.netdungeons.net.GameState
import com.tuguzteam.netdungeons.net.Result
import com.tuguzteam.netdungeons.screens.GameScreen
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.SplitPane
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import ktx.actors.plusAssign
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.error
import ktx.log.info

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
                else -> {
                    val handler = CoroutineExceptionHandler { _, throwable ->
                        Loader.logger.error(cause = throwable) { "Game starting failure" }
                    }
                    KtxAsync.launch(handler) {
                        when (val result = loader.gameManager.createGame()) {
                            is Result.Cancel -> Loader.logger.info { "Task was cancelled normally" }
                            is Result.Failure -> throw result.cause
                            is Result.Success -> loader.gameManager.gameStateListener = {
                                when (it) {
                                    is GameState.Destroyed -> TODO()
                                    is GameState.Ended -> TODO()
                                    is GameState.Failure -> Loader.logger.error(it.cause) {
                                        "General game data listening failure"
                                    }
                                    is GameState.PlayerAdded -> Loader.logger.debug {
                                        "Added ${it.player}"
                                    }
                                    is GameState.PlayerRemoved -> Loader.logger.debug {
                                        "Removed ${it.player}"
                                    }
                                    is GameState.PlayerUpdated -> Loader.logger.debug {
                                        "Updated ${it.player}"
                                    }
                                    is GameState.Started -> {
                                        val seed = it.game.seed
                                        checkNotNull(seed) { "Game was not started on the server" }
                                        MathUtils.random.setSeed(seed)

                                        loader.setScreen<GameScreen>()
                                    }
                                }
                            }
                        }
                    }
                }
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
