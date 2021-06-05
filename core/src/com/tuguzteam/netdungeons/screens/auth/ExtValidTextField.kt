package com.tuguzteam.netdungeons.screens.auth

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisValidatableTextField
import com.tuguzteam.netdungeons.heightFraction

class ExtValidTextField(
    regex: Regex, infoText: String,
    private val inputError: String,
    private val emptyError: String? = null,
    passwordMode: Boolean = false,
    align: Int = Align.center,
) : VisValidatableTextField() {

    private var infoLabel = VisLabel(infoText, align)
    private var errorLabel = VisLabel(null, align).apply {
        color = Color.RED
    }

    init {
        isPasswordMode = passwordMode
        if (passwordMode) setPasswordCharacter('*')

        setAlignment(align)
        addValidator { this.text matches regex || this.text.isEmpty() }
    }

    fun addTo(table: VisTable, button: VisTextButton? = null) {
        errorLabel.setText(null)
        setText(null)

        val cell = table.add(infoLabel).size(0f, heightFraction(.075f)).growX()

        if (button == null) cell.colspan(2).row()
        else table.add(button).size(
            heightFraction(.075f), heightFraction(.05f)
        ).padRight(.025f).row()

        table.add(this).colspan(2).size(0f, heightFraction(.075f)).growX().row()
        table.add(errorLabel).size(0f, heightFraction(.05f))
            .colspan(2).growX().row()
    }

    fun getState() = Pair(text, errorLabel.text.toString())

    fun setState(state: Pair<String, String>) {
        setText(state.first)
        errorLabel.setText(state.second)
    }

    fun isError() = errorLabel.textEquals(inputError) || errorLabel.textEquals(emptyError)

    fun setInputError() {
        if (isInputValid && !errorLabel.textEquals(emptyError))
            errorLabel.setText(null)
        else
            errorLabel.setText(inputError)
    }

    fun setEmptyError() {
        if (text.isEmpty()) errorLabel.setText(emptyError)
    }
}
