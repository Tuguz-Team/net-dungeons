package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.net.NetworkManager
import com.tuguzteam.netdungeons.ui.ButtonListener
import com.tuguzteam.netdungeons.ui.YesNoDialog
import ktx.actors.centerPosition
import ktx.actors.plusAssign
import ktx.log.debug
import ktx.log.error

class RegistrationScreen(loader: Loader) : StageScreen(loader) {
    private val defaultSkin = loader.defaultSkin
    private val yesNoDialog =
        YesNoDialog("Cancel registration?", defaultSkin, { loader.setScreen<MainScreen>() })

    private val nameLabel = Label("Enter your name", defaultSkin)
    private val nameTextField = TextField(null, defaultSkin).apply {
        alignment = Align.center
    }

    private val emailLabel = Label("Enter your email", defaultSkin)
    private val emailTextField = TextField(null, defaultSkin).apply {
        alignment = Align.center
    }

    private val passwordLabel = Label("Enter your password", defaultSkin)
    private val passwordTextField = TextField(null, defaultSkin).apply {
        alignment = Align.center
        isPasswordMode = true
        setPasswordCharacter('*')
    }

    private val registerButton = TextButton("Register", defaultSkin).apply {
        addListener(ButtonListener {
            val email = emailTextField.text
            val password = passwordTextField.text
            val name = nameTextField.text
            try {
                val callback = NetworkManager.Callback(
                    onSuccess = { user ->
                        Loader.logger.debug(user::toString)
                        loader.setScreen<MainScreen>()
                    },
                    onFailure = {
                        Loader.logger.error { "WTF!!!" }
                    }
                )
                loader.networkManager.register(email, password, name, callback)
            } catch (e: IllegalArgumentException) {
                Loader.logger.error(e::toString)
            }
        })
    }

    private val table = Table().apply {
        center()

        add(nameLabel).space(10f).row()
        add(nameTextField).spaceBottom(40f).row()

        add(emailLabel).space(10f).row()
        add(emailTextField).spaceBottom(40f).row()

        add(passwordLabel).space(10f).row()
        add(passwordTextField).spaceBottom(40f).row()

        add(registerButton)
    }

    init {
        isDebugAll = true
        this += table
        table.centerPosition()
    }

    override fun show() {
        super.show()
        Loader.logger.debug { "Registration screen is shown..." }
    }

    override fun onBackPressed() {
        if (yesNoDialog.isHidden) yesNoDialog.show(this)
    }
}
