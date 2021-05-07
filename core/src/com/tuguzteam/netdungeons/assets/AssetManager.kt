package com.tuguzteam.netdungeons.assets

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.I18NBundle
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import java.util.*

class AssetManager : Disposable {
    private val assetStorage = AssetStorage()

    private val models: MutableMap<ModelAsset, Model> = EnumMap(ModelAsset::class.java)
    private val textures: MutableMap<TextureAsset, Texture> = EnumMap(TextureAsset::class.java)
    private val skins: MutableMap<SkinAsset, Skin> = EnumMap(SkinAsset::class.java)
    private val bundles: MutableMap<I18NBundleAsset, I18NBundle> = EnumMap(I18NBundleAsset::class.java)

    val progress = assetStorage.progress

    suspend fun load(vararg assets: Asset) {
        val mutableList = mutableListOf<Deferred<Any>>()
        assets.forEach { asset ->
            when (asset) {
                is I18NBundleAsset -> mutableList += KtxAsync.async(assetStorage.asyncContext) {
                    bundles[asset] = assetStorage.load(asset.filename)
                }
                is ModelAsset -> mutableList += KtxAsync.async(assetStorage.asyncContext) {
                    models[asset] = assetStorage.load(asset.filename)
                }
                is SkinAsset -> mutableList += KtxAsync.async(assetStorage.asyncContext) {
                    skins[asset] = assetStorage.load(asset.filename)
                }
                is TextureAsset -> mutableList += KtxAsync.async(assetStorage.asyncContext) {
                    textures[asset] = assetStorage.load(asset.filename)
                }
            }
        }
        mutableList.awaitAll()
    }

    fun loaded(vararg assets: Asset) = assets.all { asset ->
        when (asset) {
            is I18NBundleAsset -> bundles[asset] != null
            is ModelAsset -> models[asset] != null
            is SkinAsset -> skins[asset] != null
            is TextureAsset -> textures[asset] != null
        }
    }

    operator fun get(modelAsset: ModelAsset) = models[modelAsset]

    operator fun get(textureAsset: TextureAsset) = textures[textureAsset]

    operator fun get(skinAsset: SkinAsset) = skins[skinAsset]

    operator fun get(bundleAsset: I18NBundleAsset) = bundles[bundleAsset]

    suspend fun unload(vararg assets: Asset) {
        val mutableList = mutableListOf<Deferred<Boolean>>()
        assets.forEach { asset ->
            when (asset) {
                is ModelAsset -> {
                    models.remove(asset)
                    mutableList += KtxAsync.async(assetStorage.asyncContext) {
                        assetStorage.unload<Model>(asset.filename)
                    }
                }
                is SkinAsset -> {
                    skins.remove(asset)
                    mutableList += KtxAsync.async(assetStorage.asyncContext) {
                        assetStorage.unload<Skin>(asset.filename)
                    }
                }
                is TextureAsset -> {
                    textures.remove(asset)
                    mutableList += KtxAsync.async(assetStorage.asyncContext) {
                        assetStorage.unload<Texture>(asset.filename)
                    }
                }
                is I18NBundleAsset -> {
                    bundles.remove(asset)
                    mutableList += KtxAsync.async(assetStorage.asyncContext) {
                        assetStorage.unload<I18NBundle>(asset.filename)
                    }
                }
            }
        }
        mutableList.awaitAll()
    }

    override fun dispose() {
        models.clear()
        textures.clear()
        skins.clear()
        bundles.clear()
        assetStorage.dispose()
    }
}
