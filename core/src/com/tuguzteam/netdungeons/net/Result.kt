package com.tuguzteam.netdungeons.net

import kotlinx.coroutines.CancellationException

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()

    data class Failure<out T>(val cause: Throwable) : Result<T>()

    class Cancel<out T> : Result<T>()
}

suspend fun <T> resultFrom(block: suspend () -> T): Result<T> = try {
    val data = block()
    Result.Success(data)
} catch (e: CancellationException) {
    Result.Cancel()
} catch (throwable: Throwable) {
    Result.Failure(cause = throwable)
}
