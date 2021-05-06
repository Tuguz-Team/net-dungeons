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

    abstract suspend fun updateUser()

    open suspend fun signIn(email: String, password: String) {
        require(email matches EMAIL_REGEX) { "Email does not match pattern!" }
        require(password matches PASSWORD_REGEX) { "Password does not match pattern!" }
    }

    open suspend fun register(name: String, email: String, password: String) {
        require(name matches NAME_REGEX) { "Name does not match pattern!" }
        require(email matches EMAIL_REGEX) { "Email does not match pattern!" }
        require(password matches PASSWORD_REGEX) { "Password does not match pattern!" }
    }

    open suspend fun signOut() {
        user = null
    }
}
