package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.ui.ButtonListener
import com.tuguzteam.netdungeons.ui.YesNoDialog
import ktx.actors.plusAssign
import ktx.log.debug

class MainScreen(loader: Loader) : StageScreen(loader) {
    private val defaultSkin = loader.defaultSkin
    private val yesNoDialog = YesNoDialog("Are you sure you want to exit?", defaultSkin) {
        Gdx.app.exit()
    }
    private val navGameButton = TextButton("Game", defaultSkin).apply {
//        setFillParent(true)
    }
    private val navProfileButton = TextButton("Profile", defaultSkin).apply {
//        setFillParent(true)
    }
    private val navRatingButton = TextButton("Rating", defaultSkin).apply {
//        setFillParent(true)
    }
    private val gameScreenButton = TextButton("Go to game screen", defaultSkin).apply {
        addListener(ButtonListener {
            loader.setScreen<GameScreen>()
        })
    }
    private val navigation = Table().apply {
        center().debugAll()
        setSize(this@MainScreen.width * 0.3f, this@MainScreen.height / 3)
//        space(this@MainScreen.height / 4)

        add(navGameButton).expand().row()
        add(navProfileButton).expand().row()
        add(navRatingButton).expand()
    }
    private val content = Table().apply {
        center().debugAll()
        add(gameScreenButton)
    }
    private val header = HorizontalGroup()
    private val contentSplitPane = SplitPane(header, content, true, defaultSkin).apply {
        maxSplitAmount = 0.15f
    }
    private val mainSplitPane = SplitPane(navigation, contentSplitPane, false, defaultSkin).apply {
        setFillParent(true)
        maxSplitAmount = 0.15f
    }

    init {
        loader.addScreen(screen = GameScreen(loader, this))
        this += mainSplitPane
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
