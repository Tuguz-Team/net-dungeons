package com.tuguzteam.netdungeons

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g3d.Model
import java.util.EnumMap

class AssetManager : AssetManager() {
    private val models: MutableMap<ModelType, Model> = EnumMap(ModelType::class.java)

    init {
        for (type in ModelType.values()) {
            load(type.filename, Model::class.java)
            models[type] = super.get(type.filename, Model::class.java)
        }
    }

    enum class ModelType(val filename: String)

    operator fun get(modelType: ModelType): Model = models[modelType]!!
}
