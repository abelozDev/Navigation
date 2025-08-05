package ru.maplyb.navigation.gui.impl.util.format

import ru.maplyb.navigation.gui.impl.data.local.entity.Meters
import kotlin.math.roundToInt

internal fun formatDistance(distance: Meters): String {
    val km = distance / 1000
    val meters = ((distance % 1000.0) / 10).roundToInt() * 10
    return buildString {
        if (km > 0 ) append("${km}км ")
        append("${meters}м")
    }
}