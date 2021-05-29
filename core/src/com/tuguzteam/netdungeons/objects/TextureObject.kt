package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.create
import com.tuguzteam.netdungeons.with
import ktx.math.vec3

abstract class TextureObject(
    position: ImmutableVector3,
    texture: Texture,
    val width: UInt,
    val height: UInt,
) : ModelObject(position, createRect(texture, width, height)) {
    var color: Color = Color.WHITE
        set(value) {
            materials[0] with ColorAttribute.createDiffuse(value)
            field = value
        }
}

private fun createRect(texture: Texture, width: UInt, height: UInt): Model {
    val attributes = (Usage.Position or Usage.Normal or Usage.TextureCoordinates).toLong()
    val material = Material(TextureAttribute.createDiffuse(texture))

    val x = width.toInt() * 0.5f
    val y = height.toInt() * 0.5f
    val corner00 = vec3(-x, -y)
    val corner10 = vec3(x, -y)
    val corner11 = vec3(x, y)
    val corner01 = vec3(-x, y)
    val normal = vec3(z = -1f)

    return ModelObject.modelBuilder.create {
        val meshPartBuilder = part("rect", GL20.GL_TRIANGLES, attributes, material)
        meshPartBuilder.rect(corner00, corner10, corner11, corner01, normal)
    }
}
