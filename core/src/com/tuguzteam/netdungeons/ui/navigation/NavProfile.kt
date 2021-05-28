package com.tuguzteam.netdungeons.ui.navigation

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.kotcrab.vis.ui.layout.FlowGroup
import com.kotcrab.vis.ui.widget.VisImageTextButton
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.SplitPane

class NavProfile(contentSplitPane: SplitPane, header: ContentHeader) {
    val navButton = VisImageTextButton(
        "Profile",
        VisImageTextButton.VisImageTextButtonStyle(null, null, null, BitmapFont())
    ).apply {
        addListener(ClickListener {
            contentSplitPane.setSecondWidget(null)
            header.setFirstWidget(FlowGroup(false))
        })
    }
}
