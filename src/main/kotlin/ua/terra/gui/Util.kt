package ua.terra.gui

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*

fun getTitle(text: String) = run {
    HBox().apply {
        padding = insets(5.0)
        alignment = Pos.TOP_LEFT

        text(text) {
            style {
                fontSize = 20.px
                fill = Color.rgb(220, 220, 220)
            }
        }
    }
}

var Node.dgrow: Priority?
    get() = HBox.getHgrow(this)
    set(value) {
        hgrow = value
        vgrow = value
    }

fun EventTarget.numfield(value: Number? = null, action: TextField.() -> Unit = {}): TextField =
    textfield {
        filterInput { it.controlNewText.isInt() }
    }.attachTo(this, action) {
        if (value !== null) it.text = value.toString()
    }

fun EventTarget.portfield(value: Number? = null, action: TextField.() -> Unit = {}): TextField =
    textfield {
        filterInput { it.controlNewText.isInt() &&
                it.controlNewText.matches("""^([1-9]\d{0,4}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$""".toRegex()) }
    }.attachTo(this, action) {
        if (value !== null) it.text = value.toString()
    }
