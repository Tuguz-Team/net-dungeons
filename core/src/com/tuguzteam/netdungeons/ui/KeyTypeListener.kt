package com.tuguzteam.netdungeons.ui

import com.kotcrab.vis.ui.widget.VisTextField

class KeyTypeListener(private val onKeyType: () -> Unit) : VisTextField.TextFieldListener {
    override fun keyTyped(textField: VisTextField?, c: Char) = onKeyType()
}
