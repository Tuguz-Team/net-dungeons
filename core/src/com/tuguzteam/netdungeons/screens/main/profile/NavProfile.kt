package com.tuguzteam.netdungeons.screens.main.profile

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.*
import com.tuguzteam.netdungeons.addRow
import com.tuguzteam.netdungeons.heightFraction
import com.tuguzteam.netdungeons.screens.main.ContentHeader
import com.tuguzteam.netdungeons.screens.main.NavButton
import com.tuguzteam.netdungeons.ui.SplitPane

class NavProfile(contentSplitPane: SplitPane, header: ContentHeader) {

    private val playerInfo = VisTable(false).apply {
        add(VisLabel("Level", Align.center))
            .pad(heightFraction(.025f))
        add(VisImageButton(null, null, null))
            .pad(heightFraction(.025f))
        add(VisLabel("Name", Align.left)).expandY()
            .padLeft(heightFraction(.025f))
    }

    private val skillTreeScroll = VisScrollPane(
        VisTable(true).apply {
            repeat(15) { addRow(this, Triple("BRUH", "BRUH", "BRUH")) }
        }
    ).apply {
        setOverscroll(false, false)
        fadeScrollBars = false
        setFlingTime(.001f)
    }

    private val statsTable = ScrollPane(VisTable(false).apply {
        repeat(15) {
            add(VisImageButton(null, null, null))
                .growX().pad(heightFraction(.0125f))
            add(VisLabel("100", Align.center)).pad(heightFraction(.025f)).row()
        }
    }).apply {
        setOverscroll(false, false)
        fadeScrollBars = true
        setFlingTime(.001f)
    }

    private val playerStats = VisTable(false).apply {
        add(Actor()).grow()
        add(statsTable).growY().pad(
            0f, heightFraction(.125f),
            0f, heightFraction(.125f)
        )
    }

    private val skillTree = VisTable(false).apply {
        add(VisLabel("Skill tree", Align.center)).growX()
            .pad(heightFraction(.025f))
        add(VisLabel("Progress points: 0", Align.center)).growX()
            .pad(heightFraction(.025f)).row()

        add(skillTreeScroll).colspan(2).expand()
    }

    private val content = SplitPane(
        Container(playerStats).fill().pad(heightFraction(.1f)).padRight(0f),
        Container(skillTree).fill().pad(heightFraction(.1f)).padLeft(0f),
        false, .5f)

    val navButton = NavButton("Profile", contentSplitPane, content) {
        contentSplitPane.setSecondWidget(content)
        header.setFirstWidget(
            Container(playerInfo).left()
                .padRight(heightFraction(.25f))
                .padLeft(heightFraction(.1f))
        )
    }
}
