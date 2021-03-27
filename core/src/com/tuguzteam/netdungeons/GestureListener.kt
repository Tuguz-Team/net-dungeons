package com.tuguzteam.netdungeons

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.input.GestureDetector.GestureAdapter
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils.clamp
import com.badlogic.gdx.math.Vector3
import kotlin.math.absoluteValue
import kotlin.math.sign

class GestureListener(private val camera: OrthographicCamera) : GestureAdapter() {
    private companion object {
        private const val FLING_VELOCITY = 1.25f
        private const val ROTATION_ANGLE = 90f
        private const val ROTATION_DURATION = 0.75f

        private const val MIN_ZOOM = 0.5f
        private const val MAX_ZOOM = 1f
        private const val ZOOM_SPEED = 1f
        private const val ZOOM_DURATION = 0.25f
    }

    private var sign = 0
    private var rotationTime = 0f
    private var rotationOldAngle = 0f
    private var rotationOldProgress = 0f

    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
        val velocity = velocityX / Gdx.graphics.width
        if (velocity.absoluteValue > FLING_VELOCITY && rotationTime <= 0) {
            sign = -velocity.sign.toInt()
            rotationTime = ROTATION_DURATION
            rotationOldAngle = 0f
            rotationOldProgress = 0f
            return true
        }
        return false
    }

    private var zoomOldDistance = 0f
    private var zoomTime = 0f
    private var zoomTarget = 0f
    private var zoomOrigin = 0f

    override fun zoom(initialDistance: Float, distance: Float): Boolean {
        val delta = Gdx.graphics.deltaTime * ZOOM_SPEED * (zoomOldDistance - distance)
        zoomOrigin = camera.zoom
        zoomTarget = clamp(camera.zoom + delta, MIN_ZOOM, MAX_ZOOM)
        zoomTime = ZOOM_DURATION
        zoomOldDistance = distance
        return true
    }

    fun update() {
        val updateZoom = zoomTime > 0
        if (updateZoom) {
            zoomTime -= Gdx.graphics.deltaTime
            val progress = if (zoomTime < 0) 1f else 1f - zoomTime / ZOOM_DURATION
            camera.zoom = Interpolation.pow3Out.apply(zoomOrigin, zoomTarget, progress)
        }

        val updateRotation = rotationTime > 0
        if (updateRotation) {
            rotationTime -= Gdx.graphics.deltaTime
            val progress = if (rotationTime < 0) 1f else 1f - rotationTime / ROTATION_DURATION
            val angle = Interpolation.fade.apply(0f, ROTATION_ANGLE, progress)
            camera.rotateAround(Vector3(), Vector3.Y, (angle - rotationOldAngle) * sign)

            rotationOldAngle = angle
            rotationOldProgress = progress
        }

        if (updateZoom || updateRotation) {
            camera.update(false)
        }
    }
}
