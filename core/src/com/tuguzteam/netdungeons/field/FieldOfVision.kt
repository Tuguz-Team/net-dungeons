package com.tuguzteam.netdungeons.field

import com.tuguzteam.netdungeons.ImmutableGridPoint2
import com.tuguzteam.netdungeons.field.tile.Tile
import com.tuguzteam.netdungeons.field.tile.Wall
import com.tuguzteam.netdungeons.immutableGridPoint2

object FieldOfVision {
    fun compute(field: Field, position: ImmutableGridPoint2, distance: UInt): Set<Tile> {
        val set: MutableSet<Tile> = HashSet((distance * distance).toInt())
        repeat(8) { octant ->
            val line = ShadowLine()
            var fullShadow = false
            for (row in 1 until distance.toInt()) {
                for (col in 0..row) {
                    val pos = position + transformOctant(row, col, octant.toUInt())
                    val tile = field[pos] ?: continue
                    set -= tile
                    if (!fullShadow) {
                        val projection = projectTile(row, col)
                        val visible = projection !in line
                        if (visible) {
                            set += tile
                            if (tile is Wall) {
                                line += projection
                                fullShadow = line.isFullShadow
                            }
                        }
                    }
                }
            }
        }
        return set
    }

    private fun projectTile(row: Int, col: Int): Shadow {
        val topLeft = col.toFloat() / (row + 2)
        val bottomRight = (col + 1).toFloat() / (row + 1)
        return Shadow(topLeft, bottomRight)
    }

    private fun transformOctant(row: Int, col: Int, octant: UInt) = when (octant) {
        0u -> immutableGridPoint2(col, -row)
        1u -> immutableGridPoint2(row, -col)
        2u -> immutableGridPoint2(row, col)
        3u -> immutableGridPoint2(col, row)
        4u -> immutableGridPoint2(-col, row)
        5u -> immutableGridPoint2(-row, col)
        6u -> immutableGridPoint2(-row, -col)
        7u -> immutableGridPoint2(-col, -row)
        else -> {
            throw IllegalArgumentException("octant must be less than 8: $octant given")
        }
    }
}

private data class Shadow(var start: Float, var end: Float) {
    operator fun contains(shadow: Shadow) = start <= shadow.start && end >= shadow.end
}

private class ShadowLine {
    private val shadows = mutableListOf<Shadow>()

    val isFullShadow get() = shadows.size == 1 && shadows[0].start == 0f && shadows[0].end == 1f

    operator fun contains(projection: Shadow): Boolean {
        shadows.forEach { shadow ->
            if (projection in shadow) return true
        }
        return false
    }

    operator fun plusAssign(shadow: Shadow) {
        var index = 0
        while (index < shadows.size) {
            if (shadows[index].start >= shadow.start) break
            index++
        }
        val overlappingPrevious =
            if (index > 0 && shadows[index - 1].end > shadow.start)
                shadows[index - 1]
            else null
        val overlappingNext =
            if (index < shadows.size && shadows[index].start < shadow.end)
                shadows[index]
            else null
        if (overlappingNext != null) {
            if (overlappingPrevious != null) {
                overlappingPrevious.end = overlappingNext.end
                shadows.removeAt(index)
            } else {
                overlappingNext.start = shadow.start
            }
        } else {
            if (overlappingPrevious != null) {
                overlappingPrevious.end = shadow.end
            } else {
                shadows.add(index, shadow)
            }
        }
    }
}
