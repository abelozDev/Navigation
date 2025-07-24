package ru.maplyb.navigation.gui.impl.util.format

internal fun formatTime(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60

    return buildString {
        if (h > 0) append("${h}ч ")
        if (m > 0 || h > 0) append("${m}мин ")
        append("${s}сек")
    }.trim()
}

internal fun formatMillisecondsTime(milliseconds: Long): String {
    return formatTime(milliseconds / 1000)
}