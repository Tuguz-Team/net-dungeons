package com.tuguzteam.netdungeons.screens

import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.dec
import com.tuguzteam.netdungeons.getHeightPerc
import com.tuguzteam.netdungeons.net.NetworkManager.Companion.EMAIL_REGEX
import com.tuguzteam.netdungeons.net.NetworkManager.Companion.NAME_REGEX
import com.tuguzteam.netdungeons.net.NetworkManager.Companion.PASSWORD_REGEX
import com.tuguzteam.netdungeons.net.Result
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.ExtValidTextField
import com.tuguzteam.netdungeons.ui.KeyTypeListener
import com.tuguzteam.netdungeons.ui.YesNoDialog
import kotlinx.coroutines.launch
import ktx.actors.centerPosition
import ktx.actors.plusAssign
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.error
import ktx.log.info

class RegistrationScreen(loader: Loader) : StageScreen(loader) {
    private val yesNoDialog = YesNoDialog(
        "Cancel registration?",
        onYesOption = { loader.setScreen<MainScreen>() }
    )
    private val nameTextField = ExtValidTextField(
        NAME_REGEX, "Enter your name",
        "name error", "empty name",
    )
    private val emailTextField = ExtValidTextField(
        EMAIL_REGEX, "Enter your email",
        "email error", "empty email"
    )
    private val passwordTextField = ExtValidTextField(
        PASSWORD_REGEX, "Enter your password",
        "password error", "empty password"
    ).apply {
        setPasswordMode('*')
    }

    init {
        nameTextField.setTextFieldListener(
            KeyTypeListener { nameTextField.setInputError() }
        )
        emailTextField.setTextFieldListener(
            KeyTypeListener { emailTextField.setInputError() }
        )
        passwordTextField.setTextFieldListener(
            KeyTypeListener { passwordTextField.setInputError() }
        )
    }

    private val registerButton = VisTextButton("Register").apply {
        addListener(ClickListener {
            nameTextField.setEmptyError()
            emailTextField.setEmptyError()
            passwordTextField.setEmptyError()
            KtxAsync.launch {
                when (val result = loader.networkManager.register(
                    nameTextField.text, emailTextField.text, passwordTextField.text
                )) {
                    is Result.Cancel -> Loader.logger.info { "Task was cancelled normally" }
                    is Result.Failure -> Loader.logger.error(result.cause) { "Register failure!" }
                    is Result.Success -> loader.setScreen<MainScreen>()
                }
            }
        })
    }

    private val registrationContent = VisTable(true).apply {
        center().padTop(getHeightPerc(1 / 20f))
        nameTextField.addTo(this)
        emailTextField.addTo(this)
        passwordTextField.addTo(this,
            VisTextButton("<  >", "toggle").apply {
                isFocusBorderEnabled = false
                addListener(ClickListener {
                    passwordTextField.isPasswordMode--
                })
        })
        add(registerButton).colspan(2).fillX().pad(getHeightPerc(.05f))
    }

    init {
        isDebugAll = true
        this += registrationContent
        registrationContent.centerPosition()
    }

    override fun show() {
        super.show()
        Loader.logger.debug { "Registration screen is shown..." }
    }

    override fun onBackPressed() {
        if (yesNoDialog.isHidden) yesNoDialog.show(this)
    }
}
