package com.tuguzteam.netdungeons.ui.auth

import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.tuguzteam.netdungeons.dec
import com.tuguzteam.netdungeons.getHeightPerc
import com.tuguzteam.netdungeons.ui.ClickListener

class AuthContent(
    context: String, clickListener: ClickListener, parent: VisTable,
    private val passwordTextField: ExtValidTextField,
    private vararg val textFields: ExtValidTextField
) : VisTable(true) {

    private val authButton = VisTextButton(context).apply {
        addListener(ClickListener {
            textFields.forEach { textField -> textField.setEmptyError() }
            passwordTextField.setEmptyError()
        })
        addListener(clickListener)
    }
    private val viewPasswordButton = VisTextButton("<  >", "toggle").apply {
        isFocusBorderEnabled = false
        addListener(ClickListener {
            passwordTextField.isPasswordMode--
        }) }

//    val radioButton = VisTextButton(context, "toggle").apply {
//        addListener(ClickListener {
//            if (!isChecked) {
//                clearChildren()
//                addChildren()
//
//                parent.clearChildren()
//                parent.add(this@AuthContent)
//            }
//        })
//    }

    val radioButton = Pair(context, ClickListener {
        clearChildren()
        addChildren()

        parent.clearChildren()
        parent.add(this)
    })

    init {
        center().padTop(getHeightPerc(.005f))
        addChildren()
    }

    private fun addChildren() {
        textFields.forEach { textField -> textField.addTo(this) }
        passwordTextField.addTo(this, viewPasswordButton)
        add(authButton).colspan(2).fillX().pad(getHeightPerc(.025f))
    }
}