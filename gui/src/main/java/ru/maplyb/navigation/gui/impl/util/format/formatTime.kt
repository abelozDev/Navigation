package ru.maplyb.navigation.gui.impl.util.format

import java.util.Locale

internal fun formatTime(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60

    return String.format(Locale.US, "%02d:%02d:%02d", h, m, s)
}

internal fun formatMillisecondsTime(milliseconds: Long): String {
    return formatTime(milliseconds / 1000)
}