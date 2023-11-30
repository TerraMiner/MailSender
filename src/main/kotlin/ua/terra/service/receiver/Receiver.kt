package ua.terra.service.receiver

import ua.terra.service.sender.Sender

data class Receiver(val userName: String) {
    var waitDataFrom: Sender? = null

    override fun hashCode(): Int {
        return userName.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Receiver

        return userName == other.userName
    }
}
