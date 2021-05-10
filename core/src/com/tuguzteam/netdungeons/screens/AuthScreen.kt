package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.kotcrab.vis.ui.widget.VisTable
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.getHeightPerc
import com.tuguzteam.netdungeons.net.AuthManager.Companion.EMAIL_REGEX
import com.tuguzteam.netdungeons.net.AuthManager.Companion.NAME_REGEX
import com.tuguzteam.netdungeons.net.AuthManager.Companion.PASSWORD_REGEX
import com.tuguzteam.netdungeons.net.Result
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.RadioButtonGroup
import com.tuguzteam.netdungeons.ui.YesNoDialog
import com.tuguzteam.netdungeons.ui.auth.AuthContent
import com.tuguzteam.netdungeons.ui.auth.ExtValidTextField
import kotlinx.coroutines.launch
import ktx.actors.centerPosition
import ktx.actors.plusAssign
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.error
import ktx.log.info

class AuthScreen(loader: Loader) : StageScreen(loader) {
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
    ).apply { setPasswordMode('*') }

    private val optionContent = VisTable(true)
    private val registerContent = AuthContent("Register", ClickListener {
        KtxAsync.launch {
            val name = nameTextField.text
            val email = emailTextField.text
            val password = passwordTextField.text
            when (val result = loader.authManager.register(name, email, password)) {
                is Result.Cancel -> Loader.logger.info { "Task was cancelled normally" }
                is Result.Failure -> Loader.logger.error(result.cause) { "Registration failure!" }
                is Result.Success -> loader.setScreen<MainScreen>()
            }
        }
    }, optionContent, passwordTextField, nameTextField, emailTextField)

    private val loginContent = AuthContent("Login", ClickListener {
        KtxAsync.launch {
            val email = emailTextField.text
            val password = passwordTextField.text
            when (val result = loader.authManager.signIn(email, password)) {
                is Result.Cancel -> Loader.logger.info { "Task was cancelled normally" }
                is Result.Failure -> Loader.logger.error(result.cause) { "Sign in failure!" }
                is Result.Success -> loader.setScreen<MainScreen>()
            }
        }
    }, optionContent, passwordTextField, emailTextField)

    private val radioButton = RadioButtonGroup(true, true,
        registerContent.radioButton.apply {
            addListener(ClickListener { registerContent.updateState() })
        },
        loginContent.radioButton.apply {
            addListener(ClickListener { loginContent.updateState() })
        }
    )
    private val chooseOptionButtons = HorizontalGroup().apply {
        center().space(getHeightPerc(.05f))
        for (button in radioButton.groupButtons)
            this += button
    }

    private val contentGroup = VisTable(true).apply {
        add(chooseOptionButtons).fillX().row()
        addSeparator().padBottom(getHeightPerc(.025f))
        add(optionContent)
    }

    init {
        isDebugAll = true
        this += contentGroup
        contentGroup.centerPosition()
    }

    override fun show() {
        super.show()
        Loader.logger.debug { "Registration screen is shown..." }
    }

    override fun onBackPressed() {
        if (yesNoDialog.isHidden) yesNoDialog.show(this)
    }
}
