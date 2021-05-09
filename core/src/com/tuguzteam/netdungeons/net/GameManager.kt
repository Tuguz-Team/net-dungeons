package com.tuguzteam.netdungeons.net

import ktx.log.logger

abstract class GameManager {
    companion object {
        val logger = logger<GameManager>()
    }

    var game: Game? = null
        protected set

    abstract suspend fun createGame(): Result<Game>

    abstract suspend fun insertIntoQueue(): Result<Game?>

    open suspend fun removeFromQueue(): Result<Unit> {
        game = null
        return Result.Success(data = Unit)
    }

    abstract suspend fun startGame(seed: Long): Result<Game>
}
