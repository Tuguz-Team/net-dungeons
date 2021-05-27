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
import ktx.math.vec3

abstract class TextureObject(
    position: ImmutableVector3,
    texture: Texture,
) : ModelObject(position, createRect(texture)) {
    var color: Color = Color.WHITE
        set(value) {
            materials[0].set(ColorAttribute.createDiffuse(value))
            field = value
        }

    val width = texture.width
    val height = texture.height
}

private fun createRect(texture: Texture): Model = ModelObject.modelBuilder.run {
    begin()
    val attributes = (Usage.Position or Usage.Normal or Usage.TextureCoordinates).toLong()
    val material = Material(TextureAttribute.createDiffuse(texture))

    val x = texture.width * 0.5f
    val y = texture.height * 0.5f
    val corner00 = vec3(-x, -y)
    val corner10 = vec3(x, -y)
    val corner11 = vec3(x, y)
    val corner01 = vec3(-x, y)
    val normal = vec3(z = 1f)

    val meshPartBuilder = part("rect", GL20.GL_TRIANGLES, attributes, material)
    meshPartBuilder.rect(corner00, corner10, corner11, corner01, normal)
    end()
}
