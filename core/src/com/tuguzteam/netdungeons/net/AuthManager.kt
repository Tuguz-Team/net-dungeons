package com.tuguzteam.netdungeons.net

import ktx.log.logger

abstract class AuthManager {
    companion object {
        val logger = logger<AuthManager>()

        val EMAIL_REGEX = "^\\S+@\\S+\\.\\S+$".toRegex()
        val PASSWORD_REGEX =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[;:`*()№\\\\/|~\\[\\]{},?!.'\"_@#\$%^&+=-])(?=\\S+$).{6,}$".toRegex()
        val NAME_REGEX = "^(?=.*[a-zA-Z])(?=\\S+\$).{4,}\$".toRegex()
    }

    var user: User? = null
        protected set

    abstract suspend fun syncUser(): Result<User?>

    abstract suspend fun signIn(email: String, password: String): Result<User>

    abstract suspend fun register(name: String, email: String, password: String): Result<User>

    abstract suspend fun signOut(): Result<Unit>

    abstract suspend fun updateName(name: String): Result<User>

    abstract suspend fun updateEmail(email: String): Result<User>

    abstract suspend fun updatePassword(password: String): Result<User>

    abstract suspend fun deleteUser(): Result<Unit>

    abstract suspend fun reAuth(email: String, password: String): Result<Unit>
}
