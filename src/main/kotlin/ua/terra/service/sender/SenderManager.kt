package ua.terra.service.sender

import ua.terra.application.Instance
import ua.terra.application.Settings
import ua.terra.gui.console.Logger
import ua.terra.service.Service

object SenderManager {

    val senders = mutableMapOf<String, Sender>()

    fun addSender(sender: Sender): Boolean {
        val userName = sender.userName
        if (senders.containsKey(userName)) {
            Logger.print("Can not add sender for $userName, because it already have been added!")
            return false
        }

        if (sender.timeStamp > System.currentTimeMillis()) {
            Logger.print("User $userName already reached limit for today!")
            return false
        }

        Logger.print("Added new sender to service $userName!")
        senders[userName] = sender
        return true
    }

    fun loadFromTable() {
        if (Settings.sendersTable == "") {
            Logger.print("Table not selected!")
            return
        }
        Logger.print("Start loading senders...")
        Service.stop()
        senders.clear()
        Instance.Connection?.senders?.forEach {
            addSender(it)
        }
        Logger.print("Completed loading senders!")
    }

    fun saveToTable() {
        if (Settings.sendersTable == "") {
            Logger.print("Table not selected!")
            return
        }
        Logger.print("Start saving senders...")
        Instance.Connection?.saveSenders(senders.values.toList())
        Logger.print("Completed saving senders!")
    }

    fun loginAll() {
//        divideList(senders.values.toList(), ceil(sqrt(senders.size.toDouble())).toInt()).forEach { chunk ->
//            async {
//                chunk
//            }
//        }
        senders.values.forEach(Sender::login)
    }

    fun remove(sender: String) {
        senders.remove(sender)
        Instance.Connection?.removeSender(sender)
    }

}