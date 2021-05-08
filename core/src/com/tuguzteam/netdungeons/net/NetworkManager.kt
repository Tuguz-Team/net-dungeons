package com.tuguzteam.netdungeons.net

import ktx.log.logger

abstract class NetworkManager {
    companion object {
        val logger = logger<NetworkManager>()

        val EMAIL_REGEX = "^\\S+@\\S+\\.\\S+$".toRegex()
        val PASSWORD_REGEX =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[;:`*()№\\\\/|~?!.'\"_@#\$%^&+=-])(?=\\S+$).{6,}$".toRegex()
        val NAME_REGEX = "^(?=.*[a-zA-Z])(?=\\S+\$).{4,}\$".toRegex()
    }

    var user: User? = null
        protected set

    var game: Game? = null
        protected set

    abstract suspend fun updateUser(): Result<User?>

    abstract suspend fun signIn(email: String, password: String): Result<User>

    abstract suspend fun register(name: String, email: String, password: String): Result<User>

    open suspend fun signOut(): Result<Unit> {
        user = null
        return Result.Success(data = Unit)
    }

    abstract suspend fun createRoom(): Result<Game>

    abstract suspend fun insertIntoQueue(): Result<Game?>

    abstract suspend fun removeFromQueue(): Result<Unit>
}
