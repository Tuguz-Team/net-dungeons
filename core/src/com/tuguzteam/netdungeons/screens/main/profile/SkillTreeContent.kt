package com.tuguzteam.netdungeons.screens.main.profile

import com.badlogic.gdx.scenes.scene2d.Actor
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton

class SkillTreeContent(size: Float, skillWindow: SkillWindow) : VisTable() {

    private val cellWidth = size * 3
    private val cellHeight = size

    private val skillsAtRow = 3
    private val cellCountAtRow = (skillsAtRow * 2) + 1

    private val skills = mutableListOf<Skill>().apply {
        this.add(
            Skill(
                "Man at the Crossroad",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod " +
                        "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim " +
                        "veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea" +
                        " commodo consequat. Duis aute irure dolor in reprehenderit in voluptate" +
                        " velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat" +
                        " cupidatat non proident, sunt in culpa qui officia deserunt mollit" +
                        " anim id est laborum.", 1
            )
        )

        repeat(9) {
            this.add(
                Skill(
                    "Accurate Dodger",
                    "Dodge everything with 25% chance!", 2
                )
            )
        }
    }

    init {
        // First row for padding
        empty()

        // Skill tree's true content
        add(skillButton(skills[0]))
            .colspan(7).size(cellWidth, cellHeight).expandX()

        repeat(3) { index ->
            empty()

            repeat(3) {
                add(Actor()).size(size).expandX()
                add(skillButton(skills[index + 1]))
                    .size(cellWidth, cellHeight).expandX()
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

    private fun skillButton(skill: Skill) = VisTextButton(skill.name)

    private data class Skill(val name: String, val description: String, val cost: Int)
}
