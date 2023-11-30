package ua.terra.application

import ua.terra.gui.settings.PropertySection
import ua.terra.gui.settings.ReceiversSection
import ua.terra.gui.settings.SendersSection
import ua.terra.service.receiver.ReceiverManager
import ua.terra.service.sender.SenderManager
import tornadofx.selectedItem
import java.io.File

object Settings {

    var sendersTableWasChanged = false
        get() { return field.apply { field = !field } }

    var receiversTableWasChanged = false
        get() { return field.apply { field = !field } }

    var sendersTable: String = ""
        set(value) {
            sendersTableWasChanged = true
            field = value
        }
    var receiversTable: String = ""
        set(value) {
            receiversTableWasChanged = true
            field = value
        }

    var selectedFilePath = Instance.PromotionSRC.listFiles()?.firstNotNullOfOrNull { it.name } ?: ""
    val sentFile get() = File("${Instance.PromotionSRC.path}/$selectedFilePath")

    var messagesPerMoment = 3
    var theme = "Hello User!"

    var every24h = false
    var atTime = -1

    fun applySendersSection() {
        SenderManager.saveToTable()
        sendersTable = SendersSection.box.selectedItem ?: ""
        SenderManager.loadFromTable()
    }

    fun applyReceiversSection() {
        ReceiverManager.saveToTable()
        receiversTable = ReceiversSection.box.selectedItem ?: ""
        ReceiverManager.loadFromTable()
    }

    fun applySendProperties() {
        selectedFilePath = PropertySection.box.selectedItem ?: ""
        theme = PropertySection.themeField.text ?: ""
        messagesPerMoment = PropertySection.numField.text.toIntOrNull() ?: 1
        every24h = PropertySection.checkbox.isSelected
        atTime = PropertySection.atTime.selectedItem ?: -1
    }
}