package com.tuguzteam.netdungeons

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Vector2
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun <T : ModelBatch> T.use(camera: Camera, action: (T) -> Unit) {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    begin(camera)
    action(this)
    end()
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
        initialPointer1: Vector2?,
        initialPointer2: Vector2?,
        pointer1: Vector2?,
        pointer2: Vector2?
    ): Boolean = false

    override fun pinchStop() = Unit
}
