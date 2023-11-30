package ua.terra.gui.home

import ua.terra.application.Updater
import ua.terra.gui.dgrow
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import ua.terra.service.receiver.ReceiverManager
import ua.terra.service.sender.LoginStatus
import ua.terra.service.sender.SenderManager
import tornadofx.*

object AccountInfoSection : VBox() {

    val sendersText = text {
        style {
            fontSize = 12.px
            fill = Color.rgb(220, 220, 220)
        }

        Updater.every(50) {
            text = "Loaded Senders: ${SenderManager.senders.size}"
        }
    }

    val receiversText = text {
        style {
            fontSize = 12.px
            fill = Color.rgb(220, 220, 220)
        }

        Updater.every(50) {
            text = "Loaded Receivers: ${ReceiverManager.receivers.size}"
        }
    }

    val totalSendsRemain = text {
        style {
            fontSize = 12.px
            fill = Color.rgb(220, 220, 220)
        }

        Updater.every(50) {
            text = "Sends Remain: ${SenderManager.senders.values.sumOf { it.sends }}"
        }
    }

    val totalLoggedIn = text {
        style {
            fontSize = 12.px
            fill = Color.rgb(220, 220, 220)
        }

        Updater.every(50) {
            text = "Logged in: ${SenderManager.senders.values.count { it.loggedIn }}"
        }
    }

    val timedOutLoggins = text {
        style {
            fontSize = 12.px
            fill = Color.rgb(220, 220, 220)
        }

        Updater.every(50) {
            text = "Timed out logins: ${SenderManager.senders.values.count { it.loginStatus === LoginStatus.TIMED_OUT }}"
        }
    }

    val badAccounts = text {
        style {
            fontSize = 12.px
            fill = Color.rgb(220, 220, 220)
        }

        Updater.every(50) {
            text = "Bad accounts: ${
                SenderManager.senders.values.count { 
                it.loginStatus === LoginStatus.WRONG_DATA ||
                it.loginStatus === LoginStatus.UNEXCEPTED_ERROR
            }}"
        }
    }

    val reachedLimit = text {
        style {
            fontSize = 12.px
            fill = Color.rgb(220, 220, 220)
        }

        Updater.every(50) {
            text = "Reached limit: ${
                SenderManager.senders.values.count {
                it.loginStatus === LoginStatus.REACHED_LIMIT
            }}"
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
            text("Account Info") {
                style {
                    fontSize = 16.px
                    fill = Color.rgb(220, 220, 220)
                }
            }
        }

        hbox {
            hgrow = Priority.ALWAYS
            spacing = 10.0
            add(sendersText)
        }

        hbox {
            hgrow = Priority.ALWAYS
            spacing = 10.0
            add(receiversText)
        }

        hbox {
            hgrow = Priority.ALWAYS
            spacing = 10.0
            add(totalSendsRemain)
        }

        hbox {
            hgrow = Priority.ALWAYS
            spacing = 10.0
            add(totalLoggedIn)
        }

        hbox {
            hgrow = Priority.ALWAYS
            spacing = 10.0
            add(timedOutLoggins)
        }

        hbox {
            hgrow = Priority.ALWAYS
            spacing = 10.0
            add(badAccounts)
        }

        hbox {
            hgrow = Priority.ALWAYS
            spacing = 10.0
            add(reachedLimit)
        }
    }
}