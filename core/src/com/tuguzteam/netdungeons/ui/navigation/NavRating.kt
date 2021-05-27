package com.tuguzteam.netdungeons.ui.navigation

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.kotcrab.vis.ui.widget.VisImageTextButton
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisTable
import com.tuguzteam.netdungeons.heightFraction
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.RadioButtonGroup
import com.tuguzteam.netdungeons.ui.SplitPane
import com.tuguzteam.netdungeons.widthFraction
import ktx.actors.plusAssign

class NavRating(contentSplitPane: SplitPane, header: ContentHeader, navGame: NavGame) {
    private val ratingTableContent = VisTable(false)
    private val ratingTable = VisTable(false).apply {
        pad(
            heightFraction(.1f), heightFraction(1 / 8f),
            heightFraction(.1f), heightFraction(1 / 8f)
        )

        ratingTableHeader(this)
        add(VisScrollPane(ratingTableContent))
            .colspan(3).expand().row()
        ratingTableFooter(this)
    }

    init {
        addRow(ratingTableContent, Triple("...", "...", "..."))
//        addRow(ratingTableContent, Triple("...", "...", "..."))
//        addRow(ratingTableContent, Triple("...", "...", "..."))
//        addRow(ratingTableContent, Triple("...", "...", "..."))
//        addRow(ratingTableContent, Triple("...", "...", "..."))
//        addRow(ratingTableContent, Triple("...", "...", "..."))
//        addRow(ratingTableContent, Triple("...", "...", "..."))
//        addRow(ratingTableContent, Triple("...", "...", "..."))
//        addRow(ratingTableContent, Triple("...", "...", "..."))
//        addRow(ratingTableContent, Triple("...", "...", "..."))
//        addRow(ratingTableContent, Triple("...", "...", "..."))
//        addRow(ratingTableContent, Triple("...", "...", "..."))
//        addRow(ratingTableContent, Triple("...", "...", "..."))
//        addRow(ratingTableContent, Triple("...", "...", "..."))
//        addRow(ratingTableContent, Triple("...", "...", "..."))
//        addRow(ratingTableContent, Triple("...", "...", "..."))
    }

    private val content = VisScrollPane(ratingTable).apply {
        setOverscroll(false, false)
        setScrollbarsVisible(false)
        fadeScrollBars = false
        setFlingTime(0f)
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
            navGame.uncheck()
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
        val cellWidth = widthFraction(.025f)
        val cellHeight = heightFraction(.0375f)

        table.add(VisLabel(triple.first)).pad(
            cellHeight, cellWidth, cellHeight, cellWidth
        )
        table.add(VisLabel(triple.second)).expandX()
        table.add(VisLabel(triple.third)).pad(
            cellHeight, cellWidth, cellHeight, cellWidth
        )
        table.row()
    }
}