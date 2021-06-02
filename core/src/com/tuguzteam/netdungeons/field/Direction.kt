package com.tuguzteam.netdungeons.field

import com.badlogic.gdx.math.Vector3
import com.tuguzteam.netdungeons.ImmutableVector3
import ktx.math.unaryMinus

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

fun Direction.toVec3(): Vector3 = when (this) {
    Direction.Forward -> Vector3.Z
    Direction.Back -> -Vector3.Z
    Direction.Left -> Vector3.X
    Direction.Right -> -Vector3.X
}

fun Direction.toImmutableVec3() = when (this) {
    Direction.Forward -> ImmutableVector3.Z
    Direction.Back -> -ImmutableVector3.Z
    Direction.Left -> ImmutableVector3.X
    Direction.Right -> -ImmutableVector3.X
}
