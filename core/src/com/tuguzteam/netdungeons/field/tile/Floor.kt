package com.tuguzteam.netdungeons.field.tile

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.Ray
import com.tuguzteam.netdungeons.ImmutableGridPoint2
import com.tuguzteam.netdungeons.immutableVec3
import com.tuguzteam.netdungeons.objects.Focusable
import com.tuguzteam.netdungeons.objects.Intersectable
import com.tuguzteam.netdungeons.objects.Renderable
import com.tuguzteam.netdungeons.objects.TextureObject

class Floor(
    position: ImmutableGridPoint2,
    texture: Texture,
) : Tile(position), Renderable, Focusable, Intersectable {

    private val textureObject = object : TextureObject(
        position = immutableVec3(
            x = position.x.toFloat(),
            z = position.y.toFloat(),
        ), texture, width = size, height = size,
    ) {
        init {
            transform.rotate(Vector3.X, -90f)
        }
    }

    override val renderableProviders = sequenceOf(textureObject.renderableProviders).flatten()

    private val initialColor = textureObject.color

    override fun focus() {
        textureObject.color = Color.RED
    }

    override fun unfocus() {
        textureObject.color = initialColor
    }

    override fun dispose() {
        super.dispose()
        textureObject.dispose()
    }

    override fun intersectedBy(ray: Ray) = textureObject.intersectedBy(ray)

    override fun intersectionPoint(ray: Ray) = textureObject.intersectionPoint(ray)
}
