package com.tuguzteam.netdungeons.field.tile

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.Vector3
import com.tuguzteam.netdungeons.field.Direction
import com.tuguzteam.netdungeons.immutableVec3
import com.tuguzteam.netdungeons.objects.Renderable
import com.tuguzteam.netdungeons.objects.TextureObject
import java.util.EnumMap

class Wall(
    position: GridPoint2,
    val height: UInt,
    val texture: Texture,
) : Tile(position), Renderable {

    private val textureObjects: MutableMap<Direction, List<TextureObject>> =
        EnumMap(Direction::class.java)

    operator fun plusAssign(direction: Direction) {
        val textureObject = textureObjects[direction]
        if (textureObject == null) {
            textureObjects[direction] = (0 until height.toInt()).map { index ->
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

    operator fun get(direction: Direction) = textureObjects[direction]

    override val renderableProviders = textureObjects.values.asSequence().flatten()
        .map(Renderable::renderableProviders).flatten()
}
