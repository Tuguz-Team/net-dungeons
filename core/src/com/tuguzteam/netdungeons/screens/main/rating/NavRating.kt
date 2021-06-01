package com.tuguzteam.netdungeons.screens.main.rating

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.tuguzteam.netdungeons.heightFraction
import com.tuguzteam.netdungeons.screens.main.ContentHeader
import com.tuguzteam.netdungeons.screens.main.NavButton
import com.tuguzteam.netdungeons.ui.RadioButtonGroup
import com.tuguzteam.netdungeons.ui.SplitPane
import ktx.actors.plusAssign

class NavRating(contentSplitPane: SplitPane, header: ContentHeader) {
    private val tableHeader = TableBlock(
        "Position", "Adventurer", "Score points")
    private val tableFooter = TableBlock(
        nickname = "фанат секса"
    )
    private val tableContent = Container<Actor>().fill()

    private val levelButton = TableDataButton(
        "Level", tableFooter, tableContent)
    private val winButton = TableDataButton(
        "Win count", tableFooter, tableContent)
    private val fragButton = TableDataButton(
        "Frag count", tableFooter, tableContent)

    private val ratingTable = VisTable(false).apply {
        tableHeader.addTo(this)
        add(tableContent).colspan(3).grow().row()
        tableFooter.addTo(this)
    }
    private val tableContainer = Container(ratingTable).fill().pad(heightFraction(.1f))

    private val radioButton = RadioButtonGroup(true,
        arrayListOf(levelButton, winButton, fragButton)
    )

    private val sortButtons = HorizontalGroup().apply {
        left().space(heightFraction(.125f))
            .padLeft(heightFraction(.1f))
        this += VisLabel("Sort by", Align.center)
        for (button in radioButton.groupButtons)
            this += button
    }

    val navButton = NavButton("Rating", contentSplitPane, tableContainer) {
        contentSplitPane.setSecondWidget(tableContainer)
        header.setFirstWidget(sortButtons)
    }
}
