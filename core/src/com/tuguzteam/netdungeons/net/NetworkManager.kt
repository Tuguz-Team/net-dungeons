package com.tuguzteam.netdungeons.net

import ktx.log.logger

interface NetworkManager {
    companion object {
        val logger = logger<NetworkManager>()
    }

    fun auth(onAuth: (User?) -> Unit)
}
