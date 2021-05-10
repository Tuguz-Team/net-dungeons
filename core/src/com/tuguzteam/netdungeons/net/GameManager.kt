package com.tuguzteam.netdungeons.net

import ktx.log.logger

abstract class GameManager {
    companion object {
        val logger = logger<GameManager>()
    }

    var game: Game? = null
        protected set

    open var gameStateListener: ((GameState) -> Unit)? = null

    abstract suspend fun createGame(): Result<Game>

    abstract suspend fun insertIntoQueue(): Result<Game?>

    abstract suspend fun removeFromQueue(): Result<Unit>

    abstract suspend fun startGame(seed: Long): Result<Game>
}
