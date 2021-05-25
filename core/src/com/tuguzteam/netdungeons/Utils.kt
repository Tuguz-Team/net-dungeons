package com.tuguzteam.netdungeons

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.decals.Decal
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.scenes.scene2d.Actor
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun <T : ModelBatch> T.use(camera: Camera, action: T.() -> Unit) {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    begin(camera)
    action()
    end()
}

fun <T : DecalBatch> T.use(decals: Iterable<Decal>) {
    decals.forEach(::add)
    flush()
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

fun <T : Actor> T.isDoneActing() = actions.isEmpty

fun widthFraction(fraction: Float) = Gdx.graphics.width * fraction
fun heightFraction(fraction: Float) = Gdx.graphics.height * fraction

operator fun Boolean.dec() = !this

operator fun BoundingBox.times(matrix: Matrix4): BoundingBox = BoundingBox(this).mul(matrix)

operator fun BoundingBox.timesAssign(matrix: Matrix4) {
    mul(matrix)
}

operator fun Environment.plusAssign(light: DirectionalLight) {
    add(light)
}

infix fun Environment.with(attribute: Attribute) {
    set(attribute)
}
