package ua.terra.application

import ua.terra.DatabaseConnection
import ua.terra.service.sender.Sender
import ua.terra.gui.navigation.ViewType
import javafx.scene.image.Image
import java.io.File
import java.util.*

object Instance {
    var IsClosed = false

    val RefreshSym = "\u21BB"

    val AppIcon = Image("data/textures/icon.png")

    var Connection: DatabaseConnection? = null

    val Sender: Sender? = null

    var CurrentView: ViewType = ViewType.HOME

    val Resource: File get() {
        val file = File("MailSender")
        if (!file.exists()) file.mkdirs()
        return file
    }

    val PromotionSRC: File get() {
        val file = File("${Resource.path}/promotion")
        if (!file.exists()) file.mkdirs()
        return file
    }

    val ProfileSRC: File get() {
        val file = File("${Resource.path}/profile")
        if (!file.exists()) file.mkdirs()
        return file
    }

    val PromotionFiles get() = PromotionSRC.listFiles()?.mapNotNull { it.name } ?: listOf()

    val PrefWidth = 555.0

    val PrefHeight = 710.0

    val SmtpProps = Properties().also { prop ->
        prop.setProperty("mail.smtp.auth","true")
        prop.setProperty("mail.smtp.starttls.enable", "true")
        prop.setProperty("mail.smtp.host", "smtp.gmail.com")
        prop.setProperty("mail.smtp.port","587")
    }
}