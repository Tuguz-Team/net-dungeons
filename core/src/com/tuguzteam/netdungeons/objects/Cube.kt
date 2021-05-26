package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.tuguzteam.netdungeons.ImmutableVector3

open class Cube(
    dimensions: ImmutableVector3,
    color: Color,
    position: ImmutableVector3 = ImmutableVector3.ZERO
) : ModelObject(
    position, model = ModelBuilder().createBox(
        dimensions.x, dimensions.y, dimensions.z,
        Material(ColorAttribute.createDiffuse(color)),
        (Usage.Position or Usage.Normal).toLong()
    )
) {
    var color = color
        set(value) {
            materials[0].set(ColorAttribute.createDiffuse(value))
            field = value
        }

    override fun dispose() {
        super.dispose()
        model.dispose()
    }
}
