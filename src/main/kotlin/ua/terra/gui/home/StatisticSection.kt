package ua.terra.gui.home

import ua.terra.TimeFormatter.formatTimeText
import ua.terra.application.Updater
import ua.terra.gui.dgrow
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import ua.terra.service.Service
import ua.terra.service.ServiceStage
import ua.terra.service.sender.SenderManager
import tornadofx.*

object StatisticSection : VBox() {

    val timeText = text {
        style {
            fontSize = 12.px
            fill = Color.rgb(220, 220, 220)
        }

        Updater.every(50) {
            text = "Time elapsed: ${formatTimeText(Service.elapsedTime)}"
        }
    }

    val progressText = text {

        style {
            fontSize = 12.px
            fill = Color.rgb(220, 220, 220)
        }

        Updater.every(50) {
            val progress = String.format("%.1f", Service.progress.value * 100)

            val suffix = when (Service.stage) {
                ServiceStage.STOPPED -> "Stopped"
                ServiceStage.SAVING -> "Saving"
                ServiceStage.Running -> "Running $progress%"
                ServiceStage.PAUSED -> "Paused $progress%"
                ServiceStage.LOADING_AUTHORIZATION -> "Authorization: ${SenderManager.senders.count { it.value.loggedIn }}/${SenderManager.senders.size}"
            }

            text = "Status: [$suffix]"
        }
    }

    val sendedText = text {
        style {
            fontSize = 12.px
            fill = Color.rgb(220, 220, 220)
        }

        Updater.every(50) {
            text = "Sended: ${Service.sended}"
        }
    }


    val bar = progressbar(Service.progress) {
        hgrow = Priority.ALWAYS
        maxWidth = Double.MAX_VALUE
        alignment = Pos.CENTER
    }

    val indicator = circle {
        addClass("circle")
        radius = 8.0

        Updater.every(50) {
            fill =
                if (Service.isStarted && Service.isPaused) Color.GRAY
                else if (Service.isStarted) Color.GREEN
                else Color.RED
        }
    }

    init {
        addClass("box")
        dgrow = Priority.ALWAYS
        spacing = 10.0
        padding = insets(10.0)
        alignment = Pos.TOP_LEFT

        hbox {
            hgrow = Priority.ALWAYS
            text("Statistics") {
                style {
                    fontSize = 16.px
                    fill = Color.rgb(220, 220, 220)
                }
            }
        }

        hbox {
            hgrow = Priority.ALWAYS
            spacing = 10.0
            add(timeText)
        }

        hbox {
            hgrow = Priority.ALWAYS
            spacing = 10.0
            add(sendedText)
        }

        hbox {
            hgrow = Priority.ALWAYS
            spacing = 10.0
            hbox {
                spacing = 10.0
                hgrow = Priority.ALWAYS
                add(progressText)
            }
            hbox {
                spacing = 10.0
                hgrow = Priority.ALWAYS
                add(bar)
                add(indicator)
            }
        }

    }
}