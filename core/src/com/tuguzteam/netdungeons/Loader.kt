package com.tuguzteam.netdungeons

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.I18NBundle
import com.tuguzteam.netdungeons.assets.Asset
import com.tuguzteam.netdungeons.assets.AssetManager
import com.tuguzteam.netdungeons.assets.I18NBundleAsset
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

        val requiredAssets: Array<out Asset> = arrayOf(SkinAsset.Default, I18NBundleAsset.Default)
    }

    val assetManager = AssetManager()

    lateinit var defaultSkin: Skin
        private set
    lateinit var defaultBundle: I18NBundle
        private set

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        Gdx.input.setCatchKey(Input.Keys.BACK, true)

        logger.debug { "Loader is creating now..." }
        assetManager.addLoadTask(*requiredAssets) {
            defaultSkin = assetManager[SkinAsset.Default]!!
            defaultBundle = assetManager[I18NBundleAsset.Default]!!
        }

        addScreen(screen = SplashScreen(this))
        setScreen<SplashScreen>()
    }

    override fun dispose() {
        super.dispose()
        assetManager.dispose()
    }
}
