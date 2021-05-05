package com.tuguzteam.netdungeons.net

import ktx.log.logger

abstract class NetworkManager {
    companion object {
        val logger = logger<NetworkManager>()

        val EMAIL_REGEX = "^\\S+@\\S+$".toRegex()
        val PASSWORD_REGEX =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[;:`*()№\\\\/|~?!.'\"_@#\$%^&+=-])(?=\\S+$).{6,}$".toRegex()
        val NAME_REGEX = "^(?=.*[a-zA-Z])(?=\\S+\$).{4,}\$".toRegex()
    }

    var user: User? = null
        protected set

    data class Callback(val onSuccess: (User) -> Unit, val onFailure: (Exception) -> Unit)

    open fun signIn(email: String, password: String, callback: Callback) {
        require(email matches EMAIL_REGEX) { "Email does not match pattern!" }
        require(password matches PASSWORD_REGEX) { "Password does not match pattern!" }
    }

    open fun register(email: String, password: String, name: String, callback: Callback) {
        require(email matches EMAIL_REGEX) { "Email does not match pattern!" }
        require(password matches PASSWORD_REGEX) { "Password does not match pattern!" }
        require(name matches NAME_REGEX) { "Name does not match pattern!" }
    }

    abstract fun signOut(onSuccess: () -> Unit)
}
