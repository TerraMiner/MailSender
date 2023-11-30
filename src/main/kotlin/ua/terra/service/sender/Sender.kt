package ua.terra.service.sender

import ua.terra.application.Instance
import ua.terra.application.Updater
import ua.terra.formatTime
import ua.terra.gui.console.Logger
import ua.terra.service.Service
import ua.terra.service.receiver.Receiver
import ua.terra.thread.async
import java.io.File
import java.nio.file.Files
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

data class Sender(val userName: String, val password: String) {

    var sends = 500

    var timeStamp = 0L

    var lastSendTime = 0L

    var sendedTo = mutableListOf<Pair<Long, Receiver>>()

    var session: Session? = null

    var transport: Transport? = null

    val loggedIn get() = loginStatus === LoginStatus.LOGGED_IN

    var loginStatus = LoginStatus.LOGGED_OUT

    val accessible get() = sends >= 0 && loggedIn

    var workState = WorkState.IDLING

    fun login() {
        async {
            Logger.print("$userName account authorization has started!")

            if (loginStatus == LoginStatus.WRONG_DATA) {
                Logger.print("Can not authorize for $userName!")
                return@async
            }

            val startLogInTime = System.currentTimeMillis()
            Updater.every(1000) {
                if (loginStatus != LoginStatus.LOGGED_OUT) {
                    cancel()
                    return@every
                }
                if (startLogInTime + 120 * 1000 > System.currentTimeMillis()) return@every
                loginStatus = LoginStatus.TIMED_OUT
                Logger.print("Skipping $userName authorization process, because it takes a long time! >120 seconds")
                cancel()
            }

            checkLocks()

            if (timeStamp > System.currentTimeMillis()) {
                Logger.print("$userName has reached limit for today!")
                Logger.print("Can be used after ${formatTime(timeStamp)}")
                loginStatus = LoginStatus.REACHED_LIMIT
                return@async
            }

            if (transport == null || loginStatus != LoginStatus.LOGGED_IN) {
                runCatching {
                    val newSession = Session.getInstance(Instance.SmtpProps, object : Authenticator() {
                        override fun getPasswordAuthentication(): PasswordAuthentication {
                            return PasswordAuthentication(userName, password)
                        }
                    })

                    runCatching {
                        transport = newSession.getTransport("smtp").apply {
                            connect(userName, password)
                        }
                    }.getOrElse {
                        loginStatus = LoginStatus.WRONG_DATA
                        Logger.print("Can not authorize for $userName!")
                        Logger.print(it.message)
                        return@async
                    }

                    session = newSession
                    loginStatus = LoginStatus.LOGGED_IN
                    Logger.print("$userName finished authorization!")

                }.getOrElse {
                    loginStatus = LoginStatus.UNEXCEPTED_ERROR
                    Logger.printStackTrace(it)
                    return@async
                }
            }
        }.thenAccept {

        }
    }

    fun checkLocks() {
        if (timeStamp != 0L && timeStamp < System.currentTimeMillis()) {
            reset()
            return
        }

        if (lastSendTime + 24 * 60 * 60 * 1000 < System.currentTimeMillis()) {
            reset()
            return
        }

        sendedTo.asSequence().forEach {
            if (it.first > System.currentTimeMillis()) return@forEach
            sendedTo.remove(it)
            sends++
        }
    }

    fun reset() {
        timeStamp = 0L
        sends = 500
        sendedTo.clear()
        lastSendTime = 0L
    }

    fun send(targets: List<Receiver>, theme: String?, file: File): List<Receiver>? {
        if (sends <= 0) {
            Logger.print("$userName reached limit for day")
            return targets
        }

        if (transport === null) {
            Logger.print("$userName transport is null, cannot sent message!")
            return targets
        }

        workState = WorkState.WORKING

        val (clonedTargets, reachedLimit) = filterTargets(targets)
        val trashedTargets = mutableListOf(*reachedLimit.toTypedArray())

        if (clonedTargets.size > sends) {
            val removedTargets = clonedTargets.subList(sends, clonedTargets.size)
            trashedTargets.addAll(removedTargets)
            clonedTargets.removeAll(removedTargets)
        }

        Logger.print("Sending message for ${clonedTargets.size} users!")

        clonedTargets.forEach {
            it.waitDataFrom = this
        }

        runCatching {

            val message = MimeMessage(session).also { message ->
                message.setFrom(InternetAddress(userName))

                message.addRecipients(
                    Message.RecipientType.TO,
                    clonedTargets.map { InternetAddress(it.userName) }.toTypedArray()
                )

                message.setSubject(theme, "UTF-8")

                MimeMultipart().also { multipart ->
                    MimeBodyPart().also { htmlPart ->
                        htmlPart.setContent(String(Files.readAllBytes(file.toPath())), "text/html; charset=utf-8")
                        multipart.addBodyPart(htmlPart)
                    }
                    message.setContent(multipart)
                }

                message.saveChanges()
            }

            transport!!.sendMessage(message, message.getAllRecipients())
        }.getOrElse {
            Logger.printStackTrace(it)
            return targets
        }

        clonedTargets.forEach {
            sendedTo.add(System.currentTimeMillis() + (24 * 60 * 60 * 1000) to it)
        }

        sends -= clonedTargets.size
        Service.sended += clonedTargets.size
        Logger.print("Finished sending messages!")

        workState = WorkState.FINISHED

        if (sends <= 0) {
            timeStamp = System.currentTimeMillis() + 24 * 60 * 60 * 1000
            loginStatus = LoginStatus.REACHED_LIMIT
        }

        lastSendTime = System.currentTimeMillis()

        clonedTargets.forEach {
            it.waitDataFrom = null
        }

        return if (trashedTargets.isEmpty()) null else trashedTargets
    }

    private fun filterTargets(targets: List<Receiver>): Pair<MutableList<Receiver>, MutableList<Receiver>> {
        val clonedTargets = mutableListOf(*targets.toTypedArray())
        val trashed = mutableListOf<Receiver>()
        targets.forEach {
            if (!canSendTo(it)) {
                clonedTargets.remove(it)
                trashed.add(it)
                Logger.print("A message cannot be sent to the specified address $it, because the sender has reached the limit.")
            }
        }
        return clonedTargets to trashed
    }

    fun canSendTo(receiver: Receiver) =
        (sendedTo.count { it.second == receiver }) < 100 || receiver.userName != userName

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sender

        if (userName != other.userName) return false
        if (password != other.password) return false
        if (sends != other.sends) return false
        if (timeStamp != other.timeStamp) return false
        if (sendedTo != other.sendedTo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userName.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + sends
        result = 31 * result + timeStamp.hashCode()
        result = 31 * result + sendedTo.hashCode()
        return result
    }


}
