package com.tuguzteam.netdungeons.field

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.math.Vector3
import com.tuguzteam.netdungeons.ImmutableVector3
import com.tuguzteam.netdungeons.objects.ModelObject
import com.tuguzteam.netdungeons.toMutable

class Player(
    position: ImmutableVector3,
    model: Model,
    direction: Direction = Direction.Forward,
) : ModelObject(position, model) {

    var direction = direction
        set(value) {
            transform.setToRotation(Vector3.Y, value.degrees)
            transform.setTranslation(position.toMutable())
            field = value
        }
}
