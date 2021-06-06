package com.tuguzteam.netdungeons.screens.main.profile

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisImageButton
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisTable
import com.tuguzteam.netdungeons.addActor
import com.tuguzteam.netdungeons.heightFraction
import com.tuguzteam.netdungeons.screens.main.ContentHeader
import com.tuguzteam.netdungeons.screens.main.NavButton
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.SplitPane
import com.tuguzteam.netdungeons.widthFraction

class NavProfile(contentSplitPane: SplitPane, header: ContentHeader) {

    private val navPad = heightFraction(.1f)

    private val playerInfo = VisTable(false).apply {
        addActor(this, VisLabel("13", Align.center), navPad * 1.25f)

        add(
            Container(VisImageButton(null, null, null))
                .fill().size(heightFraction(.12f))
        ).grow()

        addActor(this, VisLabel("Name", Align.left))
    }

    private val skillWindow = SkillWindow(
        onYesOption = { /**/ },
        onNoOption = { /**/ }
    )
    private val skillTreeScroll = VisScrollPane(
        SkillTreeContent(navPad, skillWindow)
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

            add(VisLabel("+0", "medium")).size(0f, heightFraction(.05f))
                .padRight(heightFraction(.0625f)).row()
        }
    }).apply {
        setOverscroll(false, false)
        setScrollingDisabled(true, false)
        fadeScrollBars = true
        setFlingTime(0f)
    }

    private val playerStats = VisTable(false).apply {
        add(Actor()).grow()
        add(statsTable).top()
            .padLeft(heightFraction(.0625f))
    }

    private val skillTree = VisTable(false).apply {
        add(VisLabel("Skill tree", Align.center).apply {
            addListener(ClickListener {
                skillTreeScroll.scrollPercentX = .5f
                skillTreeScroll.scrollPercentY = 0f
            })
        }).pad(heightFraction(.025f))
            .size(widthFraction(.2f), heightFraction(.075f))

        add(VisLabel("New points: 0", "medium")
            .apply { setAlignment(Align.center) }).growX().pad(heightFraction(.025f))
            .size(0f, heightFraction(.075f)).row()

        add(skillTreeScroll).colspan(2).expand()
    }

    private val content = SplitPane(
        Container(playerStats).fill().pad(heightFraction(.1f))
            .padRight(heightFraction(.0625f)),
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
