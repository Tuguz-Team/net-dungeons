package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.create
import com.tuguzteam.netdungeons.with
import ktx.assets.dispose
import ktx.graphics.color
import ktx.math.vec3

abstract class TextureObject(
    position: ImmutableVector3,
    textureRegion: TextureRegion,
    val width: UInt,
    val height: UInt,
) : ModelObject(position, createRect(textureRegion, width, height)), Blendable {

    companion object : Disposable {
        override fun dispose() {
            models.values.dispose()
            models.clear()
        }
    }

    var color: Color = Color.WHITE.cpy()
        set(value) {
            materials[0] with ColorAttribute.createDiffuse(value)
            field = value
        }

    override var alpha = 1f
        set(value) {
            val color = color(color.r, color.g, color.b, value)
            this.color = color
            field = value
        }
}

private val models = mutableMapOf<ModelData, Model>()

private data class ModelData(val textureRegion: TextureRegion, val width: UInt, val height: UInt)

private fun createRect(textureRegion: TextureRegion, width: UInt, height: UInt): Model {
    val modelData = ModelData(textureRegion, width, height)
    if (modelData in models) {
        return models[modelData]!!
    }

    val attributes = (Usage.Position or Usage.Normal or Usage.TextureCoordinates).toLong()
    val material = Material(
        TextureAttribute.createDiffuse(textureRegion),
        BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA),
    )

    val x = width.toInt() * 0.5f
    val y = height.toInt() * 0.5f
    val corner00 = vec3(-x, -y)
    val corner10 = vec3(x, -y)
    val corner11 = vec3(x, y)
    val corner01 = vec3(-x, y)
    val normal = vec3(z = -1f)

    val model = ModelObject.modelBuilder.create {
        val meshPartBuilder = part("rect", GL20.GL_TRIANGLES, attributes, material)
        meshPartBuilder.rect(corner00, corner10, corner11, corner01, normal)
    }
    models[modelData] = model
    return model
}
