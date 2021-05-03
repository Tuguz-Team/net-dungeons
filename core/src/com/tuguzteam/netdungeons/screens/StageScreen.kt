package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.scenes.scene2d.Stage
import com.tuguzteam.netdungeons.Loader
import ktx.app.KtxScreen

open class StageScreen(val loader: Loader) : Stage(), KtxScreen {
    override fun dispose() {
        super<Stage>.dispose()
    }
}
