package com.tuguzteam.netdungeons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.tuguzteam.netdungeons.net.AndroidAuthManager
import com.tuguzteam.netdungeons.net.AndroidGameManager

@Suppress("unused")
class GameFragment : AndroidFragmentApplication() {
    private val loader = Loader(AndroidAuthManager, AndroidGameManager)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val config = AndroidApplicationConfiguration().apply {
            a = 8
            useAccelerometer = false
            useCompass = false
        }
        return initializeForView(loader, config)
    }
}
