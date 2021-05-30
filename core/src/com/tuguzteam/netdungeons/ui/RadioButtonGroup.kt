package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.kotcrab.vis.ui.widget.VisTextButton

class RadioButtonGroup private constructor(private val ifChecked: Boolean) {
    private lateinit var controller: ButtonGroup<VisTextButton>
    val groupButtons = mutableListOf<VisTextButton>()

    constructor(checked: Boolean, buttonInstances: Iterable<Any>) : this(checked) {
        groupButtons += buttonInstances.map { instance ->
            when (instance) {
                is String -> VisTextButton(instance, "toggle").apply {
                    isFocusBorderEnabled = false
                }
                is VisTextButton -> instance.apply {
                    isFocusBorderEnabled = false
                }
                else -> TODO("Other variants are unnecessary")
            }
        }
        initController()
    }

    private fun initController() {
        controller = ButtonGroup(*groupButtons.toTypedArray()).apply {
            if (ifChecked && groupButtons.isNotEmpty())
                doClick(groupButtons[0])
        }
    }

    fun uncheck() = controller.uncheckAll()

    fun anyChecked() = groupButtons.find { button -> button.isChecked } != null
}
