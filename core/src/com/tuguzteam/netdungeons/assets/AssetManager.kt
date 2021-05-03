package com.tuguzteam.netdungeons.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable
import java.util.*

class AssetManager : Disposable {
    private val assetManager = AssetManager()
    private val loadTaskQueue: Queue<LoadTask> = LinkedList()

    private val models: MutableMap<ModelAsset, Model> = EnumMap(ModelAsset::class.java)
    private val textures: MutableMap<TextureAsset, Texture> = EnumMap(TextureAsset::class.java)
    private val skins: MutableMap<SkinAsset, Skin> = EnumMap(SkinAsset::class.java)

    val progress = assetManager.progress

    private data class LoadTask(val assets: Collection<Asset>, val onLoaded: () -> Unit)

    fun addLoadTask(vararg assets: Asset, onLoaded: () -> Unit) {
        filterAndLoad(*assets)
        loadTaskQueue.add(LoadTask(assets.asList(), onLoaded))
    }

    fun unload(vararg assets: Asset) {
        for (modelAsset in modelAssets(*assets)) {
            assetManager.unload(modelAsset.filename)
            models.remove(modelAsset)
        }
        for (textureAsset in textureAssets(*assets)) {
            assetManager.unload(textureAsset.filename)
            textures.remove(textureAsset)
        }
        for (skinAsset in skinAssets(*assets)) {
            assetManager.unload(skinAsset.filename)
            skins.remove(skinAsset)
        }
    }

    fun loadNow(vararg assets: Asset) {
        filterAndLoad(*assets)
        while (!assetManager.update()) {
            if (loaded(*assets)) break
        }
    }

    fun finishNow() = assetManager.finishLoading()

    fun update(): Boolean {
        val res = assetManager.update()
        checkLoadTask()
        return res
    }

    private fun modelAssets(vararg assets: Asset) = assets.filterIsInstance<ModelAsset>()

    private fun textureAssets(vararg assets: Asset) = assets.filterIsInstance<TextureAsset>()

    private fun skinAssets(vararg assets: Asset) = assets.filterIsInstance<SkinAsset>()

    private fun filterAndLoad(vararg assets: Asset) {
        for (modelAsset in modelAssets(*assets)) {
            assetManager.load(modelAsset.filename, Model::class.java)
        }
        for (textureAsset in textureAssets(*assets)) {
            assetManager.load(textureAsset.filename, Texture::class.java)
        }
        for (skinAsset in skinAssets(*assets)) {
            assetManager.load(skinAsset.filename, Skin::class.java)
        }
    }

    private fun checkLoadTask() {
        val task = loadTaskQueue.peek()
        val loaded = task?.assets?.let { loaded(*it.toTypedArray()) } ?: false
        if (loaded) {
            task.onLoaded()
            loadTaskQueue.poll()
        }
    }

    fun loaded(vararg assets: Asset) = assets.all { assetManager.isLoaded(it.filename) }

    operator fun get(modelAsset: ModelAsset): Model? {
        models[modelAsset] = models[modelAsset] ?: assetManager.get(modelAsset.filename, false)
        return models[modelAsset]
    }

    operator fun get(textureAsset: TextureAsset): Texture? {
        textures[textureAsset] =
            textures[textureAsset] ?: assetManager.get(textureAsset.filename, false)
        return textures[textureAsset]
    }

    operator fun get(skinAsset: SkinAsset): Skin? {
        skins[skinAsset] = skins[skinAsset] ?: assetManager.get(skinAsset.filename, false)
        return skins[skinAsset]
    }

    override fun dispose() {
        models.clear()
        loadTaskQueue.clear()
        assetManager.dispose()
    }
}
