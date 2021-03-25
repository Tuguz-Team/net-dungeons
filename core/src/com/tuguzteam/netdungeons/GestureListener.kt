package com.tuguzteam.netdungeons

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.input.GestureDetector.GestureAdapter
import com.badlogic.gdx.math.Vector3
import kotlin.math.absoluteValue
import kotlin.math.sign

class GestureListener(private val camera: Camera) : GestureAdapter() {
    private var sign = 0
    private var processedAngle = 0f

    private companion object {
        private const val FLING_VELOCITY = 1.25f
        private const val ANGLE = 90
        private const val CAMERA_SPEED = 0.75f
    }

    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
        val velocity = velocityX / Gdx.graphics.width
        if (velocity.absoluteValue > FLING_VELOCITY && sign == 0) {
            sign = -velocity.sign.toInt()
            return true
        }
        return false
    }

    fun update() {
        if (sign != 0 && processedAngle.absoluteValue < ANGLE) {
            val angle = Gdx.graphics.deltaTime * sign * ANGLE * CAMERA_SPEED
            camera.apply {
                rotateAround(Vector3(), Vector3.Y, angle)
                update()
            }
            processedAngle += angle
        } else {
            camera.apply {
                rotateAround(Vector3(), Vector3.Y, sign * ANGLE - processedAngle)
                update()
            }
            sign = 0
            processedAngle = 0f
        }
    }
}
