package com.tuguzteam.netdungeons.input

import com.badlogic.gdx.math.collision.Ray
import com.tuguzteam.netdungeons.KtxGestureAdapter
import com.tuguzteam.netdungeons.field.tile.Tile
import com.tuguzteam.netdungeons.field.tile.toImmutableVec3
import com.tuguzteam.netdungeons.objects.Focusable
import com.tuguzteam.netdungeons.objects.GameObject
import com.tuguzteam.netdungeons.objects.intersects
import com.tuguzteam.netdungeons.screens.GameScreen
import ktx.log.debug
import ktx.log.logger

class ObjectChooseGestureListener(private val gameScreen: GameScreen) : KtxGestureAdapter {
    private companion object {
        private val logger = logger<ObjectChooseGestureListener>()
    }

    var chosenGameObject: GameObject? = null
        private set

    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
        val ray = gameScreen.viewport.getPickRay(x, y)

        val gameObject = intersectedGameObject(ray)
        if (gameObject !== chosenGameObject) {
            (chosenGameObject as? Focusable)?.unfocus()
            chosenGameObject = gameObject
            (chosenGameObject as? Focusable)?.focus()
        } else {
            (chosenGameObject as? Focusable)?.unfocus()
            chosenGameObject = null
        }
        logger.debug { "Chosen game object: $chosenGameObject" }

        return chosenGameObject != null
    }

    private fun intersectedGameObject(ray: Ray): GameObject? {
        var gameObject: Tile? = null
        Tile.asSequence().filter {
            it in gameScreen.visibleObjects && ray intersects it
        }.forEach { tile ->
            val distance2 = gameObject?.toImmutableVec3()?.dst2(ray.origin)
            val itDistance2 = tile.toImmutableVec3().dst2(ray.origin)
            if (distance2 == null || itDistance2 < distance2) {
                gameObject = tile
            }
        }
        return gameObject
    }
}
