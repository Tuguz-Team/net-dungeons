package com.tuguzteam.netdungeons

import com.badlogic.gdx.math.Vector3
import ktx.math.ImmutableVector
import java.util.*
import kotlin.math.abs

data class ImmutableVector3(val x: Float, val y: Float, val z: Float) :
    ImmutableVector<ImmutableVector3> {
    override val len2: Float
        get() = Vector3.len2(x, y, z)

    override val nor: ImmutableVector3
        get() = withLength2(1f)

    override operator fun inc(): ImmutableVector3 =
        ImmutableVector3(x + 1, y + 1, z + 1)

    override operator fun dec(): ImmutableVector3 =
        ImmutableVector3(x - 1, y - 1, z - 1)

    override fun dot(vector: ImmutableVector3): Float = dot(vector.x, vector.y, vector.z)

    fun crs(x: Float, y: Float, z: Float): ImmutableVector3 =
        this.toMutable().crs(x, y, z).toImmutable()

    fun dot(x: Float, y: Float, z: Float): Float =
        Vector3.dot(this.x, this.y, this.z, x, y, z)

    override fun dst2(vector: ImmutableVector3): Float = dst2(vector.x, vector.y, vector.z)

    fun dst2(x: Float, y: Float, z: Float): Float =
        Vector3.dst2(this.x, this.y, this.z, x, y, z)

    override fun epsilonEquals(other: ImmutableVector3, epsilon: Float): Boolean =
        abs(other.x - x) <= epsilon
                && abs(other.y - y) <= epsilon
                && abs(other.z - z) <= epsilon

    override fun isOnLine(other: ImmutableVector3, epsilon: Float): Boolean {
        TODO("Not yet implemented")
    }

    override fun isZero(margin: Float): Boolean = (x == 0f && y == 0f && z == 0f) || len2 < margin

    override operator fun minus(other: ImmutableVector3): ImmutableVector3 =
        ImmutableVector3(x - other.x, y - other.y, z - other.z)

    override operator fun plus(other: ImmutableVector3): ImmutableVector3 =
        ImmutableVector3(x + other.x, y + other.y, z + other.z)

    override operator fun times(vector: ImmutableVector3): ImmutableVector3 {
        TODO("Not yet implemented")
    }

    override operator fun times(scalar: Float): ImmutableVector3 =
        ImmutableVector3(x * scalar, y * scalar, z * scalar)

    override operator fun unaryMinus(): ImmutableVector3 = ImmutableVector3(-x, -y, -z)

    override fun withClamp2(min2: Float, max2: Float): ImmutableVector3 {
        TODO("Not yet implemented")
    }

    override fun withLength2(length2: Float): ImmutableVector3 {
        TODO("Not yet implemented")
    }

    override fun withLerp(target: ImmutableVector3, alpha: Float): ImmutableVector3 {
        TODO("Not yet implemented")
    }

    override fun withLimit2(limit2: Float): ImmutableVector3 {
        TODO("Not yet implemented")
    }

    override fun withRandomDirection(rng: Random): ImmutableVector3 {
        TODO("Not yet implemented")
    }
}

fun ImmutableVector3.toMutable(): Vector3 = Vector3(x, y, z)

fun Vector3.toImmutable(): ImmutableVector3 = ImmutableVector3(x, y, z)

infix fun ImmutableVector3.crs(other: ImmutableVector3): ImmutableVector3 =
    crs(other.x, other.y, other.z)
