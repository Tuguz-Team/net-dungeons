package com.tuguzteam.netdungeons.input

import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.utils.viewport.Viewport
import com.tuguzteam.netdungeons.KtxGestureAdapter
import com.tuguzteam.netdungeons.field.rooms.Room
import com.tuguzteam.netdungeons.objects.Focusable
import com.tuguzteam.netdungeons.objects.GameObject
import com.tuguzteam.netdungeons.objects.Intersectable
import com.tuguzteam.netdungeons.objects.intersects
import ktx.log.debug
import ktx.log.logger

class ObjectChooseGestureListener(private val viewport: Viewport) : KtxGestureAdapter {
    private companion object {
        private val logger = logger<ObjectChooseGestureListener>()
    }

    var chosenGameObject: GameObject? = null
        private set

    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
        val ray = viewport.getPickRay(x, y)

        val room = intersectedRoom(ray)
        val gameObject = room?.let { intersectedGameObject(ray, it) }
        if (gameObject !== chosenGameObject) {
            (chosenGameObject as? Focusable)?.unfocus()
            chosenGameObject = gameObject
            (chosenGameObject as? Focusable)?.focus()
            logger.debug { "Chosen game object: $chosenGameObject" }
        }

        return chosenGameObject != null
    }

    private fun intersectedRoom(ray: Ray): Room? {
        var room: Room? = null
        Room.asSequence().filter {
            it.visible && ray intersects it
        }.forEach {
            val distance2 = room?.position?.dst2(ray.origin)
            val itDistance2 = it.position.dst2(ray.origin)
            if (distance2 == null || itDistance2 < distance2) {
                room = it
            }
        }
        return room
    }

    private fun intersectedGameObject(ray: Ray, room: Room): GameObject? {
        var gameObject: GameObject? = null
        room.asSequence().filter {
            it.visible && it is Intersectable && it is Focusable && ray intersects it
        }.forEach {
            val distance2 = gameObject?.position?.dst2(ray.origin)
            val itDistance2 = it.position.dst2(ray.origin)
            if (distance2 == null || itDistance2 < distance2) {
                gameObject = it
            }
        }
        return gameObject
    }
}
