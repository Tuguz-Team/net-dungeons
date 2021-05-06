package com.tuguzteam.netdungeons

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.tuguzteam.netdungeons.assets.AssetManager
import com.tuguzteam.netdungeons.assets.SkinAsset
import com.tuguzteam.netdungeons.net.NetworkManager
import com.tuguzteam.netdungeons.screens.SplashScreen
import com.tuguzteam.netdungeons.screens.StageScreen
import kotlinx.coroutines.CoroutineScope
import ktx.app.KtxGame
import ktx.log.debug
import ktx.log.logger

class Loader(val networkManager: NetworkManager, val coroutineScope: CoroutineScope) :
    KtxGame<StageScreen>() {

    companion object {
        val logger = logger<Loader>()
    }

    val assetManager = AssetManager()

    lateinit var defaultSkin: Skin
        private set

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        Gdx.input.setCatchKey(Input.Keys.BACK, true)

        logger.debug { "Loader is creating now..." }
        assetManager.loadNow(SkinAsset.Default)
        defaultSkin = assetManager[SkinAsset.Default]!!

        addScreen(screen = SplashScreen(this))
        setScreen<SplashScreen>()
    }

    override fun dispose() {
        super.dispose()
        assetManager.dispose()
    }
}
