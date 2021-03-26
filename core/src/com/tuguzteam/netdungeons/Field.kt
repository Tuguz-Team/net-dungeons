package com.tuguzteam.netdungeons

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Disposable
import com.tuguzteam.netdungeons.objects.Cube
import com.tuguzteam.netdungeons.objects.GameObject

class Field : Disposable, Iterable<GameObject> {
    private companion object {
        private const val SIZE = 4
    }

    val dimension = SIZE * 2 + 1

    private val array = Array<GameObject>(dimension * dimension) { i ->
        Cube(
                Vector3(5f, 5f, 5f),
                Vector3((i / dimension - SIZE) * 5f, -2.5f, (i % dimension - SIZE) * 5f)
        )
    }

    operator fun get(i: Int, j: Int): GameObject = array[i * dimension + j]

    override fun dispose() {
        for (gameObject in this) {
            gameObject.dispose()
        }
    }

    override fun iterator(): Iterator<GameObject> = array.iterator()
}
