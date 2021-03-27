package com.tuguzteam.netdungeons

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.ModelBatch

inline fun <T : ModelBatch> T.use(camera: Camera, action: (T) -> Unit) {
    begin(camera)
    action(this)
    end()
}
