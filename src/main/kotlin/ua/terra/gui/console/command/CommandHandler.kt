package ua.terra.gui.console.command

import ua.terra.gui.console.Logger

object CommandHandler {
    val CommandPrefix = "/"

    fun handleCommand(commandLabel: String) {
        if (!commandLabel.startsWith(CommandPrefix)) {
            Logger.print("Command might starts with \"$CommandPrefix\"")
            return
        }

        val args = commandLabel.split(" ")

        val label = args.firstOrNull() ?: run {
            Logger.print("Command not found!")
            return
        }

        when (label) {
            CommandType.APP.label -> {
                handleAppCommand(args.drop(1))
            }

            CommandType.SQL.label -> {
                handleSqlCommand(args.drop(1))
            }
        }
    }

    private fun handleAppCommand(args: List<String>) {

    }

    private fun handleSqlCommand(args: List<String>) {

    }
}