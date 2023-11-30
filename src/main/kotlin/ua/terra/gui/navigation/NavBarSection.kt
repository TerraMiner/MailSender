package ua.terra.gui.navigation

import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.layout.VBox
import ua.terra.thread.replaceColor
import tornadofx.*

class NavBarSection : VBox() {

    val buttons = buildList {
        add(navbutton(ViewType.HOME) {
            spacing = 10.0
            imageview {
                addClass("img")
                image = Image("data/textures/home.png")
                fitWidth = 33.0
                fitHeight = 33.0
            }

            action {
                slide()
            }
        })

        add(navbutton(ViewType.SETTINGS) {
            spacing = 10.0
            imageview {
                addClass("img")
                image = Image("data/textures/settings.png").replaceColor(255,255,255)
                fitWidth = 33.0
                fitHeight = 33.0
            }

            action {
                slide()
            }
        })

        add(navbutton(ViewType.CONSOLE) {
            spacing = 10.0
            imageview {
                addClass("img")
                image = Image("data/textures/console.png")
                fitWidth = 33.0
                fitHeight = 33.0
            }

            action {
                slide()
            }
        })
    }

    init {
        padding = insets(10.0)
        spacing = 10.0
        alignment = Pos.TOP_CENTER
        separator()

        hbox {
            spacing = 10.0
            alignment = Pos.TOP_CENTER

            buttons.forEach {
                add(it)
            }
        }
    }
}