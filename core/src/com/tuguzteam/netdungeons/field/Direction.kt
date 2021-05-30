package com.tuguzteam.netdungeons.field

enum class Direction(val degrees: Float) {
    Forward(0f),
    Back(180f),
    Left(90f),
    Right(-90f);

    operator fun inc() = when (this) {
        Forward -> Right
        Back -> Left
        Left -> Forward
        Right -> Back
    }

    operator fun dec() = when (this) {
        Forward -> Left
        Back -> Right
        Left -> Back
        Right -> Forward
    }

    operator fun not() = when (this) {
        Forward -> Back
        Back -> Forward
        Left -> Right
        Right -> Left
    }
}

fun Direction.inverse() = !this

fun Direction.clockwise() = this.inc()

fun Direction.counterClockwise() = this.dec()
