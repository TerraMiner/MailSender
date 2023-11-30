package ua.terra

import ua.terra.application.Instance
import ua.terra.application.Settings
import ua.terra.gui.console.Logger
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ua.terra.service.receiver.Receiver
import ua.terra.service.sender.Sender
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


class DatabaseConnection
constructor(
    host: String?,
    port: String?,
    user: String?,
    password: String?,
    base: String?,
    callback: Connection.() -> Unit
) {
    val connection: Connection

    companion object {
        fun of(
            host: String?,
            port: String?,
            user: String?,
            password: String?,
            base: String?,
            callback: Connection.() -> Unit = { }
        ) = DatabaseConnection(host, port, user, password, base, callback)

        fun isConnected(): Boolean {
            val statement: Boolean = hasConnection()
            if (!statement) {
                Logger.print("Not connected!")
            }
            return !statement
        }

        fun hasConnection(): Boolean {
            return try {
                Instance.Connection != null && !Instance.Connection!!.isClosed
            } catch (e: SQLException) {
                Logger.printStackTrace(e)
                false
            }
        }
    }

    init {
        val address: String = "jdbc:mysql://%s:%s/%s".format(host, port, base)
        connection = DriverManager.getConnection(address, user, password)
        callback(connection)
    }

    fun close() {
        connection.close()
    }


    val isClosed: Boolean get() = connection.isClosed

    val tables: List<String>
        get() {
            connection.prepareStatement("SHOW TABLES;").use { statement ->
                statement.executeQuery().use { resultSet ->
                    val tableList = mutableListOf<String>()
                    while (resultSet.next()) {
                        val tableName = resultSet.getString(1)
                        tableList.add(tableName)
                    }
                    return tableList
                }
            }
        }

    val receivers: List<Receiver>
        get() {
            val selectedTable = Settings.receiversTable
            if (selectedTable == "") return emptyList()
            connection.prepareStatement("SELECT * FROM $selectedTable;")?.use { statement ->
                val list: MutableList<Receiver> = ArrayList()
                runCatching {
                    statement.executeQuery().use { resultSet ->
                        while (resultSet.next()) {
                            val value: String = resultSet.getString("MAIL")
                            list.add(Receiver(value))
                        }
                    }
                }.getOrElse {
                    Logger.printStackTrace(it)
                }
                return list
            } ?: return emptyList()
        }

    val senders: List<Sender>
        get() {
            val selectedTable = Settings.sendersTable
            if (selectedTable == "") return emptyList()
            connection.prepareStatement("SELECT * FROM $selectedTable;")?.use { statement ->
                val list: MutableList<Sender> = ArrayList()
                runCatching {
                    statement.executeQuery().use { resultSet ->
                        while (resultSet.next()) {
                            val user: String = resultSet.getString("MAIL")
                            val password: String = resultSet.getString("PASSWORD")
                            val sends: Int = resultSet.getInt("SENDS")
                            val timeStamp: Long = resultSet.getLong("TIMESTAMP")
                            val lastSendTime: Long = resultSet.getLong("LASTSEND")
                            val sendedTo =
                                Json.decodeFromString<Map<Long, String>>(resultSet.getString("RECEIVERS_STAMPS"))
                            val sender = Sender(user, password).apply {
                                this.sends = sends
                                this.timeStamp = timeStamp
                                this.lastSendTime = lastSendTime
                                this.sendedTo = sendedTo.map { it.key to Receiver(it.value) }.toMutableList()
                            }
                            list.add(sender)
                        }
                    }
                }.getOrElse {
                    Logger.printStackTrace(it)
                }
                return list
            } ?: return emptyList()
        }

    fun saveSenders(senders: List<Sender>) {
        val selectedTable = Settings.sendersTable
        connection.prepareStatement(
            "INSERT INTO $selectedTable (MAIL, PASSWORD, SENDS, TIMESTAMP, LASTSEND, RECEIVERS_STAMPS) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE `PASSWORD` = ?, `SENDS` = ?, `TIMESTAMP` = ?, `LASTSEND` = ?, `RECEIVERS_STAMPS` = ?;"
        )
            ?.use { statement ->
                for (sender in senders) {
                    Logger.print("Saving ${sender.userName} with ${sender.sends}")
                    val password = sender.password
                    val sends = sender.sends
                    val timeStamp = sender.timeStamp
                    val lastSendTime = sender.lastSendTime
                    val receiversStamps = Json.encodeToString(sender.sendedTo.associate { it.first to it.second.userName })
                    statement.setString(1, sender.userName)
                    statement.setString(2, password)
                    statement.setInt(3, sends)
                    statement.setLong(4, timeStamp)
                    statement.setLong(5, lastSendTime)
                    statement.setString(6, receiversStamps)
                    statement.setString(7, password)
                    statement.setInt(8, sends)
                    statement.setLong(9, timeStamp)
                    statement.setLong(10, lastSendTime)
                    statement.setString(11, receiversStamps)
                    statement.executeUpdate()
                }
            }
    }

    fun saveReceivers(receivers: List<Receiver>) {
        val selectedTable = Settings.receiversTable
        connection.prepareStatement("INSERT INTO $selectedTable (MAIL) VALUES (?) ON DUPLICATE KEY UPDATE MAIL = VALUES(MAIL);")
            ?.use { statement ->
                for (receiver in receivers) {
                    statement.setString(1, receiver.userName)
                    statement.executeUpdate()
                }
            }
    }

    fun removeSender(sender: String) {
        val selectedTable = Settings.sendersTable
        connection.prepareStatement("DELETE FROM $selectedTable WHERE MAIL = ?;")?.use { statement ->
            statement.setString(1, sender)
            statement.executeUpdate()
        }
    }

    fun removeReceiver(receiver: String) {
        val selectedTable = Settings.receiversTable
        connection.prepareStatement("DELETE FROM $selectedTable WHERE MAIL = ?;")?.use { statement ->
            statement.setString(1, receiver)
            statement.executeUpdate()
        }
    }
}