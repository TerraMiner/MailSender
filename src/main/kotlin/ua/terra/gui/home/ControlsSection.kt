package ua.terra.gui.home

import ua.terra.gui.dgrow
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import ua.terra.service.Service
import tornadofx.*

object ControlsSection : VBox() {

    init {
        addClass("box")
        dgrow = Priority.ALWAYS
        spacing = 10.0
        padding = insets(10.0)
        alignment = Pos.TOP_LEFT

        hbox {
            hgrow = Priority.ALWAYS
            text("Controls") {
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
            padding = insets(10.0)

            button("Start") {
                action {
                    Service.start()
                }
            }

            button("Pause") {
                action {
                    Service.pause()
                }
            }

            button("Stop") {
                action {
                    Service.stop()
                }
            }
        }
    }
}