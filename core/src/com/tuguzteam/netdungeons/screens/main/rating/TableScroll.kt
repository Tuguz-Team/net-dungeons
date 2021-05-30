package com.tuguzteam.netdungeons.screens.main.rating

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.kotcrab.vis.ui.widget.VisTable
import com.tuguzteam.netdungeons.addRow

class TableScroll : ScrollPane(null) {
    private val ratingTableContent = VisTable(false)

    init {
        setOverscroll(false, false)
        fadeScrollBars = true
        setFlingTime(.001f)

        actor = ratingTableContent
    }

    fun setContentFrom(data: MutableMap<Int, Pair<String, Int>>) {
        ratingTableContent.clearChildren()
        data.forEach {
            addRow(ratingTableContent, Triple(
                it.key.toString(), it.value.first, it.value.second.toString()
            ))
        }
    }
}