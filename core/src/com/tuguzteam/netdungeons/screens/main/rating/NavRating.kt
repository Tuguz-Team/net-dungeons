package com.tuguzteam.netdungeons.screens.main.rating

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.kotcrab.vis.ui.widget.VisImageTextButton
import com.kotcrab.vis.ui.widget.VisTable
import com.tuguzteam.netdungeons.heightFraction
import com.tuguzteam.netdungeons.screens.main.ContentHeader
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.RadioButtonGroup
import com.tuguzteam.netdungeons.ui.SplitPane
import ktx.actors.plusAssign

class NavRating(contentSplitPane: SplitPane, header: ContentHeader) {
    private val ratingTableHeader = TableBlock(
        "Position", "Adventurer", "Score points")
    private val ratingTableFooter = TableBlock(
        nickname = "фанат секса"
    )

    private val ratingTableScroll = TableScroll()
    private val levelButton = TableContent(
        "Level", ratingTableFooter, ratingTableScroll)
    private val winButton = TableContent(
        "Win count", ratingTableFooter, ratingTableScroll)
    private val fragButton = TableContent(
        "Frag count", ratingTableFooter, ratingTableScroll)

    private val ratingTable = VisTable(false).apply {
        ratingTableHeader.addTo(this)
        add(ratingTableScroll).colspan(3).grow().row()
        ratingTableFooter.addTo(this)
    }

    private val content = VisTable().apply {
        add(ratingTable).pad(
            heightFraction(.1f), heightFraction(1 / 8f),
            heightFraction(.1f), heightFraction(1 / 8f)
        ).grow()
    }

    private val radioButton = RadioButtonGroup(
        checked = true, clicked = true,
        buttons = arrayListOf(levelButton, winButton, fragButton)
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
}
