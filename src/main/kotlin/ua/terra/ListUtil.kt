package ua.terra

import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.math.max

fun <T> divideList(originalList: List<T>, numChunks: Int): List<List<T>> {
    if (originalList.isEmpty()) {
        return listOf(listOf())
    }
    val chunkSize = max(1.0, (originalList.size / numChunks).toDouble()).toInt()
    return IntStream.range(0, originalList.size)
        .boxed()
        .collect(
            Collectors.groupingBy { index: Int -> index / chunkSize }
        )
        .values
        .stream()
        .map { indices: List<Int> ->
            indices.stream().map { index: Int? -> originalList[index!!] }
                .collect(Collectors.toList())
        }
        .collect(Collectors.toList())
}

fun formatTime(millis: Long, format: String ="dd:MM:yy HH:mm"): String {
    val sdf = SimpleDateFormat(format)
    val date = Date(millis)
    return sdf.format(date)
}

fun getTimeOfHour(desiredHour: Int): Long {
    val now = System.currentTimeMillis()
    val currentHour = LocalDateTime.ofInstant(Instant.ofEpochMilli(now), ZoneId.systemDefault()).hour

    val targetDate = if (currentHour >= desiredHour) LocalDateTime.now().plusDays(1) else LocalDateTime.now()

    val targetTime = targetDate.withHour(desiredHour).withMinute(0).withSecond(0).withNano(0)

    return targetTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun getTimeDifference(desiredHour: Int): Long {
    val now = System.currentTimeMillis()
    val currentDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(now), ZoneId.systemDefault())
    val currentHour = currentDateTime.hour

    val targetDate = if (currentHour >= desiredHour) currentDateTime.plusDays(1) else currentDateTime

    val targetDateTime = targetDate.withHour(desiredHour).withMinute(0).withSecond(0).withNano(0)

    val duration = Duration.between(currentDateTime, targetDateTime)

    return duration.toMillis()
}