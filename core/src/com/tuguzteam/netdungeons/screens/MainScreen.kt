package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle
import com.badlogic.gdx.utils.GdxRuntimeException
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.net.NetworkManager
import com.tuguzteam.netdungeons.net.Result
import com.tuguzteam.netdungeons.ui.*
import kotlinx.coroutines.launch
import ktx.actors.plusAssign
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.error
import ktx.log.info

class MainScreen(loader: Loader) : StageScreen(loader) {
    private val defaultSkin = loader.defaultSkin
    private val yesNoDialog =
        YesNoDialog("Are you sure you want to exit?", defaultSkin, Gdx.app::exit)

    inner class NavProfile {
        val navButton = ImageTextButton("Profile",
            ImageTextButtonStyle(null, null, null, BitmapFont())).apply {
            addListener(ClickListener {
                contentSplitPane.setSecondWidget(null)
                header.setFirstWidget(HorizontalGroup())
                navGame.uncheck()
            })
        }
    }

    inner class NavRating {
        private val radioButton = RadioButton(true, defaultSkin,
    "By level", "By wins", "By kills")
        private val sortButtons = HorizontalGroup().apply {
            left().space(Gdx.graphics.height / 20f).padLeft(Gdx.graphics.height / 20f)
            for (button in radioButton.buttons)
                this += button
        }
        val navButton = ImageTextButton("Rating",
            ImageTextButtonStyle(null, null, null, BitmapFont())).apply {
            addListener(ClickListener {
                contentSplitPane.setSecondWidget(null)
                header.setFirstWidget(sortButtons)
                navGame.uncheck()
            })
        }
    }

    private val header = ContentHeader(this, null, defaultSkin)
    private val contentSplitPane = SplitPane(header, null,
        true, defaultSkin).apply {
        maxSplitAmount = 0.15f
        minSplitAmount = 0.15f
    }
    private val navGame = NavGame(loader, defaultSkin, contentSplitPane, header)
    private val navigation = Table().apply {
        center().add(navGame.navButton).expand().row()
        add(NavProfile().navButton).expand().row()
        add(NavRating().navButton).expand()
    }
    private val mainSplitPane = SplitPane(navigation, contentSplitPane,
        false, defaultSkin).apply {
        setFillParent(true)
        maxSplitAmount = 0.15f
        minSplitAmount = 0.15f
    }

    init {
        isDebugAll = true
        this += mainSplitPane
        loader.addScreen(screen = GameScreen(loader, this))
    }

    override fun show() {
        super.show()
        Loader.logger.debug { "Main menu screen is shown..." }
        val registrationScreen = try {
            loader.getScreen<RegistrationScreen>()
        } catch (e: GdxRuntimeException) {
            loader.addScreen(screen = RegistrationScreen(loader))
            null
        }
        KtxAsync.launch {
            when (val result = loader.networkManager.updateUser()) {
                is Result.Cancel -> Loader.logger.info { "Task was cancelled normally" }
                is Result.Failure -> Loader.logger.error(result.cause) { "User update failure!" }
                is Result.Success -> {
                    val data = result.data
                    if (registrationScreen == null && data == null) {
                        loader.setScreen<RegistrationScreen>()
                    } else {
                        NetworkManager.logger.debug(data::toString)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (yesNoDialog.isHidden) yesNoDialog.show(this)
    }
}
