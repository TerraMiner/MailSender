package ua.terra

object TimeFormatter {
    val timeUnits = arrayOf("yr." to 32140800, "mo." to 2678400, "d." to 86400, "h." to 3600, "m." to 60, "s." to 1)

    fun formatTimeText(time: Long): String {
        if (time < 1000) return "0 s."

        var seconds = time / 1000

        val formattedTimeList = mutableListOf<String>()

        for ((textUnit, timeUnit) in timeUnits) {
            val value = seconds / timeUnit
            seconds %= timeUnit
            if (value > 0) {
                formattedTimeList.add(String.format(" %s %s", value, textUnit))
            }
        }

        return formattedTimeList.joinToString("").trim()
    }
}