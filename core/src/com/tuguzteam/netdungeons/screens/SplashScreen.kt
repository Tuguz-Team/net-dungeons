package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.assets.Asset
import com.tuguzteam.netdungeons.assets.TextureAsset
import com.tuguzteam.netdungeons.isDoneActing
import ktx.actors.centerPosition
import ktx.actors.plusAssign
import ktx.actors.then
import ktx.log.info

class SplashScreen(loader: Loader) : StageScreen(loader) {
    companion object {
        private val assets: Array<Asset> = arrayOf(TextureAsset.LogoLibGDX)
    }

    private val assetManager = loader.assetManager.apply {
        loadNow(*assets)
    }
    private val logoTexture = assetManager[TextureAsset.LogoLibGDX]?.apply {
        setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
    }
    private val logoImage = Image(logoTexture).apply {
        centerPosition(this@SplashScreen.width, this@SplashScreen.height)
        this += alpha(0f) then fadeIn(1.25f) then delay(1.5f) then fadeOut(0.75f)
        this@SplashScreen += this
    }

    init {
        Gdx.input.inputProcessor = this
    }

    override fun show() {
        Loader.logger.info { "Splash screen in shown..." }
    }

    override fun render(delta: Float) {
        act(delta)
        draw()
        if (logoImage.isDoneActing()) {
            goToMainMenu()
        }
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        goToMainMenu()
        return super.touchUp(screenX, screenY, pointer, button)
    }

    private fun goToMainMenu() {
        loader.addScreen(screen = GameScreen(loader))
        loader.setScreen<GameScreen>()
        loader.removeScreen<SplashScreen>()?.dispose()
    }

    override fun dispose() {
        super.dispose()
        assetManager.unload(*assets)
    }
}
