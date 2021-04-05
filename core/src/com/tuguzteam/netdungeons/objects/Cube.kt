package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.collision.BoundingBox
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.toMutable

open class Cube(
        dimensions: ImmutableVector3,
        color: Color,
        position: ImmutableVector3
) : GameObject(position) {
    private var model: Model = ModelBuilder().createBox(
            dimensions.x, dimensions.y, dimensions.z,
            Material(ColorAttribute.createDiffuse(color)),
            (Usage.Position or Usage.Normal).toLong()
    )

    init {
        modelInstance = ModelInstance(model, position.toMutable())
        boundingBox = modelInstance.calculateBoundingBox(BoundingBox()).apply {
            mul(modelInstance.transform)
        }
    }

    override fun dispose() {
        super.dispose()
        model.dispose()
    }
}
