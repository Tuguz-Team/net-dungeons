package com.tuguzteam.netdungeons.screens.main.profile

import com.badlogic.gdx.scenes.scene2d.Actor
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton

class SkillTreeContent(size: Float) : VisTable() {

    private val cellWidth = size * 3
    private val cellHeight = size

    private val skillsAtRow = 3
    private val cellCountAtRow = (skillsAtRow * 2) + 1

    init {
        // First row for padding
        empty()

        // Skill tree's true content
        add(VisTextButton("Man at the Crossroad"))
            .colspan(7).size(cellWidth, cellHeight).expandX()

        repeat(3) {
            empty()

            repeat(3) {
                add(Actor()).size(size).expandX()
                add(VisTextButton("Accurate Dodger")).size(cellWidth, cellHeight).expandX()
            }
            add(Actor()).size(size).expandX()
        }

        // Last row for padding
        empty()
    }

    private fun empty() {
        row()
        repeat(cellCountAtRow) { add(Actor()).size(cellHeight).expandX() }
        row()
    }
}
