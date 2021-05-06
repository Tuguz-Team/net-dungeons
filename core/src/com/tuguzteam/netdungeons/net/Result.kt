package com.tuguzteam.netdungeons.net

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()

    data class Failure<out T>(val cause: Throwable) : Result<T>()

    class Cancel<out T> : Result<T>()
}
