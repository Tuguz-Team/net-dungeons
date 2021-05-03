package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.Input
import com.tuguzteam.netdungeons.Loader

open class ReturnableScreen(loader: Loader, private val prevScreen: StageScreen) :
    StageScreen(loader) {
    override fun keyDown(keyCode: Int): Boolean {
        if (keyCode == Input.Keys.BACK) {
            goToPreviousScreen()
            return true
        }
        return super.keyDown(keyCode)
    }

    private fun goToPreviousScreen() {
        loader.setScreen(prevScreen.javaClass)
    }
}
