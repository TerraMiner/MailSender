package ua.terra.gui.navigation

import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import ua.terra.thread.replaceColor
import tornadofx.*
import ua.terra.gui.console.Logger
import ua.terra.gui.dgrow
import java.awt.Desktop
import java.net.URI

class NavBarSection : VBox() {

    val buttons = buildList {
        add(navbutton(ViewType.HOME) {
            spacing = 10.0
            imageview {
                addClass("img")
                image = Image("data/textures/home.png")
                fitWidth = 33.3
                fitHeight = 33.3
                isPreserveRatio = true
                isSmooth = true
            }

            action {
                slide()
            }
        })

        add(navbutton(ViewType.SETTINGS) {

            spacing = 10.0
            imageview {
                addClass("img")
                image = Image("data/textures/settings.png").replaceColor(255, 255, 255)
                fitWidth = 33.3
                fitHeight = 33.3
                isPreserveRatio = true
                isSmooth = true
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
                fitWidth = 33.3
                fitHeight = 33.3
                isPreserveRatio = true
                isSmooth = true
            }

            action {
                slide()
            }
        })
    }

    val initialsButtons = buildList {
        add(button {

            spacing = 10.0
            imageview {
                addClass("img")
                image = Image("data/textures/github-mark-white.png")
                fitWidth = 33.3
                fitHeight = 33.3
                isPreserveRatio = true
                isSmooth = true
            }

            action {
                openWebpage("https://github.com/TerraMiner")
            }
        })
        add(button {
            vgrow = Priority.ALWAYS
            maxHeight = Double.MAX_VALUE
            spacing = 10.0

            imageview {
                addClass("img")
                image = Image("data/textures/discord-mark-white.png")
                fitWidth = 33.3
                fitHeight = 27.7
                isPreserveRatio = true
                isSmooth = true
            }

            action {
                openWebpage("https://discordapp.com/users/467749502527471636/")
            }
        })
    }


    init {
        padding = insets(10.0)
        spacing = 10.0
        alignment = Pos.TOP_LEFT
        separator()

        hbox {
            hgrow = Priority.ALWAYS
            alignment = Pos.CENTER

            hbox {
                hgrow = Priority.ALWAYS
                spacing = 10.0
                alignment = Pos.TOP_LEFT

                buttons.forEach {
                    add(it)
                }
            }

            hbox {
                alignment = Pos.TOP_RIGHT
                spacing = 10.0
                initialsButtons.forEach {
                    add(it)
                }
            }
        }
    }

    private fun openWebpage(url: String) {
        try {
            val uri = URI(url)
            Desktop.getDesktop().browse(uri)
        } catch (e: Exception) {
            Logger.printStackTrace(e)
        }
    }
}