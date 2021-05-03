package com.tuguzteam.netdungeons

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.tuguzteam.netdungeons.assets.AssetManager
import com.tuguzteam.netdungeons.screens.SplashScreen
import com.tuguzteam.netdungeons.screens.StageScreen
import ktx.app.KtxGame
import ktx.log.info
import ktx.log.logger

class Loader : KtxGame<StageScreen>() {
    companion object {
        val logger = logger<Loader>()
    }

    val assetManager = AssetManager()

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        logger.info { "Loader is creating now..." }

        addScreen(screen = SplashScreen(this))
        setScreen<SplashScreen>()
    }

    override fun dispose() {
        super.dispose()
        assetManager.dispose()
    }
}
