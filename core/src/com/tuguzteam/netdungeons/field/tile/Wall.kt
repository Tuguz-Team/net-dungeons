package com.tuguzteam.netdungeons.field.tile

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3
import com.tuguzteam.netdungeons.ImmutableGridPoint2
import com.tuguzteam.netdungeons.field.Direction
import com.tuguzteam.netdungeons.immutableVec3
import com.tuguzteam.netdungeons.objects.Renderable
import com.tuguzteam.netdungeons.objects.TextureObject
import java.util.EnumMap

class Wall(
    position: ImmutableGridPoint2,
    val height: UInt,
    val texture: Texture,
) : Tile(position), Renderable {

    private val topTextureObject = object : TextureObject(
        position = immutableVec3(
            x = position.x.toFloat(),
            y = (height * size).toFloat(),
            z = position.y.toFloat(),
        ),
        texture, width = size, height = size,
    ) {
        init {
            transform.rotate(Vector3.X, -90f)
        }
    }

    private val wallTextureObjects: MutableMap<Direction, List<TextureObject>> =
        EnumMap(Direction::class.java)

    operator fun plusAssign(direction: Direction) {
        val textureObject = wallTextureObjects[direction]
        if (textureObject == null) {
            wallTextureObjects[direction] = (0 until height.toInt()).map { index ->
                val wallPosition = when (direction) {
                    Direction.Forward -> immutableVec3(
                        x = -size.toInt() / 2f + 0.5f,
                        y = size.toInt() * (index % height.toInt() + 0.5f),
                        z = size.toInt() / 2f,
                    )
                    Direction.Back -> immutableVec3(
                        x = size.toInt() / 2f - 0.5f,
                        y = size.toInt() * (index % height.toInt() + 0.5f),
                        z = -size.toInt() / 2f,
                    )
                    Direction.Left -> immutableVec3(
                        x = size.toInt() / 2f,
                        y = size.toInt() * (index % height.toInt() + 0.5f),
                        z = -size.toInt() / 2f + 0.5f,
                    )
                    Direction.Right -> immutableVec3(
                        x = -size.toInt() / 2f,
                        y = size.toInt() * (index % height.toInt() + 0.5f),
                        z = size.toInt() / 2f - 0.5f,
                    )
                } + immutableVec3(x = position.x.toFloat(), z = position.y.toFloat())
                object : TextureObject(wallPosition, texture, width = size, height = size) {
                    init {
                        transform.rotate(Vector3.Y, direction.degrees)
                    }
                }
            }
        }
    }

    operator fun get(direction: Direction) = wallTextureObjects[direction]

    override val renderableProviders =
        (wallTextureObjects.values.asSequence().flatten() + topTextureObject)
            .map(Renderable::renderableProviders)
            .flatten()
}
