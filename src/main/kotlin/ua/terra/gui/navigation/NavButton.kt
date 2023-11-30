package ua.terra.gui.navigation

import ua.terra.application.Instance.CurrentView
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Button
import tornadofx.*

class NavButton(val target: ViewType, text: String) : Button(text) {
    init {
        if (CurrentView === target) {
            addClass("selected")
        }
    }

    fun slide() {
        val view = CurrentView
        CurrentView = target
        find(view.clazz).replaceWith(CurrentView.clazz, sizeToScene = false, centerOnScreen = false)
    }
}

fun EventTarget.navbutton(target: ViewType, text: String = "", graphic: Node? = null, op: NavButton.() -> Unit = {}) =
    NavButton(target, text).attachTo(this, op) {
        if (graphic != null) it.graphic = graphic
    }