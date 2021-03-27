package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.math.Vector3
import ktx.graphics.color

open class Cube(dimensions: Vector3) : GameObject() {
    init {
        val modelBuilder = ModelBuilder()
        model = modelBuilder.createBox(
                dimensions.x, dimensions.y, dimensions.z,
                Material(ColorAttribute.createDiffuse(
                        color(red = random(), green = random(), blue = random(), alpha = 1f)
                )),
                (Usage.Position or Usage.Normal).toLong()
        )
        modelInstance = ModelInstance(model)
    }

    constructor(dimensions: Vector3, position: Vector3) : this(dimensions) {
        this.position = position
    }
}
