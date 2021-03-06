package com.tuguzteam.netdungeons.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldListener

class ClickListener(private val onClick: () -> Unit) : ClickListener() {
    override fun clicked(event: InputEvent?, x: Float, y: Float) = onClick()
}

class KeyTypeListener(private val onKeyType: () -> Unit) : TextFieldListener {
    override fun keyTyped(textField: VisTextField?, c: Char) = onKeyType()
}

class VerticalDragListener(
    private val checkDragDown: Boolean, private val onDrag: () -> Unit
) : DragListener() {

    override fun touchDragged(
        event: InputEvent?, x: Float, y: Float, pointer: Int
    ) =
        if (Gdx.input.isTouched && (isDragDown() || isDragUp()))
            onDrag()
        else Unit

    private fun isDragDown() = checkDragDown && Gdx.input.deltaY < 0
    private fun isDragUp() = !checkDragDown && Gdx.input.deltaY > 0
}

fun doClick(actor: Actor) =
    actor.listeners.forEach { listener ->
        if (listener is ClickListener)
            listener.clicked(null, 0f, 0f)
    }
