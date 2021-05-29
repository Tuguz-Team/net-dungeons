package com.tuguzteam.netdungeons.screens.main.rating

import com.kotcrab.vis.ui.widget.VisTextButton
import com.tuguzteam.netdungeons.ui.ClickListener

class TableContent(
    sortByText: String, block: TableBlock, scroll: TableScroll
) : VisTextButton(sortByText, "toggle") {

    private val data = mutableMapOf<Int, Pair<String, Int>>()
    var percentage: Float = 0.5f

    init {
        if (sortByText == "Level") {
            repeat(30) { index ->
                data[index + 1] = sortByText to 0
            }
        } else {
            repeat(20) { index ->
                data[index + 1] = sortByText to 0
            }
        }

        addListener(ClickListener {
            block.setPosition(sortByText)
            block.setPoints(sortByText)

            scroll.setContentFrom(data)
        })
    }
}