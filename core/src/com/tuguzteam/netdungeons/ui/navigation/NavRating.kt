package com.tuguzteam.netdungeons.ui.navigation

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisImageTextButton
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.tuguzteam.netdungeons.heightFraction
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.RadioButtonGroup
import com.tuguzteam.netdungeons.ui.SplitPane
//import com.tuguzteam.netdungeons.ui.ScrollPane
import com.tuguzteam.netdungeons.widthFraction
import ktx.actors.plusAssign

class NavRating(contentSplitPane: SplitPane, header: ContentHeader) {
    private val ratingTableContent = VisTable(false).apply {
        for (i in 1..10) {
            addRow(this, Triple(i.toString(), "...", "..."))
        }
    }
    private val ratingTableScroll = ScrollPane(ratingTableContent).apply {
        setOverscroll(false, false)
        fadeScrollBars = true
    }

    private val ratingTable = VisTable(false).apply {
        ratingTableHeader(this)
        add(ratingTableScroll).colspan(3).grow().row()
        ratingTableFooter(this)
    }

    private val content = VisTable().apply {
        add(ratingTable).pad(
            heightFraction(.1f), heightFraction(1 / 8f),
            heightFraction(.1f), heightFraction(1 / 8f)
        ).grow()
    }

    private val radioButton = RadioButtonGroup(
        true, arrayListOf("By level", "By wins", "By kills")
    )

    private val sortButtons = HorizontalGroup().apply {
        left().space(heightFraction(.05f)).padLeft(heightFraction(.05f))
        for (button in radioButton.groupButtons)
            this += button
    }

    val navButton = VisImageTextButton(
        "Rating",
        VisImageTextButton.VisImageTextButtonStyle(null, null, null, BitmapFont())
    ).apply {
        addListener(ClickListener {
            contentSplitPane.setSecondWidget(content)
            header.setFirstWidget(sortButtons)
        })
    }

    private fun ratingTableHeader(table: VisTable) =
        addRow(table, Triple(
            "Position", "Adventurer", "Level points"
        ))

    private fun ratingTableFooter(
        table: VisTable, number: Int = 0, nickname: String = "You", level: Int = 0
    ) =
        addRow(table, Triple(number.toString(), nickname, level.toString()))

    private fun addRow(table: VisTable, triple: Triple<String, String, String>) {
        addLabel(table, triple.first, pad = true)
        addLabel(table, triple.second, expand = true, multiply = 5f)
        addLabel(table, triple.third, pad = true)
        table.row()
    }

    private fun addLabel(
        table: VisTable, string: String, pad: Boolean = false,
        expand: Boolean = false, multiply: Float = 1f
    ) {
        val cellWidth = widthFraction(.025f)
        val cellHeight = heightFraction(.0375f)
        val cell = table.add(VisLabel(string, Align.center))
            .width(cellWidth * 3f * multiply)

        if (expand) cell.expandX()
        if (pad) cell.pad(cellHeight, cellWidth, cellHeight, cellWidth)
    }
}
