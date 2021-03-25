package com.tuguzteam.netdungeons

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.objects.Cube
import com.tuguzteam.netdungeons.objects.GameObject

class Field : Disposable {
    val grid = arrayOfNulls<Array<GameObject?>>(SIZE * 2 + 1)

    private companion object {
        private const val SIZE = 5
    }

    init {
        for (i in grid.indices) {
            grid[i] = arrayOfNulls(SIZE * 2 + 1)
            for (j in grid[i]!!.indices) {
                grid[i]!![j] = Cube(
                        Vector3(5f, 5f, 5f),
                        Vector3((i - SIZE) * 5f, -2.5f, (j - SIZE) * 5f)
                )
            }
        }
    }

    override fun dispose() {
        for (array in grid) {
            for (item in array!!) {
                item?.dispose()
            }
        }
    }
}
