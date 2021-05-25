package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.decals.Decal
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.collision.Ray
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.toMutable

abstract class DecalObject(
    position: ImmutableVector3 = ImmutableVector3.ZERO,
    texture: Texture,
) : GameObject() {
    override var position = position
        set(value) {
            decal.position = value.toMutable()
            field = value
        }

    private val textureRegion: TextureRegion = TextureRegion(texture)

    val decal: Decal = Decal.newDecal(textureRegion, true).apply {
        this.position = position.toMutable()
    }

    override fun intersectedBy(ray: Ray) = Intersector.intersectRayTriangles(
        ray,
        decal.vertices,
        shortArrayOf(0, 1, 2, 3, 2, 1),
        6,
        null,
    )
}
