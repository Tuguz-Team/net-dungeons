package com.tuguzteam.netdungeons

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.tuguzteam.netdungeons.screens.GameScreen
import com.tuguzteam.netdungeons.screens.Screen
import ktx.app.KtxGame
import ktx.log.info
import ktx.log.logger

class Loader : KtxGame<Screen>() {
    companion object {
        val logger = logger<Loader>()
    }

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        logger.info { "Logger is creating now..." }

        addScreen(screen = GameScreen(this))
        setScreen<GameScreen>()
        super.create()
    }
}
