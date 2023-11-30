package ua.terra.gui.console

import ua.terra.application.Instance
import ua.terra.gui.navigation.NavBarSection
import ua.terra.gui.TitleBarSection
import ua.terra.gui.console.command.CommandHandler
import ua.terra.gui.dgrow
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import tornadofx.*

class ConsoleView : View("Mail Sender") {

    init {
        primaryStage.setOnCloseRequest {
            Instance.IsClosed = true
        }
    }

    val input = textfield {
        addClass("console")
        hgrow = Priority.ALWAYS
        vgrow = Priority.NEVER
        promptText = "Execute command..."

        setOnAction {
            CommandHandler.handleCommand(text)
            text = ""
        }
    }

    override val root = borderpane {

        prefWidth = Instance.PrefWidth
        prefHeight = Instance.PrefHeight

        stylesheets.add("styles/style.css")

        center {
            dgrow = Priority.ALWAYS

            vbox {
                padding = insets(10.0)
                spacing = 10.0
                alignment = Pos.TOP_CENTER

                add(TitleBarSection("Console"))

                add(Logger)

                add(input)
            }
        }

        bottom {
            add(NavBarSection())
        }
    }
}