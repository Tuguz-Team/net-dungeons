package com.tuguzteam.netdungeons.field

enum class Direction(val degrees: Float) {
    Forward(0f),
    Back(180f),
    Left(90f),
    Right(-90f);

    operator fun not() = when (this) {
        Forward -> Back
        Back -> Forward
        Left -> Right
        Right -> Left
    }
}

fun Direction.inverse() = !this
