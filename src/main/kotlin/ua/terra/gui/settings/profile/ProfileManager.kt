package ua.terra.gui.settings.profile

import ua.terra.file.readStringFromFile
import ua.terra.file.writeStringToFile
import ua.terra.gui.console.Logger
import ua.terra.gui.settings.DatabaseSection.baseField
import ua.terra.gui.settings.DatabaseSection.hostField
import ua.terra.gui.settings.DatabaseSection.passwordField
import ua.terra.gui.settings.DatabaseSection.portField
import ua.terra.gui.settings.DatabaseSection.userField
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.stage.FileChooser
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ua.terra.thread.replaceColor
import tornadofx.*
import ua.terra.application.Instance
import java.io.File

object ProfileManager {

    val Extension = "profile"

    val buttonLoad = Button().apply {
        alignment = Pos.BOTTOM_RIGHT
        imageview {
            addClass("img")
            image = Image("data/textures/download.png").replaceColor(255,255,255)
            fitHeight = 20.0
            fitWidth = 20.0
        }

        action {
            val files = chooseFile("Choose File", initialDirectory = Instance.ProfileSRC, mode = FileChooserMode.Single, filters = arrayOf(FileChooser.ExtensionFilter("Profile files", "*.$Extension")))
            if (files.isNotEmpty()) {
                val file = files[0]
                setDataFromProfile(readStringFromFile(file))
                Logger.print("Profile loaded from file: ${file.path}")
            }
        }
    }

    val buttonSave = Button().apply {
        alignment = Pos.BOTTOM_RIGHT
        imageview {
            addClass("img")
            image = Image("data/textures/save.png").replaceColor(255,255,255)
            fitHeight = 20.0
            fitWidth = 20.0
        }

        action {
            val initialFile = chooseFile("Save File", initialDirectory = Instance.ProfileSRC, mode = FileChooserMode.Save, filters = arrayOf(FileChooser.ExtensionFilter("Profile files", "*.$Extension")))
            if (initialFile.isNotEmpty()) {
                val selectedFile = initialFile[0]
                val fileToSave = if (selectedFile.extension == Extension) selectedFile else File(selectedFile.absolutePath)
                writeStringToFile(fileToSave, getDataForProfile())
                saveToFile(fileToSave)
                Logger.print("Profile saved to file: ${fileToSave.path}")
            }
        }
    }

    private fun saveToFile(file: File) {
        file.createNewFile()
    }

    fun getDataForProfile(): String {
        val data = mutableMapOf<String, String>()
        data["host"] = hostField.text
        data["port"] = portField.text
        data["user"] = userField.text
        data["pass"] = passwordField.text
        data["base"] = baseField.text
        return Json.encodeToString(data)
    }

    fun setDataFromProfile(data: String) {
        val map = Json.decodeFromString<Map<String, String>>(data)
        hostField.text = map["host"] ?: ""
        portField.text = map["port"] ?: ""
        userField.text = map["user"] ?: ""
        passwordField.text = map["pass"] ?: ""
        baseField.text = map["base"] ?: ""
    }
}