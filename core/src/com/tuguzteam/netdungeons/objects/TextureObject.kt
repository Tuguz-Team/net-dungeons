package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.tuguzteam.netdungeons.ImmutableVector3

abstract class TextureObject(
    position: ImmutableVector3,
    private val texture: Texture,
) : ModelObject(position, createRect(texture)) {
    var color: Color = Color.WHITE
        set(value) {
            materials[0].set(ColorAttribute.createDiffuse(value))
            field = value
        }

    val width: Int
        get() = texture.width
    val height: Int
        get() = texture.height

    override fun dispose() {
        super.dispose()
        model.dispose()
    }
}

private fun createRect(texture: Texture): Model = ModelBuilder().createRect(
    -texture.width * 0.5f, -texture.height * 0.5f, 0f,
    texture.width * 0.5f, -texture.height * 0.5f, 0f,
    texture.width * 0.5f, texture.height * 0.5f, 0f,
    -texture.width * 0.5f, texture.height * 0.5f, 0f,
    0f, 0f, 1f,
    Material(TextureAttribute.createDiffuse(texture)),
    (Usage.Position or Usage.Normal or Usage.TextureCoordinates).toLong(),
)
