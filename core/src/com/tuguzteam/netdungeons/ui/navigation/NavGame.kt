package com.tuguzteam.netdungeons.ui.navigation

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.getHeightPerc
import com.tuguzteam.netdungeons.screens.GameScreen
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.SplitPane
import ktx.actors.plusAssign
import ktx.log.error

class NavGame(loader: Loader, skin: Skin, contentSplitPane: SplitPane, header: ContentHeader) {
    private val scrollGroup = VerticalGroup().apply {
        pad(
            getHeightPerc(.1f), getHeightPerc(1 / 8f),
            getHeightPerc(.1f), getHeightPerc(1 / 8f)
        ).space(getHeightPerc(1 / 8f))
    }
    private val content = ScrollPane(scrollGroup).apply {
        setOverscroll(false, false)
        setFlingTime(0f)
    }
    private val gameMode = NavGameElement(
        skin, content, 0f, "Mode",
        "Choose game mode", "Team Fight", "Slaughter"
    )
    private val gameSize = NavGameElement(
        skin, content, .5f, "Size",
        "Choose map size", "Medium", "Large", "Very Large"
    )
    private val gameType = NavGameElement(
        skin, content, 1f, "Type",
        "Choose amounts of treasure", "Mansion", "Castle", "Slum"
    )

    init {
        scrollGroup.apply {
            this += gameMode.window
            this += gameSize.window
            this += gameType.window
        }
    }

    private val headerContent = HorizontalGroup().apply {
        center().space(getHeightPerc(.05f))
        this += gameMode.innerLabel
        this += Label("in", skin)
        this += gameSize.innerLabel
        this += gameType.innerLabel
    }
    private val playButton = ImageTextButton(
        "Play",
        ImageTextButtonStyle(null, null, null, BitmapFont())
    ).apply {
        addListener(ClickListener {
            when {
                !gameMode.anyChecked() -> gameMode.click()
                !gameSize.anyChecked() -> gameSize.click()
                !gameType.anyChecked() -> gameType.click()
                else -> try {
                    val seed = loader.gameManager.game?.seed
                    checkNotNull(seed) { "Game was not started on the server" }
                    MathUtils.random.setSeed(seed)

                    loader.setScreen<GameScreen>()
                } catch (e: Exception) {
                    Loader.logger.error(e) { "Game starting failure" }
                }
            }
        })
    }
    private val headerSplitPane = SplitPane(
        headerContent, Container(playButton), false, 0.8f
    )
    val navButton = ImageTextButton(
        "Game",
        ImageTextButtonStyle(null, null, null, BitmapFont())
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
