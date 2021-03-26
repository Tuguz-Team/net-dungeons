package com.tuguzteam.netdungeons

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.input.GestureDetector.GestureAdapter
import com.badlogic.gdx.math.MathUtils.clamp
import com.badlogic.gdx.math.Vector3
import kotlin.math.absoluteValue
import kotlin.math.sign

class GestureListener(private val camera: OrthographicCamera) : GestureAdapter() {
    private var sign = 0
    private var processedAngle = 0f
    private var oldZoomDistance = 0f

    private companion object {
        private const val FLING_VELOCITY = 1.25f
        private const val ANGLE = 90
        private const val CAMERA_SPEED = 1.25f
        private const val MIN_ZOOM = 0.5f
        private const val MAX_ZOOM = 1f
        private const val ZOOM_SPEED = 0.85f
    }

    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
        val velocity = velocityX / Gdx.graphics.width
        if (velocity.absoluteValue > FLING_VELOCITY && sign == 0) {
            sign = -velocity.sign.toInt()
            return true
        }
        return false
    }

    override fun zoom(initialDistance: Float, distance: Float): Boolean {
        val delta = Gdx.graphics.deltaTime * ZOOM_SPEED *
                clamp(oldZoomDistance - distance, -1f, 1f)
        camera.apply {
            zoom = clamp(camera.zoom + delta, MIN_ZOOM, MAX_ZOOM)
            update()
        }
        oldZoomDistance = distance
        return true
    }

    fun update() {
        if (sign != 0) {
            var angle = Gdx.graphics.deltaTime * sign * ANGLE * CAMERA_SPEED
            processedAngle += angle
            if (processedAngle.absoluteValue >= ANGLE) {
                angle += sign * ANGLE - processedAngle
                sign = 0
                processedAngle = 0f
            }
            camera.apply {
                rotateAround(Vector3(), Vector3.Y, angle)
                update()
            }
        }
    }
}
