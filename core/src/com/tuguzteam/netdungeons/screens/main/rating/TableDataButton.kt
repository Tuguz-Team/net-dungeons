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

                val positionContainer = Container<Actor>(
                    VisLabel("Loading...", Align.center)).fill()
                val nicknameContainer = Container<Actor>(
                    VisLabel("Loading...", Align.center)).fill()
                val pointsContainer = Container<Actor>(
                    VisLabel("Loading...", Align.center)).fill()

                loading(true, Triple(
                    positionContainer, nicknameContainer, pointsContainer
                ))
                wasDragged--

                KtxAsync.launch {
                    delay(Duration.Companion.seconds(1))
                    wasDragged = false

                    downloading(true, Triple(
                        positionContainer, nicknameContainer, pointsContainer
                    ))
                }
            }
        })

        addListener(VerticalDragListener(false) {
            if (!wasDragged && scrollPercentY == 0f) {

                val positionContainer = Container<Actor>(
                    VisLabel("Loading...", Align.center)).fill()
                val nicknameContainer = Container<Actor>(
                    VisLabel("Loading...", Align.center)).fill()
                val pointsContainer = Container<Actor>(
                    VisLabel("Loading...", Align.center)).fill()

                loading(false, Triple(
                    positionContainer, nicknameContainer, pointsContainer
                ))
                wasDragged--

                KtxAsync.launch {
                    delay(Duration.Companion.seconds(1))
                    wasDragged = false

                    downloading(false, Triple(
                        positionContainer, nicknameContainer, pointsContainer
                    ))
                }
            }
        })
    }

//    TODO("Pull data from server about the best 20 players")
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

    @ExperimentalTime
    private fun loading(loadBottom: Boolean, triple: Triple<Actor, Actor, Actor>) {
        tableContent.clearChildren()

        KtxAsync.launch {
            if (!loadBottom) {
                addRow(tableContent, triple)
                tableScroll.scrollPercentY = 2f / (rowCount + 1f)
                loadData()

                delay(Duration.Companion.seconds(.1))
                tableScroll.scrollPercentY = 0f
            } else {
                loadData()
                tableScroll.scrollPercentY = (rowCount - 1f) / (rowCount + 1f)
                addRow(tableContent, triple)

                delay(Duration.Companion.seconds(.1))
                tableScroll.scrollPercentY = 1f
            }
        }
    }

    private fun downloading(downloadBottom: Boolean,
        triple: Triple<Container<Actor>, Container<Actor>, Container<Actor>>
    ) {
        // tableContent.clearChildren()
        triple.first.actor = VisLabel("...", Align.center)
        triple.second.actor = VisLabel("...", Align.center)
        triple.third.actor = VisLabel("...", Align.center)
    }

    private fun loadData() {
        data.forEach {
            addRow(tableContent, Triple(
                it.key.toString(), it.value.first,
                it.value.second.toString()
            ))
        }
    }
}
