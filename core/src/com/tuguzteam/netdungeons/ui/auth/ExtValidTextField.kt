package com.tuguzteam.netdungeons.ui.auth

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisValidatableTextField
import com.tuguzteam.netdungeons.getHeightPerc

class ExtValidTextField(
    regex: Regex, infoText: String, private val inputError: String,
    private val emptyError: String? = null, align: Int = Align.center
) : VisValidatableTextField() {
    private var infoLabel = VisLabel(infoText, align)
    private var errorLabel = VisLabel(null, align).apply {
        color = Color.RED
    }

    init {
        setAlignment(align)
        addValidator { this.text matches regex || this.text.isEmpty() }
    }

    fun setPasswordMode(passwordChar: Char) {
        isPasswordMode = true
        setPasswordCharacter(passwordChar)
    }

    fun addTo(table: Table, fieldButton: Button? = null) {
        errorLabel.setText(null)
        setText(null)
        if (fieldButton == null) {
            table.add(infoLabel).colspan(2).fillX().row()
            table.add(this).colspan(2).fillX().row()
            table.add(errorLabel).colspan(2).fillX().row()
        } else {
            table.add(infoLabel).colspan(2).fillX().row()
            table.add(this)
            table.add(fieldButton).spaceLeft(getHeightPerc(1 / 60f)).row()
            table.add(errorLabel).colspan(2).fillX().row()
        }
    }

    fun getState() = Pair(text, errorLabel.text.toString())

    fun setState(state: Pair<String, String>) {
        setText(state.first)
        errorLabel.setText(state.second)
    }

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
