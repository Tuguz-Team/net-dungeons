package com.tuguzteam.netdungeons.net

import ktx.log.logger

abstract class NetworkManager {
    companion object {
        val logger = logger<NetworkManager>()
    }

    var user: User? = null
        protected set

    abstract fun signIn(email: String, password: String, onCompleted: () -> Unit)

    abstract fun register(email: String, password: String, name: String, onCompleted: () -> Unit)

    abstract fun signOut()
}
