package com.tuguzteam.netdungeons.objects

import com.badlogic.gdx.graphics.g3d.RenderableProvider

interface Renderable : Bounded {
    val renderableProviders: Sequence<RenderableProvider>
}
