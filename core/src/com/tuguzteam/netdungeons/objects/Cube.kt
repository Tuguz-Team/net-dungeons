package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder

class Cube : GameObject() {
    init {
        val modelBuilder = ModelBuilder()
        model = modelBuilder.createBox(
                5f, 5f, 5f,
                Material(ColorAttribute.createDiffuse(Color.GREEN)),
                (Usage.Position or Usage.Normal).toLong()
        )
        modelInstance = ModelInstance(model)
    }
}
