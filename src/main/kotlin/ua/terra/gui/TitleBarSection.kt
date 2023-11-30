package ua.terra.gui

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*

class TitleBarSection(text: String, action: HBox.() -> Unit = {}) : VBox() {
    init {
        hgrow = Priority.ALWAYS
        alignment = Pos.TOP_LEFT

        hbox {
            add(getTitle(text))
            action.invoke(this)
        }
        separator()
    }
}

fun EventTarget.titleBar(text: String, action: HBox.() -> Unit = { }) {
    add(TitleBarSection(text, action))
}