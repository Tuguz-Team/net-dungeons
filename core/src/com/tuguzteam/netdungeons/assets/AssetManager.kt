package com.tuguzteam.netdungeons.assets

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.I18NBundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import ktx.assets.async.AssetStorage
import java.util.EnumMap

@Suppress("MemberVisibilityCanBePrivate")
class AssetManager : Disposable {
    private val assetStorage = AssetStorage()

    private val models: MutableMap<ModelAsset, Model> = EnumMap(ModelAsset::class.java)
    private val textures: MutableMap<TextureAsset, Texture> = EnumMap(TextureAsset::class.java)
    private val skins: MutableMap<SkinAsset, Skin> = EnumMap(SkinAsset::class.java)
    private val bundles: MutableMap<I18NBundleAsset, I18NBundle> = EnumMap(I18NBundleAsset::class.java)

    val progress = assetStorage.progress

    suspend fun load(assets: Iterable<Asset>) {
        assets.map(::loadAsync).awaitAll()
    }

    suspend fun load(asset: Asset): Unit = loadAsync(asset).await()

    fun loadAsync(asset: Asset): Deferred<Unit> {
        val coroutineScope = CoroutineScope(assetStorage.asyncContext)
        return coroutineScope.async {
            when (asset) {
                is I18NBundleAsset -> bundles[asset] = assetStorage.load(asset.filename)
                is ModelAsset -> models[asset] = assetStorage.load(asset.filename)
                is SkinAsset -> skins[asset] = assetStorage.load(asset.filename)
                is TextureAsset -> textures[asset] = assetStorage.load(asset.filename)
            }
        }
    }

    fun loaded(assets: Iterable<Asset>): Boolean = assets.all(::loaded)

    fun loaded(asset: Asset): Boolean = when (asset) {
        is I18NBundleAsset -> bundles[asset] != null
        is ModelAsset -> models[asset] != null
        is SkinAsset -> skins[asset] != null
        is TextureAsset -> textures[asset] != null
    }

    operator fun get(modelAsset: ModelAsset) = models[modelAsset]

    operator fun get(textureAsset: TextureAsset) = textures[textureAsset]

    operator fun get(skinAsset: SkinAsset) = skins[skinAsset]

    operator fun get(bundleAsset: I18NBundleAsset) = bundles[bundleAsset]

    suspend fun unload(assets: Iterable<Asset>) {
        assets.map(::unloadAsync).awaitAll()
    }

    suspend fun unload(asset: Asset): Boolean = unloadAsync(asset).await()

    fun unloadAsync(asset: Asset): Deferred<Boolean> {
        val coroutineScope = CoroutineScope(assetStorage.asyncContext)
        return coroutineScope.async {
            when (asset) {
                is I18NBundleAsset -> {
                    bundles.remove(asset)
                    assetStorage.unload<I18NBundle>(asset.filename)
                }
                is ModelAsset -> {
                    models.remove(asset)
                    assetStorage.unload<Model>(asset.filename)
                }
                is SkinAsset -> {
                    skins.remove(asset)
                    assetStorage.unload<Skin>(asset.filename)
                }
                is TextureAsset -> {
                    textures.remove(asset)
                    assetStorage.unload<Texture>(asset.filename)
                }
            }
        }
    }

    override fun dispose() {
        models.clear()
        textures.clear()
        skins.clear()
        bundles.clear()
        assetStorage.dispose()
    }
}
