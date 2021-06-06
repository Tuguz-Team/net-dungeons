package com.tuguzteam.netdungeons.field.tile

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.ImmutableGridPoint2
import com.tuguzteam.netdungeons.field.Direction
import com.tuguzteam.netdungeons.immutableVec3
import com.tuguzteam.netdungeons.objects.Bounded
import com.tuguzteam.netdungeons.objects.Renderable
import com.tuguzteam.netdungeons.objects.TextureObject
import ktx.assets.dispose
import java.util.*

class Wall(
    position: ImmutableGridPoint2,
    val textureRegion: TextureRegion,
    val height: UInt,
    val walls: Set<Direction> = Direction.values().toSet(),
) : Tile(position) {

    private val topTextureObject = object : TextureObject(
        position = toImmutableVec3(position) + immutableVec3(y = (height * size).toFloat()),
        textureRegion, width = size, height = size,
    ) {
        init {
            transform.rotate(Vector3.X, -90f)
        }
    }

    private val wallTextureObjects: Map<Direction, List<TextureObject>>

    init {
        val map: MutableMap<Direction, List<TextureObject>> = EnumMap(Direction::class.java)
        walls.forEach { direction ->
            map[direction] = (0 until height.toInt()).map { index ->
                val wallPosition = toImmutableVec3(position) + when (direction) {
                    Direction.Forward -> immutableVec3(
                        y = size.toInt() * (index % height.toInt() + 0.5f),
                        z = size.toInt() / 2f,
                    )
                    Direction.Back -> immutableVec3(
                        y = size.toInt() * (index % height.toInt() + 0.5f),
                        z = -size.toInt() / 2f,
                    )
                    Direction.Left -> immutableVec3(
                        x = size.toInt() / 2f,
                        y = size.toInt() * (index % height.toInt() + 0.5f),
                    )
                    Direction.Right -> immutableVec3(
                        x = -size.toInt() / 2f,
                        y = size.toInt() * (index % height.toInt() + 0.5f),
                    )
                }
                object : TextureObject(wallPosition, textureRegion, width = size, height = size) {
                    init {
                        transform.rotate(Vector3.Y, direction.degrees)
                    }
                }
            }
        }
        wallTextureObjects = map
    }

    val textureObjects = wallTextureObjects.values.flatten() + topTextureObject

    override var alpha = 1f
        set(value) {
            textureObjects.forEach { textureObject ->
                textureObject.alpha = value
            }
            field = value
        }

    override val boundingBox = BoundingBox().apply {
        textureObjects.asSequence().map(Bounded::boundingBox).forEach(this::ext)
    }

    override val renderableProviders =
        (wallTextureObjects.values.asSequence().flatten() + topTextureObject)
            .map(Renderable::renderableProviders)
            .flatten()

    override fun dispose() {
        super.dispose()
        topTextureObject.dispose()
        wallTextureObjects.values.forEach(Iterable<Disposable>::dispose)
    }
}
