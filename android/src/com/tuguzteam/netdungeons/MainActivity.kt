package com.tuguzteam.netdungeons

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

import com.badlogic.gdx.backends.android.AndroidFragmentApplication

class MainActivity : FragmentActivity(), AndroidFragmentApplication.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun exit() {
        TODO("Not yet implemented")
    }
}
