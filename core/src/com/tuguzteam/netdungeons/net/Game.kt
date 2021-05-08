package com.tuguzteam.netdungeons.net

data class Game(val userIDs: MutableList<String> = mutableListOf(), var seed: Long? = null)
