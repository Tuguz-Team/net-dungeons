package com.tuguzteam.netdungeons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class MainMenuFragment : Fragment() {
    companion object Static {
        @JvmStatic
        fun newInstance(): MainMenuFragment {
            val args = Bundle()
            val fragment = MainMenuFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }
}
