package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.kotcrab.vis.ui.widget.VisSplitPane

open class SplitPane(
    first: Actor?, second: Actor?, vertical: Boolean,
    private val splitAmount: Float = .5f
) : VisSplitPane(first, second, vertical) {

    init {
        init()
    }

    private fun init() {
        setSplitAmount(splitAmount)
        setMinSplitAmount(splitAmount)
        setMaxSplitAmount(splitAmount + .000001f)
    }
}
