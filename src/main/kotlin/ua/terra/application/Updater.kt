package ua.terra.application

import ua.terra.getTimeDifference
import ua.terra.service.receiver.ReceiverManager
import ua.terra.service.sender.SenderManager
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.fixedRateTimer
import kotlin.system.exitProcess

object Updater {

    val actions = CopyOnWriteArrayList<Runnable>()

    class Runnable(val delay: Long, val period: Long, val times: Int, val action: Runnable.() -> Unit) {
        init {
            require(delay != -1L || period != -1L) { "Delay and Period can not be -1" }
        }

        private val creationTime = System.currentTimeMillis()
        private var lastDo = creationTime
        private var remain = times

        fun cancel() {
            actions.remove(this)
        }

        fun run() {
            if (lastDo != -1L && creationTime + delay > System.currentTimeMillis()) return
            if (lastDo + period > System.currentTimeMillis()) return
            lastDo = System.currentTimeMillis()
            action(this)
            if (remain < 0) return

            remain--

            if (remain <= 0) cancel()
        }
    }

    val worker = fixedRateTimer("updater", false, 0, 1) {
        actions.forEach(Runnable::run)

        if (Instance.IsClosed) {
            actions.clear()
            cancel()
            SenderManager.saveToTable()
            ReceiverManager.saveToTable()
            println("Program was closed!")
            exitProcess(0)
        }
    }

    fun every(period: Long, times: Int = -1, action: Runnable.() -> Unit) {
        actions.add(Runnable(-1, period, times, action))
    }

    fun after(millis: Long, action: Runnable.() -> Unit) {
        actions.add(Runnable(millis,-1,1, action))
    }

    fun atHour(hour: Int, action: Runnable.() -> Unit) {
        actions.add(Runnable(getTimeDifference(hour),-1,1,action))
    }
}



