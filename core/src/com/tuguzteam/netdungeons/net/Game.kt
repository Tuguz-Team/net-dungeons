package com.tuguzteam.netdungeons.net

data class Game(val userIDs: MutableList<String> = mutableListOf(), val seed: Long? = null)
