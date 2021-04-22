package com.tuguzteam.netdungeons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.google.android.material.button.MaterialButton

class MainMenuFragment : Fragment(R.layout.fragment_main_menu) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View? = super.onCreateView(inflater, container, savedInstanceState)

        val materialButton: MaterialButton? = view?.findViewById(R.id.fragment_main_menu_button)
        materialButton?.setOnClickListener {
            parentFragmentManager.commit {
                setReorderingAllowed(true)
                val gameFragment = parentFragmentManager.findFragmentByTag(GameFragment.TAG)
                if (gameFragment == null) {
                    add<GameFragment>(R.id.activity_main_fragment_container, GameFragment.TAG)
                } else {
                    show(gameFragment)
                }
                addToBackStack(null)
            }
        }

        return view
    }
}
