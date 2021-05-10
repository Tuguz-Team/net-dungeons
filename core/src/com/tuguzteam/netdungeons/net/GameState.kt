package com.tuguzteam.netdungeons.net

sealed class GameState {
    data class PlayerAdded(val player: Player) : GameState()

    data class PlayerUpdated(val player: Player) : GameState()

    data class PlayerRemoved(val player: Player) : GameState()

    data class Started(val game: Game) : GameState()

    data class Ended(val game: Game) : GameState()

    data class Destroyed(val game: Game) : GameState()

    data class Failure(val cause: Throwable) : GameState()
}
