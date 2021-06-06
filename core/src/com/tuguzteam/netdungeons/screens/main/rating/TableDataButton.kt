package com.tuguzteam.netdungeons.screens.main.rating

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.tuguzteam.netdungeons.addRow
import com.tuguzteam.netdungeons.dec
import com.tuguzteam.netdungeons.toFloat
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.VerticalDragListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class TableDataButton(
    sortByText: String, block: TableBlock, scrollContainer: Container<Actor>
) : VisTextButton(sortByText, "toggle") {

    private val rowCount = 50

    // TODO("Pull amount of players from server")
    private var maxRowKey = 65
    private var wasDragged = false

    private val tableContent = VisTable(false)

    private val tableScroll: ScrollPane = ScrollPane(tableContent).apply {
        setOverscroll(false, false)
        fadeScrollBars = true
        setFlingTime(.001f)

        addListener(verticalDragListener(false))
        addListener(verticalDragListener(true))
    }

    // TODO("Pull data from server about the best 20 players")
    val data = mutableMapOf<Int, Pair<String, Int>>().apply {
        repeat(rowCount) { index ->
            this[index + 1] = sortByText to 0
        }
    }

    init {
        loadData()

        addListener(ClickListener {
            block.setPosition(sortByText)
            block.setPoints(sortByText)

            scrollContainer.actor = tableScroll
        })
    }

    @OptIn(ExperimentalTime::class)
    private fun verticalDragListener(checkDown: Boolean) =
        VerticalDragListener(checkDown) {
            if (canLoad(checkDown)) {
                // TODO("Pull amount of players from server")
                KtxAsync.launch {
                    loading(checkDown)

                    wasDragged--
                    delay(Duration.Companion.seconds(1))
                    wasDragged = false

                    downloading(checkDown)
                }
            }
        }

    @OptIn(ExperimentalTime::class)
    private suspend fun loading(loadBottom: Boolean) {
        val triple = Triple(
            VisLabel("Loading...", Align.center),
            VisLabel("Loading...", Align.center),
            VisLabel("Loading...", Align.center)
        )
        tableContent.clearChildren()

        if (!loadBottom) {
            addRow(tableContent, triple)
            tableScroll.scrollPercentY = 2f / (rowCount + 1f)
            loadData()
        } else {
            loadData()
            tableScroll.scrollPercentY = (rowCount - 1f) / (rowCount + 1f)
            addRow(tableContent, triple)
        }

        delay(Duration.Companion.seconds(.1))
        tableScroll.scrollPercentY = loadBottom.toFloat()
    }

    private fun downloading(downloadBottom: Boolean) {
        tableContent.clearChildren()

        if (!downloadBottom) {
            // TODO("Pull data from server about previous (rowCount/2) players")
            val firstKey = data.toSortedMap().firstKey()

            repeat(rowCount / 2) { index ->
                if (firstKey - index - 1 > 0)
                    data[firstKey - index - 1] = "Hooray!" to 0
                if (firstKey + (rowCount / 2) + index > 1 + rowCount - 1)
                    data.remove(firstKey + (rowCount / 2) + index)
            }
        } else {
            // TODO("Pull data from server about next (rowCount/2) players")
            val lastKey = data.toSortedMap().lastKey()

            repeat(rowCount / 2) { index ->
                if (lastKey + index + 1 < maxRowKey + 1)
                    data[lastKey + index + 1] = "Hooray!" to 0
                if (lastKey - (rowCount / 2) - index < maxRowKey - rowCount + 1)
                    data.remove(lastKey - (rowCount / 2) - index)
            }
        }

        loadData()
    }

    private fun loadData() {
        data.toSortedMap().forEach {
            addRow(
                tableContent, Triple(
                    it.key.toString(), it.value.first,
                    it.value.second.toString()
                )
            )
        }
    }

    private fun canLoad(checkDown: Boolean) =
        !wasDragged && tableScroll.scrollPercentY == checkDown.toFloat() &&
                ((checkDown && data.toSortedMap().lastKey() < maxRowKey)
                        || (!checkDown && data.toSortedMap().firstKey() > 1))
}
