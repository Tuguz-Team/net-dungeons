package com.tuguzteam.netdungeons.screens.auth

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.*
import com.tuguzteam.netdungeons.dec
import com.tuguzteam.netdungeons.heightFraction
import com.tuguzteam.netdungeons.ui.*
import com.tuguzteam.netdungeons.widthFraction

class AuthContent(
    stage: Stage, headerText: String,
    buttonText: String, clickListener: ClickListener,
    parent: Container<Actor>, buttonTable: VisTable,
    private val passwordTextField: ExtValidTextField,
    private val textFields: Iterable<ExtValidTextField>
) : VisTable(true) {

    private var textFieldsStates = mutableListOf<Pair<String, String>>()
    private val policyCheckBox = VisCheckBox("Accept privacy policy").apply {
        label.setAlignment(Align.center)
        imageStackCell.size(heightFraction(.0375f))
        labelCell.width(heightFraction(.375f)).grow()

        addListener(ClickListener {
            setStateInvalid(!isChecked)
            anyError()
        })
    }

    private val authButton = VisTextButton(buttonText).apply {
        isFocusBorderEnabled = false
        addListener(ClickListener {
            textFields.forEach(ExtValidTextField::setEmptyError)
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

    private val textArea = VisLabel(
        "\nJust enjoy the game!!!\n".repeat(50)
    ).apply { wrap = true }

    private val policyWindow = Dialog("Privacy Policy").apply dialog@{
        buttonsTable.add(VisTextButton("I accept!").apply {
            addListener(ClickListener {
                if (policyCheckBox.setStateInvalid())
                    doClick(policyCheckBox)
                this@dialog.hide()
            })
        })
        button("Close", false)

        contentTable.add(ScrollPane(textArea).apply {
            setScrollingDisabled(true, false)
            setOverscroll(false, false)
        }).pad(heightFraction(.05f)).size(
            widthFraction(.65f), heightFraction(.35f)
        )

        size().pad()
    }

    private val viewPolicyButton = VisTextButton(" ? ").apply {
        isFocusBorderEnabled = false
        addListener(ClickListener { policyWindow.show(stage) })
    }

    val radioButton = VisTextButton(headerText, "toggle").apply {
        addListener(ClickListener {
            this@AuthContent.clearChildren()

            if (textFields.count() > 1) {
                parent.actor = SplitPane(null, null, false).apply {
                    setFirstWidget(
                        Container(
                            VisTable().apply {
                                textFields.forEach { textField -> textField.addTo(this) }
                            }).pad(
                            0f, heightFraction(.025f),
                            0f, heightFraction(.05f)
                        ).fill()
                    )
                    setSecondWidget(
                        Container(
                            VisTable().apply {
                                passwordTextField.addTo(this, viewPasswordButton)
                                add(policyCheckBox).padTop(heightFraction(.025f))
                                add(viewPolicyButton).size(
                                    heightFraction(.075f), heightFraction(.05f)
                                ).padTop(heightFraction(.025f)).row()
                            }).pad(
                            0f, heightFraction(.05f),
                            0f, heightFraction(.025f)
                        ).fill()
                    )
                }
            } else {
                parent.actor = VisTable().apply {
                    pad(
                        0f, heightFraction(.375f),
                        0f, heightFraction(.375f)
                    )
                    textFields.forEach { textField -> textField.addTo(this) }
                    passwordTextField.addTo(this, viewPasswordButton)
                }
            }
            buttonTable.clearChildren()
            buttonTable.add(authButton).grow()
                .size(heightFraction(.75f), heightFraction(.1f))

            updateState()
            anyError()
        })
    }

    init {
        authButton.addListener(ClickListener { anyError() })
        policyCheckBox.setStateInvalid(true)

        center()
    }

    private fun updateState() {
        passwordTextField.isPasswordMode = !viewPasswordButton.isChecked

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
                    || (policyCheckBox.setStateInvalid() && textFields.count() > 1)

        if (authButton.isDisabled)
            authButton.touchable = Touchable.disabled
    }
}
