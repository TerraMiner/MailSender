package ua.terra.gui.settings

import ua.terra.gui.console.Logger
import ua.terra.application.Instance
import ua.terra.gui.portfield
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import ua.terra.thread.appSync
import ua.terra.thread.async
import tornadofx.*
import ua.terra.DatabaseConnection

object DatabaseSection : VBox() {

    val hostField = textfield {
        hgrow = Priority.ALWAYS
        promptText = "Enter host"
    }
    val portField = portfield {
        hgrow = Priority.ALWAYS
        promptText = "Enter port"
    }
    val userField = textfield {
        hgrow = Priority.ALWAYS
        promptText = "Enter user"
    }
    val passwordField = passwordfield {
        hgrow = Priority.ALWAYS
        promptText = "Enter password"
    }
    val baseField = textfield {
        hgrow = Priority.ALWAYS
        promptText = "Enter base"
    }


    val buttonNode = button {
        var isConnected = false

        text = "Connect"

        hgrow = Priority.ALWAYS
        maxWidth = Double.MAX_VALUE

        action {

            if (isConnected) {
                isConnected = false
                text = "Connect"
                isDisable = false

                Logger.print("Disconnected from database")
                Instance.Connection?.close()
                Instance.Connection = null
            } else {
                text = "Connecting..."
                isDisable = true

                async {

                    runCatching {
                        DatabaseConnection.of(
                            hostField.text,
                            portField.text,
                            userField.text,
                            passwordField.text,
                            baseField.text
                        )
                    }.getOrElse { e ->
                        Logger.printStackTrace(e)
                        Logger.print("Check your credentials!")
                        null
                    }

                }.thenAccept {
                    Instance.Connection = it
                    isConnected = it !== null

                    if (!isConnected) {
                        appSync {
                            text = "Connect"
                            isDisable = false
                        }
                        return@thenAccept
                    }

                    appSync {
                        text = "Disconnect"
                        isDisable = false
                    }
                    Logger.print("Connected to ${hostField.text.ifBlank { "localhost" }}, ${baseField.text}")
                }
            }
        }
    }


    init {
        addClass("box")
        padding = insets(10.0)
        spacing = 10.0
        hgrow = Priority.ALWAYS

        hbox {
            hbox {
                hgrow = Priority.ALWAYS
                text("Database") {
                    style {
                        fontSize = 16.px
                        fill = Color.rgb(220, 220, 220)
                    }
                }
            }

            spacing = 10.0
        }

        hbox {
            spacing = 10.0
            add(hostField)
            add(portField)
        }

        hbox {
            spacing = 10.0
            add(userField)
            add(passwordField)
        }

        hbox {
            spacing = 10.0
            add(baseField)
        }

        hbox {
            alignment = Pos.CENTER
            add(buttonNode)
        }
    }
}
