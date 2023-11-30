package ua.terra.gui.console

import javafx.scene.control.TextArea
import javafx.scene.layout.Priority
import ua.terra.thread.appSync
import tornadofx.vgrow
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object Logger : TextArea() {
    private val maxLines = 1024

    init {
        isWrapText = true
        isEditable = false
        prefRowCount = 10
        maxHeight = Double.MAX_VALUE
        vgrow = Priority.ALWAYS
    }

    fun print(text: Any?) {
        appSync {
            val formattedTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            val message = if (getText().isBlank()) "[$formattedTime]: $text" else "\n[$formattedTime]: $text"

            val newText = "${this.text.trim()}$message"
            val lines = newText.split("\n")

            if (lines.size > maxLines) {
                val trimmedText = lines.subList(lines.size - maxLines, lines.size).joinToString("\n")
                this.text = trimmedText
            } else {
                this.text = newText
            }

            scrollTop = Double.MAX_VALUE
        }
    }

    fun printStackTrace(e: Throwable) {
        appSync {
            try {
                StringWriter().use { sw ->
                    PrintWriter(sw).use { pw ->
                        e.printStackTrace(pw)
                        val stackTrace = sw.toString()
                        print(stackTrace)
                    }
                }
            } catch (ignored: IOException) {
            }
        }
    }
}
