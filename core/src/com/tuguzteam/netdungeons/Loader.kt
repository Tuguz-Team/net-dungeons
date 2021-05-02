package com.tuguzteam.netdungeons

import com.badlogic.gdx.Application
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.tuguzteam.netdungeons.screens.GameScreen

class Loader : Game() {
    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG

        screen = GameScreen()
    }
}
