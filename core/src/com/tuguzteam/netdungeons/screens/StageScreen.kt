package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.tuguzteam.netdungeons.Loader
import ktx.app.KtxScreen

open class StageScreen(val loader: Loader) : Stage(), KtxScreen {
    protected val inputMultiplexer = InputMultiplexer()

    override fun show() {
        inputMultiplexer.addProcessor(this)
        Gdx.input.inputProcessor = inputMultiplexer
    }

    override fun render(delta: Float) {
        act(delta)
        draw()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun hide() {
        inputMultiplexer.clear()
    }

    override fun dispose() {
        super<Stage>.dispose()
    }

    protected open fun onBackPressed() = Unit

    override fun keyDown(keyCode: Int): Boolean {
        if (keyCode == Input.Keys.BACK) {
            onBackPressed()
            return true
        }
        return super.keyDown(keyCode)
    }
}
