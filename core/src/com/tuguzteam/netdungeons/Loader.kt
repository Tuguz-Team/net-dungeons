package com.tuguzteam.netdungeons

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.I18NBundle
import com.tuguzteam.netdungeons.assets.*
import com.tuguzteam.netdungeons.net.NetworkManager
import com.tuguzteam.netdungeons.screens.SplashScreen
import com.tuguzteam.netdungeons.screens.StageScreen
import kotlinx.coroutines.launch
import ktx.app.KtxGame
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.logger

class Loader(val networkManager: NetworkManager) : KtxGame<StageScreen>() {
    companion object {
        val logger = logger<Loader>()
        val requiredAssets: Array<out Asset> = arrayOf(SkinAsset.Default, I18NBundleAsset.Default)
    }

    init {
        KtxAsync.initiate()
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
        KtxAsync.launch {
            assetManager.load(TextureAsset.LogoLibGDX)
            addScreen(screen = SplashScreen(this@Loader))
            setScreen<SplashScreen>()

            assetManager.load(*requiredAssets)
            defaultSkin = assetManager[SkinAsset.Default]!!
            defaultBundle = assetManager[I18NBundleAsset.Default]!!
        }
    }

    override fun dispose() {
        super.dispose()
        assetManager.dispose()
    }
}
