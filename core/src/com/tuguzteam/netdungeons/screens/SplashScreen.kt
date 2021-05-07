package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.assets.TextureAsset
import com.tuguzteam.netdungeons.isDoneActing
import kotlinx.coroutines.runBlocking
import ktx.actors.centerPosition
import ktx.actors.plusAssign
import ktx.actors.then
import ktx.log.debug

class SplashScreen(loader: Loader) : StageScreen(loader) {
    private val assetManager = loader.assetManager

    private val logoTexture = assetManager[TextureAsset.LogoLibGDX]!!.apply {
        setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
    }
    private val logoImage = Image(logoTexture).apply {
        this += alpha(0f) then fadeIn(1.25f) then delay(1.5f) then fadeOut(0.75f)
    }

    init {
        this += logoImage
        logoImage.centerPosition()
    }

    override fun show() {
        super.show()
        Loader.logger.debug { "Splash screen in shown..." }
    }

    override fun render(delta: Float) {
        super.render(delta)
        if (logoImage.isDoneActing() && assetManager.loaded(*Loader.requiredAssets)) {
            goToMainScreen()
        }
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (assetManager.loaded(*Loader.requiredAssets)) goToMainScreen()
        return super.touchUp(screenX, screenY, pointer, button)
    }

    private fun goToMainScreen() {
        loader.addScreen(screen = MainScreen(loader))
        loader.setScreen<MainScreen>()
        loader.removeScreen<SplashScreen>()?.dispose()
    }

    override fun dispose() {
        super.dispose()
        runBlocking {
            assetManager.unload(TextureAsset.LogoLibGDX)
        }
    }
}
