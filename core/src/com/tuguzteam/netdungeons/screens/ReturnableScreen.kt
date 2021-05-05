package com.tuguzteam.netdungeons.screens

import com.tuguzteam.netdungeons.Loader

open class ReturnableScreen(loader: Loader, private val prevScreen: StageScreen) :
    StageScreen(loader) {

    override fun onBackPressed() {
        goToPreviousScreen()
    }

    protected fun goToPreviousScreen() {
        loader.setScreen(prevScreen.javaClass)
    }
}
