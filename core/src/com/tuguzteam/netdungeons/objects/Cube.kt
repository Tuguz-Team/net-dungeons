package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Disposable

open class Cube(
        dimensions: Vector3,
        color: Color,
        position: Vector3
) : GameObject(position), Disposable {
    private var model = ModelBuilder().createBox(
            dimensions.x, dimensions.y, dimensions.z,
            Material(ColorAttribute.createDiffuse(color)),
            (Usage.Position or Usage.Normal).toLong()
    )

    init {
        modelInstance = ModelInstance(model, position)
    }

    override fun dispose() {
        model.dispose()
    }
}
