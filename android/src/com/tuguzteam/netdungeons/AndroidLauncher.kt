package com.tuguzteam.netdungeons

import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup libGDX configuration and initialize it
        val config = AndroidApplicationConfiguration().apply {
            a = 8
            useAccelerometer = false
            useCompass = false
            hideStatusBar = true
        }
        initialize(NetDungeonsGame(), config)

        // Set view to main menu
        setContentView(R.layout.activity_launcher)
    }
}
