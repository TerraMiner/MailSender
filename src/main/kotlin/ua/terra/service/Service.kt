package ua.terra.service

import ua.terra.application.Instance
import ua.terra.application.Settings
import ua.terra.application.Updater
import ua.terra.gui.console.Logger
import ua.terra.gui.navigation.ViewType
import javafx.beans.property.SimpleDoubleProperty
import ua.terra.service.receiver.Receiver
import ua.terra.service.receiver.ReceiverManager
import ua.terra.service.sender.LoginStatus
import ua.terra.service.sender.Sender
import ua.terra.service.sender.SenderManager
import ua.terra.service.sender.WorkState
import ua.terra.thread.appSync
import ua.terra.thread.async
import java.util.concurrent.ConcurrentLinkedQueue

object Service {

    val senders = mutableListOf<Sender>()
    val receivers = ConcurrentLinkedQueue<List<Receiver>>()

    var stage = ServiceStage.STOPPED

    var isStarted = false
    var isPaused = false

    var startTime = 0L

    var estimatedTime = 0L

    val elapsedTime get() = (if (isPaused || !isStarted) 0L else System.currentTimeMillis() - startTime) + estimatedTime

    var receiverCountOnStart = receivers.size
    var sended = 0

    var hasEveryDayStarter = false

    val progress = SimpleDoubleProperty(0.0)

    fun start() {
        if (isStarted) return

        if (!Settings.sentFile.exists()) {
            Logger.print("The file being sent is missing!")
            return
        }

        Logger.print("Starting send service!")

        if ((Settings.every24h || Settings.atTime != -1) && !hasEveryDayStarter) {
            Logger.print("Service started with 24/7 mode")
            hasEveryDayStarter = true
            when {
                Settings.every24h -> {
                    Updater.after(24 * 60 * 60 * 1000) {
                        hasEveryDayStarter = false
                        if (!Settings.every24h) return@after
                        start()
                    }
                }

                Settings.atTime > 0 -> {
                    Updater.atHour(Settings.atTime) {
                        hasEveryDayStarter = false
                        if (Settings.atTime == -1) return@atHour
                        start()
                    }
                }
            }
        }

        progress.value = -1.0
        async {
            stage = ServiceStage.LOADING_AUTHORIZATION
            load()
        }.thenAccept {
            stage = ServiceStage.Running
            receiverCountOnStart = receivers.size
            isStarted = true
            startTime = System.currentTimeMillis()
            estimatedTime = 0L
            appSync {
                progress.value = .0
            }
        }
    }

    fun pause() {
        if (!isStarted) return
        if (!isPaused) {
            Logger.print("Suspending send service!")
            stage = ServiceStage.PAUSED
            estimatedTime = elapsedTime
            isPaused = true
        } else {
            Logger.print("Resume send service!")
            stage = ServiceStage.Running
            isPaused = false
            startTime = System.currentTimeMillis()
        }
    }

    fun stop() {
        if (!isStarted) return
        Logger.print("Stopping send service!")
        sended = 0
        stage = ServiceStage.SAVING
        isStarted = false
        SenderManager.saveToTable()
        senders.clear()
        receivers.clear()
        stage = ServiceStage.STOPPED
    }

    fun load() {
        SenderManager.loginAll()
        while (SenderManager.senders.values.any { it.loginStatus === LoginStatus.LOGGED_OUT }) {

        }
        senders.addAll(SenderManager.senders.values.filter { it.accessible })
        receivers.addAll(ReceiverManager.receivers.values.toList().chunked(Settings.messagesPerMoment))
    }

    private fun tick() {
        if (!isStarted) return
        if (isPaused) return

        if (!canBeContinued()) {
            stop()
            return
        }

        async {
            senders.forEach { sender ->

                val list = receivers.element() ?: run {
                    Logger.print("All data has been sent!")
                    stop()
                    return@forEach
                }

                if (list.any { it.waitDataFrom != null }) {
                    receivers.remove()
                    return@forEach
                }

                val remain = sender.send(list, Settings.theme, Settings.sentFile) ?: return@forEach
                receivers.add(remain)
            }
        }.thenAccept { }


        appSync {
            if (Instance.CurrentView === ViewType.HOME) {
                progress.set(sended.toDouble() / receiverCountOnStart.coerceAtLeast(1).toDouble())
            }
        }
    }

    private fun canBeContinued(): Boolean {
        senders.removeIf { !it.accessible }

        if (senders.isEmpty()) {
            Logger.print("All senders have exhausted their resources!")
            return false
        }

        if (receivers.isEmpty() && senders.none { it.workState === WorkState.WORKING }) {
            Logger.print("All recipients have received their message!")
            return false
        }

        if (!receivers.isEmpty() && senders.none { s -> receivers.element().any { r -> s.canSendTo(r) } }) {
            Logger.print("None of the senders can anymore send a message to any of the recipients.")
            return false
        }

        return true
    }

    init {
        Updater.every(50) {
            runCatching {
                tick()
            }.getOrElse {
                Logger.printStackTrace(it)
            }
        }
    }

}