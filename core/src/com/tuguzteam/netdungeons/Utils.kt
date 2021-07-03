package com.tuguzteam.netdungeons

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.environment.BaseLight
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

operator fun Appendable.plusAssign(charSequence: CharSequence) {
    append(charSequence)
}

operator fun Appendable.plusAssign(char: Char) {
    append(char)
}

@OptIn(ExperimentalContracts::class)
inline fun ModelBatch.use(camera: Camera, action: ModelBatch.() -> Unit) {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    begin(camera)
    action()
    end()
}

@OptIn(ExperimentalContracts::class)
inline fun ModelBuilder.create(action: ModelBuilder.() -> Unit): Model {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    begin()
    action()
    return end()
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
        pointer2: Vector2,
    ): Boolean = false

    override fun pinchStop() = Unit
}

fun Actor.isDoneActing() = actions.isEmpty

fun widthFraction(fraction: Float = 1f) = Gdx.graphics.width * fraction
fun heightFraction(fraction: Float = 1f) = Gdx.graphics.height * fraction

val framesPerSecond get() = Gdx.graphics.framesPerSecond

operator fun Boolean.dec() = !this

operator fun <T : BaseLight<T>> Environment.plusAssign(light: T) {
    add(light)
}

infix fun Environment.with(attribute: Attribute) = set(attribute)

fun addRow(table: VisTable, triple: Triple<Any, Any, Any>) {
    when (triple.first) {
        is String -> {
            addActor(table, VisLabel(triple.first as String, "medium")
                .apply { setAlignment(Align.center) }, pad = true)
            addActor(table, VisLabel(triple.second as String, "medium")
                .apply { setAlignment(Align.center) }, expand = true, multiply = 5f)
            addActor(table, VisLabel(triple.third as String, "medium")
                .apply { setAlignment(Align.center) }, pad = true)
        }
        is Actor -> {
            addActor(table, triple.first as Actor, pad = true)
            addActor(table, triple.second as Actor, expand = true, multiply = 5f)
            addActor(table, triple.third as Actor, pad = true)
        }
    }
    table.row()
}

fun addActor(
    table: VisTable, actor: Actor, pad: Boolean = false,
    expand: Boolean = false, multiply: Float = 1f
) {
    val cellWidth = widthFraction(.025f)
    val cellHeight = heightFraction(.0375f)
    val cell = table.add(actor).width(cellWidth * 3f * multiply)

    if (expand) cell.expandX()
    if (pad) cell.pad(cellHeight, cellWidth, cellHeight, cellWidth)
}

fun addActor(table: VisTable, actor: Actor, widthMultiplier: Float = 0f) {
    val cell = table.add(actor).width(widthMultiplier).pad(
        heightFraction(.1f) / 2.5f, heightFraction(.1f),
        heightFraction(.1f) / 2.5f, 0f
    )

    if (!widthMultiplier.toBoolean()) cell.grow()
}

infix fun Material.with(attribute: Attribute) = set(attribute)

fun Boolean.toFloat(): Float = if (this) 1f else 0f

fun Float.toBoolean(): Boolean = this != 0f
