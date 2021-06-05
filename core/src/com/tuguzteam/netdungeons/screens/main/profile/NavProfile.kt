package com.tuguzteam.netdungeons.screens.main.profile

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.*
import com.tuguzteam.netdungeons.addRow
import com.tuguzteam.netdungeons.addActor
import com.tuguzteam.netdungeons.heightFraction
import com.tuguzteam.netdungeons.screens.main.ContentHeader
import com.tuguzteam.netdungeons.screens.main.NavButton
import com.tuguzteam.netdungeons.ui.SplitPane

class NavProfile(contentSplitPane: SplitPane, header: ContentHeader) {

    private val navPad = heightFraction(.1f)

    private val playerInfo = VisTable(false).apply {
        addActor(this, VisLabel("Level", Align.center), navPad * 2)

        add(Container(VisImageButton(null, null, null))
            .fill().size(heightFraction(.12f))).grow()

        addActor(this, VisLabel("Name", Align.left), navPad * 9)
    }

    private val skillTreeScroll = VisScrollPane(
        VisTable(true).apply {
            repeat(15) { addRow(this, Triple("BRUH", "BRUH", "BRUH")) }
        }
    ).apply {
        setOverscroll(false, false)
        fadeScrollBars = false
        setFlingTime(0f)
    }

    private val statsTable = ScrollPane(VisTable(false).apply {
        repeat(5) {
            add(Container(
                VisImageButton(null, null, null))
                .fill().pad(heightFraction(.02f))
            ).size(heightFraction(.1f))

            add(VisLabel("100", Align.center)).size(0f, heightFraction(.05f))
                .pad(0f, heightFraction(.05f), 0f, heightFraction(.05f))

            add(VisLabel("+0", Align.center)).size(0f, heightFraction(.05f))
                .padRight(heightFraction(.05f)).row()
        }
    }).apply {
        setOverscroll(false, false)
        setScrollingDisabled(true, false)
        fadeScrollBars = true
        setFlingTime(0f)
    }

    private val playerStats = VisTable(false).apply {
        add(Actor()).grow()
        add(statsTable).top().pad(
            0f, heightFraction(.0625f),
            0f, heightFraction(.0625f)
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
        false
    )

    val navButton = NavButton("Profile", contentSplitPane, content) {
        contentSplitPane.setSecondWidget(content)
        header.setFirstWidget(
            Container(playerInfo).fill().padRight(navPad)
        )
    }
}
