package com.tuguzteam.netdungeons

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import com.kotcrab.vis.ui.VisUI
import com.tuguzteam.netdungeons.assets.*
import com.tuguzteam.netdungeons.net.AuthManager
import com.tuguzteam.netdungeons.net.GameManager
import com.tuguzteam.netdungeons.objects.GameObject
import com.tuguzteam.netdungeons.screens.SplashScreen
import com.tuguzteam.netdungeons.screens.StageScreen
import kotlinx.coroutines.launch
import ktx.app.KtxGame
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.logger
import kotlin.random.asKotlinRandom

class Loader(val authManager: AuthManager, val gameManager: GameManager) : KtxGame<StageScreen>() {
    companion object {
        val random = MathUtils.random.asKotlinRandom()
        val logger = logger<Loader>()

        val requiredAssets = listOf<Asset>(
            SkinAsset.Default,
            I18NBundleAsset.Default,
            TextureAtlasAsset.All,
        )
    }

    val assetManager by lazy { AssetManager() }

    override fun create() {
        KtxAsync.initiate()
        Gdx.app.logLevel = Application.LOG_DEBUG
        Gdx.input.setCatchKey(Input.Keys.BACK, true)

        logger.debug { "Loader is creating now..." }
        KtxAsync.launch {
            VisUI.load("tixel/x1/tixel.json")
            assetManager.load(TextureAsset.LogoLibGDX)
            addScreen(screen = SplashScreen(this@Loader))
            setScreen<SplashScreen>()

            assetManager.load(requiredAssets)
        }
    }

    override fun dispose() {
        super.dispose()
        assetManager.dispose()
        GameObject.dispose()
        VisUI.dispose()
    }
}
