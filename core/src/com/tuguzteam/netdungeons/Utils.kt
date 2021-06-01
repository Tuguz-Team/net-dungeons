package com.tuguzteam.netdungeons

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.environment.BaseLight
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.scenes.scene2d.Actor
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

operator fun Appendable.plusAssign(charSequence: CharSequence) {
    append(charSequence)
}

operator fun Appendable.plusAssign(char: Char) {
    append(char)
}

@OptIn(ExperimentalContracts::class)
inline fun ModelBatch.use(camera: Camera, action: ModelBatch.() -> Unit) {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    begin(camera)
    action()
    end()
}

@OptIn(ExperimentalContracts::class)
inline fun ModelBuilder.create(action: ModelBuilder.() -> Unit): Model {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    begin()
    action()
    return end()
}

interface KtxGestureAdapter : GestureDetector.GestureListener {
    override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean = false

    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean = false

    override fun longPress(x: Float, y: Float): Boolean = false

    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean = false

    override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean = false

    override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean = false

    override fun zoom(initialDistance: Float, distance: Float): Boolean = false

    override fun pinch(
        initialPointer1: Vector2,
        initialPointer2: Vector2,
        pointer1: Vector2,
        pointer2: Vector2
    ): Boolean = false

    override fun pinchStop() = Unit
}

fun Actor.isDoneActing() = actions.isEmpty

fun widthFraction(fraction: Float) = Gdx.graphics.width * fraction
fun heightFraction(fraction: Float) = Gdx.graphics.height * fraction

operator fun Boolean.dec() = !this

operator fun BoundingBox.times(matrix: Matrix4): BoundingBox = BoundingBox(this).mul(matrix)

operator fun BoundingBox.timesAssign(matrix: Matrix4) {
    mul(matrix)
}

operator fun <T : BaseLight<T>> Environment.plusAssign(light: T) {
    add(light)
}

infix fun Environment.with(attribute: Attribute) {
    set(attribute)
}

infix fun Material.with(attribute: Attribute) {
    set(attribute)
}
