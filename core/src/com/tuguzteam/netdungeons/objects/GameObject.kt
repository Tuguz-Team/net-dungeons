package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3

abstract class GameObject(position: Vector3) {
    var position: Vector3 = position
        set(value) {
            field = value
            if (::modelInstance.isInitialized) {
                modelInstance.transform?.setTranslation(field)
            }
        }

    lateinit var modelInstance: ModelInstance
        protected set
}
