package com.tuguzteam.netdungeons

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.environment.BaseLight
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun <T : ModelBatch> T.use(camera: Camera, action: T.() -> Unit) {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    begin(camera)
    action()
    end()
}

interface KtxGestureAdapter : GestureDetector.GestureListener {
    override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean = false

    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean = false

    override fun longPress(x: Float, y: Float): Boolean = false

    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean = false

    override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean = false

    override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean = false

    override fun zoom(initialDistance: Float, distance: Float): Boolean = false

    override fun pinch(
        initialPointer1: Vector2,
        initialPointer2: Vector2,
        pointer1: Vector2,
        pointer2: Vector2
    ): Boolean = false

    override fun pinchStop() = Unit
}

fun <T : Actor> T.isDoneActing() = actions.isEmpty

fun widthFraction(fraction: Float) = Gdx.graphics.width * fraction
fun heightFraction(fraction: Float) = Gdx.graphics.height * fraction

operator fun Boolean.dec() = !this

operator fun BoundingBox.times(matrix: Matrix4): BoundingBox = BoundingBox(this).mul(matrix)

operator fun BoundingBox.timesAssign(matrix: Matrix4) {
    mul(matrix)
}

operator fun <T : BaseLight<T>> Environment.plusAssign(light: T) {
    add(light)
}

infix fun Environment.with(attribute: Attribute) {
    set(attribute)
}

fun addRow(table: VisTable, triple: Triple<String, String, String>) {
    addLabel(table, VisLabel(triple.first, Align.center), pad = true)
    addLabel(table, VisLabel(triple.second, Align.center), expand = true, multiply = 5f)
    addLabel(table, VisLabel(triple.third, Align.center), pad = true)
    table.row()
}

fun addLabel(
    table: VisTable, label: VisLabel, pad: Boolean = false,
    expand: Boolean = false, multiply: Float = 1f
) {
    val cellWidth = widthFraction(.025f)
    val cellHeight = heightFraction(.0375f)
    val cell = table.add(label).width(cellWidth * 3f * multiply)

    if (expand) cell.expandX()
    if (pad) cell.pad(cellHeight, cellWidth, cellHeight, cellWidth)
}
