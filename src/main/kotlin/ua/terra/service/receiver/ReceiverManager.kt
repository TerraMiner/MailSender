package ua.terra.service.receiver

import ua.terra.application.Instance
import ua.terra.application.Settings
import ua.terra.gui.console.Logger
import ua.terra.service.Service

object ReceiverManager {
    val receivers = mutableMapOf<String, Receiver>()

    fun addReceiver(userName: String): Boolean {
        return addReceiver(Receiver(userName))
    }

    fun addReceiver(receiver: Receiver): Boolean {
        val userName = receiver.userName
        if (receivers.containsKey(userName)) {
            Logger.print("Can not add receiver for $userName, because it already have been added!")
            return false
        }

        Logger.print("Added new receiver to service $userName!")
        receivers[userName] = receiver
        return true
    }


    fun loadFromTable() {
        if (Settings.receiversTable == "") {
            Logger.print("Table not selected!")
            return
        }
        Logger.print("Start loading receivers...")
        Service.stop()
        receivers.clear()
        Instance.Connection?.receivers?.forEach {
            addReceiver(it)
        }
        Logger.print("Completed loading receivers!")
    }

    fun saveToTable() {
        if (Settings.receiversTable == "") {
            Logger.print("Table not selected!")
            return
        }
        Logger.print("Start saving receivers...")
        Instance.Connection?.saveReceivers(receivers.values.toList())
        Logger.print("Completed saving receivers!")
    }

    fun remove(receiver: String) {
        receivers.remove(receiver)
        Instance.Connection?.removeReceiver(receiver)
    }

}