package com.tuguzteam.netdungeons.screens.main.rating

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.kotcrab.vis.ui.widget.VisTextButton
import com.tuguzteam.netdungeons.ui.ClickListener

class TableDataButton(
    sortByText: String, block: TableBlock, scrollContainer: Container<Actor>
) : VisTextButton(sortByText, "toggle") {

    private val scroll = TableScroll()
    private val data = mutableMapOf<Int, Pair<String, Int>>()

    init {
        repeat(16) { index ->
            data[index + 1] = sortByText to 0
        }

        addListener(ClickListener {
            block.setPosition(sortByText)
            block.setPoints(sortByText)

            scrollContainer.actor = scroll
            scroll.setContentFrom(data)
        })
    }
}
