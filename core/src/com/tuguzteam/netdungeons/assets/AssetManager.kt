package com.tuguzteam.netdungeons.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.utils.Disposable
import java.util.*

class AssetManager : Disposable {
    private val assetManager = AssetManager()
    private val taskQueue: Queue<Task> = LinkedList()

    private val models: EnumMap<ModelAsset, Model?> = EnumMap(ModelAsset::class.java)

    val progress = assetManager.progress

    private data class Task(val assets: List<Asset>, val onLoaded: () -> Unit)

    fun addTask(vararg assets: Asset, onLoaded: () -> Unit) {
        val modelAssets = assets.filterIsInstance<ModelAsset>()
        for (modelAsset in modelAssets) {
            assetManager.load(modelAsset.filename, Model::class.java)
        }
        taskQueue.add(Task(assets.asList(), onLoaded))
    }

    fun update(): Boolean {
        val res = assetManager.update()
        checkTask()
        return res
    }

    private fun checkTask() {
        val task = taskQueue.peek()
        val allAssetsLoaded = task?.assets?.all { assetManager.isLoaded(it.filename) } ?: false
        if (allAssetsLoaded) {
            task.onLoaded()
            taskQueue.poll()
        }
    }

    operator fun get(modelAsset: ModelAsset): Model? {
        models[modelAsset] = models[modelAsset] ?: assetManager.get(
            modelAsset.filename,
            Model::class.java,
            false
        )
        return models[modelAsset]
    }

    override fun dispose() {
        models.clear()
        taskQueue.clear()
        assetManager.dispose()
    }
}
