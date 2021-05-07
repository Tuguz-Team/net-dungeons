package com.tuguzteam.netdungeons.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.I18NBundle
import java.util.*

class AssetManager : Disposable {
    private val assetManager = AssetManager()
    private val loadTaskQueue: Queue<LoadTask> = LinkedList()

    private val models: MutableMap<ModelAsset, Model> = EnumMap(ModelAsset::class.java)
    private val textures: MutableMap<TextureAsset, Texture> = EnumMap(TextureAsset::class.java)
    private val skins: MutableMap<SkinAsset, Skin> = EnumMap(SkinAsset::class.java)
    private val bundles: MutableMap<I18NBundleAsset, I18NBundle> = EnumMap(I18NBundleAsset::class.java)

    val progress = assetManager.progress

    private data class LoadTask(val assets: Collection<Asset>, val onLoaded: () -> Unit)

    fun addLoadTask(vararg assets: Asset, onLoaded: () -> Unit) {
        filterAndLoad(*assets)
        loadTaskQueue.add(LoadTask(assets.asList(), onLoaded))
    }

    fun unload(vararg assets: Asset) {
        for (asset in assets) {
            assetManager.unload(asset.filename)
            when (asset) {
                is ModelAsset -> models.remove(asset)
                is SkinAsset -> skins.remove(asset)
                is TextureAsset -> textures.remove(asset)
                is I18NBundleAsset -> bundles.remove(asset)
            }
        }
    }

    fun loadNow(vararg assets: Asset) {
        filterAndLoad(*assets)
        do {
            checkLoadTask()
            if (loaded(*assets)) break
        } while (!assetManager.update())
    }

    fun finishNow() = assetManager.finishLoading()

    fun update(): Boolean {
        val res = assetManager.update()
        checkLoadTask()
        return res
    }

    private fun filterAndLoad(vararg assets: Asset) {
        for (asset in assets) {
            val type = when (asset) {
                is ModelAsset -> Model::class.java
                is SkinAsset -> Skin::class.java
                is TextureAsset -> Texture::class.java
                is I18NBundleAsset -> I18NBundle::class.java
            }
            assetManager.load(asset.filename, type)
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

    operator fun get(bundleAsset: I18NBundleAsset): I18NBundle? {
        bundles[bundleAsset] = bundles[bundleAsset] ?: assetManager.get(bundleAsset.filename, false)
        return bundles[bundleAsset]
    }

    override fun dispose() {
        models.clear()
        loadTaskQueue.clear()
        assetManager.dispose()
    }
}
