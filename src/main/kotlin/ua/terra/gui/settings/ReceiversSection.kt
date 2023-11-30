package ua.terra.gui.settings

import ua.terra.application.Instance
import ua.terra.application.Settings
import javafx.geometry.Pos
import javafx.scene.control.SelectionMode
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import ua.terra.service.receiver.Receiver
import ua.terra.service.receiver.ReceiverManager
import tornadofx.*

object ReceiversSection : VBox() {

    val box = combobox<String> {
        items.addAll(Instance.Connection?.tables ?: listOf())
        value = items.firstOrNull() ?: "No elements"
    }

    val emailField = textfield {
        hgrow = Priority.ALWAYS
        promptText = "Enter mail"
    }

    val receiverListText = text("Receiver List") {
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
            Settings.applyReceiversSection()
            updateReceiversNode()
        }
    }

    val addReceiverButton = button {
        text = "Add receiver"
        hgrow = Priority.ALWAYS
        maxWidth = Double.MAX_VALUE

        action {
            if (emailField.text.isNullOrBlank()) return@action
            val receiver = Receiver(emailField.text)
            if (ReceiverManager.addReceiver(receiver)) {
                updateReceiversNode()
                listReceiverNode.items.add(receiver)
            }
        }
    }

    val listReceiverNode = listview<Receiver> {
        vgrow = Priority.ALWAYS
        selectionModel.selectionMode = SelectionMode.MULTIPLE

        val receivers = (Instance.Connection?.receivers ?: listOf()).asObservable()

        items.addAll(receivers)

        cellFormat { receiver ->
            text = receiver.userName

            setOnMouseClicked {
                val selectedIndex = items.indexOf(receiver)

                if (selectedIndex != -1) {
                    ReceiverManager.remove(items[selectedIndex].userName)
                    items.removeAt(selectedIndex)
                }
            }
        }

        tooltip("Click on element for remove!")

        fixedCellSize = 25.0
        minHeight = fixedCellSize * 3.0
        prefHeight = fixedCellSize * 3.0
    }

    fun updateReceiversNode() {
        listReceiverNode.items.clear()
        listReceiverNode.items.addAll((Instance.Connection?.receivers ?: listOf()).asObservable())
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

            add(receiverListText)

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
            }

            add(addReceiverButton)

            add(listReceiverNode)

            hbox {
                alignment = Pos.TOP_CENTER
                hgrow = Priority.ALWAYS
                add(applyButton)
            }
        }
    }
}