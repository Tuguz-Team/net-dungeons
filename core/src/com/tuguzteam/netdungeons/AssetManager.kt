package com.tuguzteam.netdungeons

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g3d.Model
import java.util.*

class AssetManager : AssetManager() {
    private val models: EnumMap<ModelType, Model> = EnumMap(ModelType::class.java)

    init {
        for (modelType in ModelType.values()) {
            super.load("models/${modelType.filename}", Model::class.java)
        }
    }

    enum class ModelType(val filename: String) {
        Suzanne("suzanne.obj")
    }

    operator fun get(modelType: ModelType): Model {
        models[modelType] = models[modelType]
            ?: super.get("models/${modelType.filename}", Model::class.java)
        return models[modelType]!!
    }
}
