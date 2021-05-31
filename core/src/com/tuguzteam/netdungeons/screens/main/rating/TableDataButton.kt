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

    private val rowCount = 20
    private val tableContent = VisTable(false)

    @OptIn(ExperimentalTime::class)
    private val tableScroll = ScrollPane(tableContent).apply {
        var wasDragged = false

        setOverscroll(false, false)
        fadeScrollBars = true
        setFlingTime(.001f)

        addListener(VerticalDragListener(true) {
            if (!wasDragged && scrollPercentY == 1f) {
                val positionLabel = VisLabel("Loading...", Align.center)
                val nicknameLabel = VisLabel("Loading...", Align.center)
                val pointsLabel = VisLabel("Loading...", Align.center)
                addRow(
                    tableContent, Triple(
                        positionLabel, nicknameLabel, pointsLabel
                    )
                )
                wasDragged--

                KtxAsync.launch {
                    delay(Duration.Companion.seconds(1))
                    wasDragged = false
                    // Pulling new data from server
                    updateContent(
                        Triple(positionLabel, nicknameLabel, pointsLabel)
                    )
                }
            }
        })
    }

    val data = mutableMapOf<Int, Pair<String, Int>>().apply {
        repeat(rowCount) { index ->
            this[index + 1] = sortByText to 0
        }
    }

    init {
        data.forEach {
            addRow(tableContent, Triple(
                it.key.toString(), it.value.first,
                it.value.second.toString()
            ))
        }

        addListener(ClickListener {
            block.setPosition(sortByText)
            block.setPoints(sortByText)

            scrollContainer.actor = tableScroll
        })
    }

    private fun updateContent(triple: Triple<VisLabel, VisLabel, VisLabel>) {
        triple.first.setText("...")
        triple.second.setText("...")
        triple.third.setText("...")

        addRow(tableContent, Triple(
            "...", "...", "..."
        ))
    }
}
