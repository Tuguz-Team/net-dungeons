package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.KtxScreen

open class Screen : Stage(), KtxScreen {
    override fun dispose() {
        super<Stage>.dispose()
    }
}
