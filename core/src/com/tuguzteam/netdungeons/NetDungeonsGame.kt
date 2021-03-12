package com.tuguzteam.netdungeons

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import ktx.app.KtxApplicationAdapter

class NetDungeonsGame : KtxApplicationAdapter {
    override fun render() {
        Gdx.gl.glClearColor(0f, 1f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }
}
