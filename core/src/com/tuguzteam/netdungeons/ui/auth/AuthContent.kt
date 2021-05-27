package com.tuguzteam.netdungeons.ui.auth

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.tuguzteam.netdungeons.dec
import com.tuguzteam.netdungeons.heightFraction
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.KeyTypeListener

class AuthContent(
    context: String, clickListener: ClickListener, parent: VisTable,
    private val passwordTextField: ExtValidTextField,
    private val textFields: Iterable<ExtValidTextField>
) : VisTable(true) {

    private var textFieldsStates = mutableListOf<Pair<String, String>>()

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

    private var wasChecked = false
    val radioButton = VisTextButton(context, "toggle").apply {
        addListener(ClickListener {
            if (!wasChecked) {
                this@AuthContent.clearChildren()
                this@AuthContent.addChildren()
                wasChecked--
                parent.clearChildren()
                parent.add(this@AuthContent)
            }
            updateState()
            anyError()
        })
    }

    init {
        authButton.addListener(ClickListener { anyError() })
        addChildren()
        center()
    }

    private fun updateState() {
        passwordTextField.isPasswordMode = !viewPasswordButton.isChecked
        wasChecked = false

        textFields.forEach { textField ->
            textField.setTextFieldListener(KeyTypeListener {
                textField.setInputError()
                storeState()
                anyError()
            })
        }
        passwordTextField.setTextFieldListener(KeyTypeListener {
            passwordTextField.setInputError()
            storeState()
            anyError()
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

    private fun anyError() {
        authButton.isDisabled = false
        authButton.touchable = Touchable.enabled

        textFields.forEach { textField ->
            if (!authButton.isDisabled)
                authButton.isDisabled = textField.isError()
        }
        if (!authButton.isDisabled)
            authButton.isDisabled = passwordTextField.isError()

        if (authButton.isDisabled)
            authButton.touchable = Touchable.disabled
    }

    private fun addChildren() {
        textFields.forEach { textField -> textField.addTo(this) }
        passwordTextField.addTo(this, viewPasswordButton)
        add(authButton).colspan(2)
            .fillX().pad(heightFraction(.025f))
    }
}
