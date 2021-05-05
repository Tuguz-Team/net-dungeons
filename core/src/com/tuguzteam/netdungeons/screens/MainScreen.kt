package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.ui.ButtonListener
import com.tuguzteam.netdungeons.ui.ContentHeader
import com.tuguzteam.netdungeons.ui.YesNoDialog
import ktx.actors.plusAssign
import ktx.log.debug

class MainScreen(loader: Loader) : StageScreen(loader) {
    private val defaultSkin = loader.defaultSkin
    private val yesNoDialog =
        YesNoDialog("Are you sure you want to exit?", defaultSkin, Gdx.app::exit)
    private val navGameButton = ImageTextButton("Game", ImageTextButtonStyle(
        null, null, null, BitmapFont()))
    private val navProfileButton = ImageTextButton("Profile", ImageTextButtonStyle(
        null, null, null, BitmapFont()))
    private val navRatingButton = ImageTextButton("Rating", ImageTextButtonStyle(
        null, null, null, BitmapFont()))
    private val gameScreenButton = TextButton("Go to game screen", defaultSkin).apply {
        addListener(ButtonListener {
            loader.setScreen<GameScreen>()
        })
    }
    private val navigation = Table().apply {
        center().add(navGameButton).expand().row()
        add(navProfileButton).expand().row()
        add(navRatingButton).expand()
    }
    private val emptyContent = Table().apply {
        center().add(gameScreenButton)
    }
    private val header = ContentHeader(this, HorizontalGroup(), defaultSkin)
    private val contentSplitPane = SplitPane(header,
        emptyContent, true, defaultSkin).apply {
        maxSplitAmount = 0.15f
    }
    private val mainSplitPane = SplitPane(navigation,
        contentSplitPane, false, defaultSkin).apply {
        setFillParent(true)
        maxSplitAmount = 0.15f
    }

    init {
        isDebugAll = true
        this += mainSplitPane
        loader.addScreen(screen = GameScreen(loader, this))
        loader.networkManager.auth { user ->
            Loader.logger.debug { user.toString() }
        }
    }

    override fun show() {
        super.show()
        Loader.logger.debug { "Main menu screen is shown..." }
    }

    override fun onBackPressed() {
        if (yesNoDialog.isHidden) yesNoDialog.show(this)
    }
}
