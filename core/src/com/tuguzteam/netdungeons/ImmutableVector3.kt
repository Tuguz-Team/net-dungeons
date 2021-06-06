@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.tuguzteam.netdungeons

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import ktx.math.ImmutableVector
import ktx.math.ImmutableVector2
import ktx.math.vec3
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

data class ImmutableVector3(val x: Float, val y: Float, val z: Float) :
    ImmutableVector<ImmutableVector3> {

    companion object {
        val ZERO = immutableVec3()

        val X = immutableVec3(x = 1f)

        val Y = immutableVec3(y = 1f)

        val Z = immutableVec3(z = 1f)

        fun fromString(string: String) = Vector3().fromString(string).toImmutable()
    }

    override val len2: Float
        get() = Vector3.len2(x, y, z)

    override val nor: ImmutableVector3
        get() = withLength2(1f)

    override fun toString() = "($x, $y, $z)"

    override operator fun inc() = immutableVec3(x + 1, y + 1, z + 1)

    override operator fun dec() = immutableVec3(x - 1, y - 1, z - 1)

    override fun dot(vector: ImmutableVector3) = dot(vector.x, vector.y, vector.z)

    fun crs(x: Float, y: Float, z: Float) = this.toMutable().crs(x, y, z).toImmutable()

    fun dot(x: Float, y: Float, z: Float) = Vector3.dot(this.x, this.y, this.z, x, y, z)

    override fun dst2(vector: ImmutableVector3) = dst2(vector.x, vector.y, vector.z)

    fun dst2(vector: Vector3) = dst2(vector.x, vector.y, vector.z)

    fun dst2(x: Float, y: Float, z: Float) = Vector3.dst2(this.x, this.y, this.z, x, y, z)

    override fun epsilonEquals(other: ImmutableVector3, epsilon: Float) =
        abs(other.x - x) <= epsilon
                && abs(other.y - y) <= epsilon
                && abs(other.z - z) <= epsilon

    override fun isOnLine(other: ImmutableVector3, epsilon: Float) =
        MathUtils.isZero(dot(other), epsilon) && isNotZero(0f) && other.isNotZero(0f)

    override fun isZero(margin: Float) = (x == 0f && y == 0f && z == 0f) || len2 < margin

    fun isNotZero(margin: Float) = !isZero(margin)

    override operator fun minus(other: ImmutableVector3) =
        immutableVec3(x - other.x, y - other.y, z - other.z)

    override operator fun plus(other: ImmutableVector3) =
        immutableVec3(x + other.x, y + other.y, z + other.z)

    override operator fun times(vector: ImmutableVector3) = times(vector.x, vector.y, vector.z)

    override operator fun times(scalar: Float) = times(scalar, scalar, scalar)

    fun times(x: Float, y: Float, z: Float) =
        immutableVec3(this.x * x, this.y * y, this.z * z)

    override operator fun unaryMinus() = immutableVec3(-x, -y, -z)

    override fun withClamp2(min2: Float, max2: Float): ImmutableVector3 {
        val l2 = len2

        return when {
            l2 < min2 -> withLength2(min2)
            l2 > max2 -> withLength2(max2)
            else -> this
        }
    }

    override fun withLength2(length2: Float): ImmutableVector3 {
        val oldLen2 = len2

        return if (oldLen2 == 0f || oldLen2 == length2) this else times(sqrt(length2 / oldLen2))
    }

    override fun withLerp(target: ImmutableVector3, alpha: Float): ImmutableVector3 {
        val invAlpha = 1.0f - alpha

        return immutableVec3(
            x = x * invAlpha + target.x * alpha,
            y = y * invAlpha + target.y * alpha,
            z = z * invAlpha + target.z * alpha
        )
    }

    override fun withLimit2(limit2: Float) = if (len2 <= limit2) this else withLength2(limit2)

    override fun withRandomDirection(rng: Random): ImmutableVector3 {
        TODO("Not yet implemented")
    }

    fun rotated(axis: ImmutableVector3, degrees: Float) = rotated(axis.x, axis.y, axis.z, degrees)

    fun rotated(axisX: Float, axisY: Float, axisZ: Float, degrees: Float) =
        this.toMutable().rotate(degrees, axisX, axisY, axisZ).toImmutable()

    fun rotatedRad(axis: ImmutableVector3, radians: Float) =
        rotatedRad(axis.x, axis.y, axis.z, radians)

    fun rotatedRad(axisX: Float, axisY: Float, axisZ: Float, radians: Float) =
        this.toMutable().rotateRad(radians, axisX, axisY, axisZ).toImmutable()
}

fun ImmutableVector3.toMutable() = vec3(x, y, z)

fun Vector3.toImmutable() = immutableVec3(x, y, z)

infix fun ImmutableVector3.x(other: ImmutableVector3) = crs(other.x, other.y, other.z)

infix fun ImmutableVector3.crs(other: ImmutableVector3) = crs(other.x, other.y, other.z)

fun immutableVec3(x: Float = 0f, y: Float = 0f, z: Float = 0f) = ImmutableVector3(x, y, z)

fun immutableVec2(x: Float = 0f, y: Float = 0f) = ImmutableVector2(x, y)
