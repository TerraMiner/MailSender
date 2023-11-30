package ua.terra.gui.settings

import ua.terra.application.Instance
import ua.terra.application.Settings
import ua.terra.gui.numfield
import javafx.geometry.Pos
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.input.MouseButton
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.*

object PropertySection : VBox() {

    val box = combobox<String> {
        hgrow = Priority.ALWAYS
        maxWidth = Double.MAX_VALUE
        items.addAll(Instance.PromotionFiles)
        value = items.firstOrNull() ?: "No elements"
    }

    val numField = numfield {
        hgrow = Priority.ALWAYS
        promptText = "Messages per tick"
    }

    val themeField = textfield {
        hgrow = Priority.ALWAYS
        promptText = "Message theme"
    }

    val applyButton = button("Apply") {
        hgrow = Priority.ALWAYS
        maxWidth = Double.MAX_VALUE
        action {
            Settings.applySendProperties()
        }
    }

    val checkbox: CheckBox = checkbox {
        tooltip("If this property is enabled, after the first launch\nservice will be started automatically")
        setOnMouseClicked {
            if (it.button != MouseButton.PRIMARY) return@setOnMouseClicked
            if (atTime.value == -1) return@setOnMouseClicked
            atTime.value = -1
        }
    }

    val atTime: ComboBox<Int> = combobox {
        hgrow = Priority.ALWAYS
        maxWidth = Double.MAX_VALUE
        items.addAll(buildList { repeat(24) { add(it + 1) }; add(-1) })
        value = -1
        setOnMouseClicked {
            if (it.button != MouseButton.PRIMARY) return@setOnMouseClicked
            if (!checkbox.isSelected) return@setOnMouseClicked
            checkbox.isSelected = false
        }
    }

    init {
        addClass("box")
        hgrow = Priority.ALWAYS
        padding = insets(10.0)
        spacing = 10.0

        hbox {
            hgrow = Priority.ALWAYS
            text("Send Properties") {
                style {
                    fontSize = 16.px
                    fill = Color.rgb(220, 220, 220)
                }
            }
        }

        hbox {
            hgrow = Priority.ALWAYS
            alignment = Pos.CENTER
            spacing = 10.0

            text("Select file:") {
                style {
                    fontSize = 12.px
                    fill = Color.rgb(220, 220, 220)
                }
            }

            add(box)

            button(Instance.RefreshSym) {
                action {
                    box.items.clear()
                    box.items.addAll(Instance.PromotionFiles)
                    box.value = box.items.firstOrNull() ?: "No elements"
                }
            }
        }

        hbox {
            hgrow = Priority.ALWAYS
            alignment = Pos.CENTER
            spacing = 10.0

            text("Every 24/7 mode: ") {
                style {
                    fontSize = 12.px
                    fill = Color.rgb(220, 220, 220)
                }
            }
            add(atTime)
            add(checkbox)
        }

        hbox {
            alignment = Pos.CENTER
            spacing = 10.0

            text("Messages per account:") {
                style {
                    fontSize = 12.px
                    fill = Color.rgb(220, 220, 220)
                }
            }

            add(numField)
        }

        hbox {
            alignment = Pos.CENTER
            spacing = 10.0

            text("Theme of message:") {
                style {
                    fontSize = 12.px
                    fill = Color.rgb(220, 220, 220)
                }
            }

            add(themeField)
        }

        hbox {
            alignment = Pos.TOP_CENTER
            hgrow = Priority.ALWAYS
            add(applyButton)
        }
    }
}