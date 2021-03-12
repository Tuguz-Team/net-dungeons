package com.tuguzteam.netdungeons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.button.MaterialButton

class MainMenuFragment : Fragment() {
    private lateinit var materialButton: MaterialButton

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main_menu, container, false)
        materialButton = view.findViewById(R.id.fragment_main_menu_button)
        materialButton.setOnClickListener {
            parentFragmentManager.commit {
                setReorderingAllowed(true)
                replace<GameFragment>(R.id.activity_main_fragment_container)
                addToBackStack(null)
            }
        }
        return view
    }
}
