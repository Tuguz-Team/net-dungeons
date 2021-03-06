package com.tuguzteam.netdungeons.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils.clamp
import com.badlogic.gdx.math.Vector3
import com.tuguzteam.netdungeons.KtxGestureAdapter
import com.tuguzteam.netdungeons.field.tile.Tile
import com.tuguzteam.netdungeons.screens.GameScreen
import com.tuguzteam.netdungeons.toMutable
import kotlin.math.absoluteValue
import kotlin.math.sign

class RotationZoomGestureListener(private val gameScreen: GameScreen) : KtxGestureAdapter {
    private companion object {
        private const val FLING_VELOCITY = 1.25f
        private const val ROTATION_ANGLE = 90f
        private const val ROTATION_DURATION = 0.75f

        private const val MIN_ZOOM = 0.5f
        private const val MAX_ZOOM = 1f
        private const val ZOOM_SPEED = 0.75f
        private const val ZOOM_DURATION = 0.25f
    }

    private val camera = gameScreen.camera as OrthographicCamera

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

    fun update(delta: Float) {
        val updateZoom = zoomTime > 0
        if (updateZoom) {
            zoomTime -= delta
            val progress = if (zoomTime < 0) 1f else 1f - zoomTime / ZOOM_DURATION
            camera.zoom = Interpolation.pow3Out.apply(zoomOrigin, zoomTarget, progress)
        }

        val updateRotation = rotationTime > 0
        if (updateRotation) {
            rotationTime -= delta
            val progress = if (rotationTime < 0) 1f else 1f - rotationTime / ROTATION_DURATION
            val angle = Interpolation.fade.apply(0f, ROTATION_ANGLE, progress)
            camera.rotateAround(
                Tile.toImmutableVec3(gameScreen.playerPosition).toMutable(),
                Vector3.Y,
                (angle - rotationOldAngle) * sign,
            )

            rotationOldAngle = angle
            rotationOldProgress = progress
        }

        if (updateZoom || updateRotation) {
            camera.update()
        }
    }
}
