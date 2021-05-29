package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.Gdx
import com.kotcrab.vis.ui.widget.VisTable
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.net.Result
import com.tuguzteam.netdungeons.ui.SplitPane
import com.tuguzteam.netdungeons.ui.YesNoDialog
import com.tuguzteam.netdungeons.ui.navigation.ContentHeader
import com.tuguzteam.netdungeons.ui.navigation.NavGame
import com.tuguzteam.netdungeons.ui.navigation.NavProfile
import com.tuguzteam.netdungeons.ui.navigation.NavRating
import kotlinx.coroutines.launch
import ktx.actors.plusAssign
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.error
import ktx.log.info

class MainScreen(loader: Loader) : StageScreen(loader) {
    private val yesNoDialog = YesNoDialog("Are you sure you want to exit?", Gdx.app::exit)

    private val header = ContentHeader(this, null, 0.9f)
    private val contentSplitPane = SplitPane(header, null, true, 0.2f)

    private val navGame = NavGame(loader, contentSplitPane, header)
    private val navProfile = NavProfile(contentSplitPane, header)
    private val navRating = NavRating(contentSplitPane, header)

    private val navigation = VisTable().apply {
        center().add(navGame.navButton).expand().row()
        add(navProfile.navButton).expand().row()
        add(navRating.navButton).expand()
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
            loader.containsScreen<AuthScreen>() -> loader.getScreen<AuthScreen>()
            else -> {
                loader.addScreen(screen = AuthScreen(loader))
                null
            }
        }
        KtxAsync.launch {
            when (val result = loader.authManager.syncUser()) {
                is Result.Cancel -> Loader.logger.info { "Task was cancelled normally" }
                is Result.Failure -> Loader.logger.error(result.cause) { "User update failure" }
                is Result.Success -> {
                    val data = result.data
                    if (registrationScreen == null && data == null) {
                        loader.setScreen<AuthScreen>()
                    } else {
                        Loader.logger.debug(data::toString)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (yesNoDialog.isHidden) yesNoDialog.show(this)
    }
}
