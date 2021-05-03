package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.assets.SkinAsset
import com.tuguzteam.netdungeons.ui.ButtonListener
import com.tuguzteam.netdungeons.ui.YesNoDialog
import ktx.actors.centerPosition
import ktx.actors.plusAssign
import ktx.log.debug

class MainScreen(loader: Loader) : StageScreen(loader) {
    private val defaultSkin = loader.assetManager[SkinAsset.Default]!!
    private val yesNoDialog = YesNoDialog("Are you sure you want to exit?", defaultSkin) {
        Gdx.app.exit()
    }
    private val button = TextButton("Go to game screen", defaultSkin).apply {
        addListener(ButtonListener {
            loader.setScreen<GameScreen>()
        })
    }

    init {
        loader.addScreen(screen = GameScreen(loader, this))
        this += button
        button.centerPosition()
    }

    override fun show() {
        super.show()
        Loader.logger.debug { "Main menu screen is shown..." }
    }

    override fun keyDown(keyCode: Int): Boolean {
        if (keyCode == Input.Keys.BACK && yesNoDialog.isHidden) {
            yesNoDialog.show(this)
            return true
        }
        return false
    }
}
