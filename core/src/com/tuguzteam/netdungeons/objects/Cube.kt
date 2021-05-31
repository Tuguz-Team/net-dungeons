package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.with

open class Cube(
    dimensions: ImmutableVector3,
    color: Color,
    position: ImmutableVector3,
) : ModelObject(
    position, model = modelBuilder.createBox(
        dimensions.x, dimensions.y, dimensions.z,
        Material(ColorAttribute.createDiffuse(color)),
        (Usage.Position or Usage.Normal).toLong()
    )
) {
    var color = color
        set(value) {
            materials[0] with ColorAttribute.createDiffuse(value)
            field = value
        }

    override fun dispose() {
        super.dispose()
        model.dispose()
    }
}
