package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.assets.SkinAsset
import com.tuguzteam.netdungeons.getHeightPerc
import com.tuguzteam.netdungeons.net.NetworkManager
import com.tuguzteam.netdungeons.net.Result
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.RadioButton
import com.tuguzteam.netdungeons.ui.SplitPane
import com.tuguzteam.netdungeons.ui.YesNoDialog
import com.tuguzteam.netdungeons.ui.navigation.ContentHeader
import com.tuguzteam.netdungeons.ui.navigation.NavGame
import kotlinx.coroutines.launch
import ktx.actors.plusAssign
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.error
import ktx.log.info

class MainScreen(loader: Loader) : StageScreen(loader) {
    private val defaultSkin = loader.assetManager[SkinAsset.Default]!!
    private val yesNoDialog =
        YesNoDialog("Are you sure you want to exit?", Gdx.app::exit)

    inner class NavProfile {
        val navButton = ImageTextButton(
            "Profile",
            ImageTextButtonStyle(null, null, null, BitmapFont())
        ).apply {
            addListener(ClickListener {
                contentSplitPane.setSecondWidget(null)
                header.setFirstWidget(HorizontalGroup())
                navGame.uncheck()
            })
        }
    }

    inner class NavRating {
        private val radioButton = RadioButton(
            true, "By level", "By wins", "By kills"
        )
        private val sortButtons = HorizontalGroup().apply {
            left().space(getHeightPerc(.05f)).padLeft(getHeightPerc(.05f))
            for (button in radioButton.buttons)
                this += button
        }
        val navButton = ImageTextButton(
            "Rating",
            ImageTextButtonStyle(null, null, null, BitmapFont())
        ).apply {
            addListener(ClickListener {
                contentSplitPane.setSecondWidget(null)
                header.setFirstWidget(sortButtons)
                navGame.uncheck()
            })
        }
    }

    private val header = ContentHeader(this, null, 0.9f)
    private val contentSplitPane = SplitPane(header, null, true, 0.2f)
    private val navGame = NavGame(loader, defaultSkin, contentSplitPane, header)
    private val navigation = Table().apply {
        center().add(navGame.navButton).expand().row()
        add(NavProfile().navButton).expand().row()
        add(NavRating().navButton).expand()
    }
    private val mainSplitPane = SplitPane(
        navigation, contentSplitPane, false, 0.15f
    ).apply {
        setFillParent(true)
    }

    init {
        isDebugAll = true
        this += mainSplitPane
        loader.addScreen(screen = GameScreen(loader, this))
    }

    override fun show() {
        super.show()
        Loader.logger.debug { "Main menu screen is shown..." }
        val registrationScreen = when {
            loader.containsScreen<RegistrationScreen>() -> loader.getScreen<RegistrationScreen>()
            else -> {
                loader.addScreen(screen = RegistrationScreen(loader))
                null
            }
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
