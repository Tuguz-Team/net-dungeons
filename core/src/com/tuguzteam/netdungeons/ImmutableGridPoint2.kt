@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.tuguzteam.netdungeons

import com.badlogic.gdx.math.GridPoint2
import kotlin.math.sqrt

data class ImmutableGridPoint2(val x: Int, val y: Int) {
    infix fun dst2(point: ImmutableGridPoint2) = dst2(point.x, point.y)

    infix fun dst(point: ImmutableGridPoint2) = dst(point.x, point.y)

    fun dst2(x: Int, y: Int): Int {
        val xd = x - this.x
        val yd = y - this.y

        return xd * xd + yd * yd
    }

    fun dst(x: Int, y: Int): Float = sqrt(dst2(x, y).toFloat())

    operator fun plus(point: ImmutableGridPoint2) = immutableGridPoint2(x + point.x, y + point.y)

    operator fun minus(point: ImmutableGridPoint2) = immutableGridPoint2(x - point.x, y - point.y)

    operator fun times(scalar: Int) = immutableGridPoint2(x * scalar, y * scalar)

    operator fun div(scalar: Int) = immutableGridPoint2(x / scalar, y / scalar)

    operator fun unaryMinus() = immutableGridPoint2(-x, -y)

    operator fun unaryPlus() = immutableGridPoint2(x, y)
}

fun immutableGridPoint2(x: Int = 0, y: Int = 0) = ImmutableGridPoint2(x, y)

fun gridPoint2(x: Int = 0, y: Int = 0) = GridPoint2(x, y)

fun GridPoint2.toImmutable() = immutableGridPoint2(x, y)

fun ImmutableGridPoint2.toMutable() = gridPoint2(x, y)

operator fun GridPoint2.plus(point: GridPoint2) = gridPoint2(x + point.x, y + point.y)

operator fun GridPoint2.minus(point: GridPoint2) = gridPoint2(x - point.x, y - point.y)

operator fun GridPoint2.times(scalar: Int) = gridPoint2(x * scalar, y * scalar)

operator fun GridPoint2.div(scalar: Int) = gridPoint2(x / scalar, y / scalar)

operator fun GridPoint2.unaryMinus() = gridPoint2(-x, -y)

operator fun GridPoint2.unaryPlus() = gridPoint2(x, y)
