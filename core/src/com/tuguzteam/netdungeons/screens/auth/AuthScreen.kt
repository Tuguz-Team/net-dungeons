package com.tuguzteam.netdungeons.screens.auth

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.kotcrab.vis.ui.layout.FlowGroup
import com.kotcrab.vis.ui.widget.VisTable
import com.tuguzteam.netdungeons.*
import com.tuguzteam.netdungeons.net.AuthManager.Companion.EMAIL_REGEX
import com.tuguzteam.netdungeons.net.AuthManager.Companion.NAME_REGEX
import com.tuguzteam.netdungeons.net.AuthManager.Companion.PASSWORD_REGEX
import com.tuguzteam.netdungeons.net.Result
import com.tuguzteam.netdungeons.screens.main.MainScreen
import com.tuguzteam.netdungeons.screens.StageScreen
import com.tuguzteam.netdungeons.ui.*
import kotlinx.coroutines.launch
import ktx.actors.centerPosition
import ktx.actors.plusAssign
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.error
import ktx.log.info

class AuthScreen(loader: Loader) : StageScreen(loader) {
    private val cancelDialog = YesNoDialog(
        "Cancel registration?",
        onYesOption = { loader.setScreen<MainScreen>() }
    )
    private val registerErrorDialog = OkDialog("Registration error!").apply { text("") }
    private val signInErrorDialog = OkDialog("Sign in error!").apply { text("") }
    private val registerWaitDialog = Dialog("Waiting server for registration...")
    private val signInWaitDialog = Dialog("Waiting for server for sign in...")

    private val nameTextField = ExtValidTextField(
        NAME_REGEX, "Enter your name",
        "Name doesn't match pattern",
        "Name couldn't be empty",
    )
    private val emailTextField = ExtValidTextField(
        EMAIL_REGEX, "Enter your email",
        "Email doesn't match pattern",
        "Email couldn't be empty",
    )
    private val passwordTextField = ExtValidTextField(
        PASSWORD_REGEX, "Enter your password",
        "Password doesn't match pattern",
        "Password couldn't be empty",
    ).apply { setPasswordMode('*') }

    private val optionContent = VisTable(true)
    private val registerContent = AuthContent("Register", ClickListener {
        KtxAsync.launch {
            registerWaitDialog.show(this@AuthScreen)
            val name = nameTextField.text
            val email = emailTextField.text
            val password = passwordTextField.text
            when (val result = loader.authManager.register(name, email, password)) {
                is Result.Cancel -> Loader.logger.info { "Task was cancelled normally" }
                is Result.Failure -> {
                    Loader.logger.error(result.cause) { "Registration failure!" }
                    registerErrorDialog.apply {
                        val message: String = when {
                            email.isBlank() -> "Email cannot be empty"
                            password.isBlank() -> "Password cannot be empty"
                            else -> when (result.cause) {
                                is AuthInvalidPasswordException -> "Server rejected given password: it is too weak"
                                is AuthInvalidEmailException -> "Server rejected given email"
                                is AuthUserCollisionException -> "User with given email already exists"
                                is WeakNetworkException -> "Check your Internet connection and try again"
                                else -> "Internal server error"
                            }
                        }
                        (contentTable.cells[0].actor as Label).setText(message)
                        this.show(this@AuthScreen)
                    }
                }
                is Result.Success -> loader.setScreen<MainScreen>()
            }
            registerWaitDialog.hide()
        }
    }, optionContent, passwordTextField, arrayListOf(nameTextField, emailTextField))

    private val signInContent = AuthContent("Login", ClickListener {
        KtxAsync.launch {
            signInWaitDialog.show(this@AuthScreen)
            val email = emailTextField.text
            val password = passwordTextField.text
            when (val result = loader.authManager.signIn(email, password)) {
                is Result.Cancel -> Loader.logger.info { "Task was cancelled normally" }
                is Result.Failure -> {
                    Loader.logger.error(result.cause) { "Sign in failure!" }
                    signInErrorDialog.apply {
                        val message = when {
                            email.isBlank() -> "Email cannot be empty"
                            password.isBlank() -> "Password cannot be empty"
                            else -> when (result.cause) {
                                is AuthInvalidPasswordException -> "Wrong password for account with given email"
                                is AuthInvalidUserException -> "No user exists with given email"
                                is WeakNetworkException -> "Check your Internet connection and try again"
                                else -> "Internal server error"
                            }
                        }
                        (contentTable.cells[0].actor as Label).setText(message)
                        this.show(this@AuthScreen)
                    }
                }
                is Result.Success -> loader.setScreen<MainScreen>()
            }
            signInWaitDialog.hide()
        }
    }, optionContent, passwordTextField, listOf(emailTextField))

    private val radioButton = RadioButtonGroup(
        true, arrayListOf(registerContent.radioButton, signInContent.radioButton)
    )
    private val chooseOptionButtons = FlowGroup(
        false, heightFraction(.05f)
    ).apply {
        radioButton.groupButtons.forEach { button -> this += button }
    }

    private val contentGroup = VisTable(true).apply {
        width = widthFraction(.1875f)

        add(chooseOptionButtons).expandX().row()
        addSeparator().padBottom(heightFraction(.025f))
        add(optionContent)
    }

    init {
        isDebugAll = true
        this += contentGroup
        contentGroup.centerPosition()
    }

    override fun show() {
        super.show()
        Loader.logger.debug { "Auth screen is shown..." }
    }

    override fun onBackPressed() {
        if (cancelDialog.isHidden) cancelDialog.show(this)
    }
}
