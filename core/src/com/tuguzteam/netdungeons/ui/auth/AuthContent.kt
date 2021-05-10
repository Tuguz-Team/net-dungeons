package com.tuguzteam.netdungeons.ui.auth

import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.tuguzteam.netdungeons.dec
import com.tuguzteam.netdungeons.getHeightPerc
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.KeyTypeListener

class AuthContent(
    context: String, clickListener: ClickListener, parent: VisTable,
    private val passwordTextField: ExtValidTextField,
    private vararg val textFields: ExtValidTextField
) : VisTable(true) {

    var textFieldsStates = mutableListOf<Pair<String, String>>()

    private val authButton = VisTextButton(context).apply {
        isFocusBorderEnabled = false
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
        })
    }

    var wasChecked = false
    val radioButton = VisTextButton(context, "toggle").apply {
        addListener(ClickListener {
            if (!wasChecked) {
                this@AuthContent.clearChildren()
                this@AuthContent.addChildren()
                wasChecked--
                parent.clearChildren()
                parent.add(this@AuthContent)
            }
        })
    }

    init {
        center().padTop(getHeightPerc(.005f))
        addChildren()
    }

    fun updateState() {
        passwordTextField.isPasswordMode = !viewPasswordButton.isChecked
        wasChecked = false

        textFields.forEach { textField ->
            textField.setTextFieldListener(KeyTypeListener {
                textField.setInputError()
                storeState()
            }) }
        passwordTextField.setTextFieldListener(KeyTypeListener {
            passwordTextField.setInputError()
            storeState()
        })

        setState()
    }

    private fun storeState() {
        textFieldsStates = mutableListOf()

        textFields.forEach { textField ->
            textFieldsStates += textField.getState()
        }
        textFieldsStates += passwordTextField.getState()
    }

    private fun setState() {
        if (textFieldsStates.isNotEmpty()) {
            textFields.forEachIndexed { index, textField ->
                textField.setState(textFieldsStates[index])
            }
            passwordTextField.setState(textFieldsStates.last())
        }
    }

    private fun addChildren() {
        textFields.forEach { textField -> textField.addTo(this) }
        passwordTextField.addTo(this, viewPasswordButton)
        add(authButton).colspan(2).fillX().pad(getHeightPerc(.025f))
    }
}