package com.tuguzteam.netdungeons.input

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.utils.viewport.Viewport
import com.tuguzteam.netdungeons.KtxGestureAdapter
import com.tuguzteam.netdungeons.objects.GameObject
import com.tuguzteam.netdungeons.toImmutable
import ktx.log.debug
import ktx.log.logger

class ObjectChooseGestureListener(
        private val viewport: Viewport,
        var focusAction: GameObject.() -> Unit,
        var resetFocusAction: GameObject.() -> Unit
) : KtxGestureAdapter {
    private companion object {
        private val logger = logger<ObjectChooseGestureListener>()
    }

    var chosenGameObject: GameObject? = null
        private set

    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
        val ray = viewport.getPickRay(x, y)

        var tempGameObject: GameObject? = null
        GameObject.forEach {
            if (Intersector.intersectRayBoundsFast(ray, it.boundingBox)) {
                val distance2 = tempGameObject?.position?.dst2(ray.origin.toImmutable())
                val itDistance2 = it.position.dst2(ray.origin.toImmutable())
                if (distance2 == null || itDistance2 < distance2) {
                    tempGameObject = it
                }
            }
        }
        if (tempGameObject !== chosenGameObject) {
            chosenGameObject?.let(resetFocusAction)
            chosenGameObject = tempGameObject
            chosenGameObject?.let(focusAction)
            logger.debug { "Chosen object: $chosenGameObject" }
        }

        return chosenGameObject != null
    }
}
