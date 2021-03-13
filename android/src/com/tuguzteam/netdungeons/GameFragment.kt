package com.tuguzteam.netdungeons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication

class GameFragment : AndroidFragmentApplication() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val config = AndroidApplicationConfiguration().apply {
            a = 8
            useAccelerometer = false
            useCompass = false
        }
        return initializeForView(NetDungeonsGame(), config)
    }
}
