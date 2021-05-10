package com.tuguzteam.netdungeons.net

data class Game(val userIDs: MutableList<String> = mutableListOf(), val seed: Long? = null) {
    companion object FieldNames {
        const val USER_IDS = "userIDs"
        const val SEED = "seed"
    }
}
