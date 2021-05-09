package com.tuguzteam.netdungeons.net

data class Game(val userIDs: MutableList<String> = mutableListOf(), var seed: Long? = null) {
    companion object FieldNames {
        const val USER_IDS = "userIDs"
        const val SEED = "seed"
    }
}
