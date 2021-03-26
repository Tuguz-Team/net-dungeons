package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Disposable

abstract class GameObject : Disposable {
    var position = Vector3()
    set(value) {
        field.set(value)
        modelInstance.transform?.setTranslation(field)
    }

    lateinit var model: Model
    protected set

    lateinit var modelInstance: ModelInstance
    protected set

    override fun dispose() {
        model.dispose()
    }
}
