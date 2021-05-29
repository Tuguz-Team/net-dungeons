package com.tuguzteam.netdungeons.screens.main.rating

import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.tuguzteam.netdungeons.addLabel

class TableBlock(
    position: String? = null, nickname: String? = null, points: String? = null
) {
    private val positionLabel = VisLabel(position, Align.center)
    private val nicknameLabel = VisLabel(nickname, Align.center)
    private val pointsLabel = VisLabel(points, Align.center)

    fun setPosition(position: String) = positionLabel.setText(position)
    fun setPoints(points: String) = pointsLabel.setText(points)

    fun addTo(table: VisTable) {
        addLabel(table, positionLabel, pad = true)
        addLabel(table, nicknameLabel, expand = true, multiply = 5f)
        addLabel(table, pointsLabel, pad = true)
        table.row()
    }
}
