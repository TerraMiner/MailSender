package ua.terra.file

import ua.terra.gui.console.Logger
import java.io.*

fun writeStringToFile(file: File, content: String) {
    runCatching {
        if (!file.exists()) {
            file.createNewFile()
        }

        FileWriter(file).use { fileWriter ->
            BufferedWriter(fileWriter).use { bufferedWriter ->
                bufferedWriter.write(content)
            }
        }

        Logger.print("Data successfully written to file: ${file.path}")
    }.getOrElse {
        Logger.print("Error while trying write data to file!")
        Logger.printStackTrace(it)
    }
}

fun readStringFromFile(file: File): String {
    return runCatching {
        if (!file.exists()) {
            Logger.print("File not exists: ${file.path}")
            return ""
        }

        val stringBuilder = StringBuilder()
        var line: String?

        FileReader(file).use { fileReader ->
            BufferedReader(fileReader).use { bufferedReader ->
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append("\n")
                }
            }
        }

        stringBuilder.toString()
    }.getOrElse {
        Logger.print("Error while reading file!")
        Logger.printStackTrace(it)
        ""
    }
}