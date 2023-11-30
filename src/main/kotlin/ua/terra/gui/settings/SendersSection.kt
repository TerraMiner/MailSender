package ua.terra.gui.settings

import ua.terra.application.Instance
import ua.terra.application.Settings
import javafx.geometry.Pos
import javafx.scene.control.SelectionMode
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import ua.terra.service.sender.Sender
import ua.terra.service.sender.SenderManager
import tornadofx.*

object SendersSection : VBox() {

    val box = combobox<String> {
        items.addAll(Instance.Connection?.tables ?: listOf())
        value = items.firstOrNull() ?: "No elements"
    }

    val passwordField = textfield {
        hgrow = Priority.ALWAYS
        promptText = "Enter key"
    }

    val emailField = textfield {
        hgrow = Priority.ALWAYS
        promptText = "Enter mail"
    }

    val senderListText = text("Sender List") {
        style {
            fontSize = 16.px
            fill = Color.rgb(220, 220, 220)
        }
    }

    val selectTableText = text("Select table:") {
        style {
            fontSize = 12.px
            fill = Color.rgb(220, 220, 220)
        }
    }

    val refreshTableButton = button(Instance.RefreshSym) {
        action {
            box.items.clear()
            box.items.addAll(Instance.Connection?.tables ?: listOf())
            box.value = box.items.firstOrNull() ?: "No elements"
        }
    }

    val applyButton = button("Apply/Reload") {
        hgrow = Priority.ALWAYS
        maxWidth = Double.MAX_VALUE
        action {
            Settings.applySendersSection()
            updateSendersNode()
        }
    }

    val addSenderButton = button {
        text = "Add sender"
        hgrow = Priority.ALWAYS
        maxWidth = Double.MAX_VALUE

        action {
            if (emailField.text.isNullOrBlank()) return@action
            val sender = Sender(emailField.text, passwordField.text)
            if (SenderManager.addSender(sender)) {
                updateSendersNode()
                listSendersNode.items.add(sender)
            }
        }
    }

    val listSendersNode = listview<Sender> {
        vgrow = Priority.ALWAYS
        selectionModel.selectionMode = SelectionMode.MULTIPLE

        val senders = (Instance.Connection?.senders ?: listOf()).asObservable()

        items.addAll(senders)

        cellFormat { sender ->
            text = sender.userName

            setOnMouseClicked {
                val selectedIndex = items.indexOf(sender)

                if (selectedIndex != -1) {
                    SenderManager.remove(items[selectedIndex].userName)
                    items.removeAt(selectedIndex)
                }
            }
        }

        tooltip("Click on element for remove!")

        fixedCellSize = 25.0
        minHeight = fixedCellSize * 3.0
        prefHeight = fixedCellSize * 3.0
    }

    fun updateSendersNode() {
        listSendersNode.items.clear()
        listSendersNode.items.addAll((Instance.Connection?.senders ?: listOf()).asObservable())
    }

    init {
        vbox {
            addClass("box")
            padding = insets(10.0)
            spacing = 10.0
            hgrow = Priority.ALWAYS
            vgrow = Priority.SOMETIMES
            alignment = Pos.TOP_LEFT
            minWidth = 80.0

            add(senderListText)

            hbox {
                spacing = 10.0
                hgrow = Priority.ALWAYS

                alignment = Pos.CENTER

                add(selectTableText)

                add(box)

                add(refreshTableButton)
            }

            hbox {
                spacing = 10.0

                add(emailField)

                add(passwordField)
            }

            add(addSenderButton)

            add(listSendersNode)

            hbox {
                alignment = Pos.TOP_CENTER
                hgrow = Priority.ALWAYS
                add(applyButton)
            }
        }
    }
}