package ru.maplyb.navigation.gui.impl.util

import ru.maplyb.navigation.gui.api.model.GeoPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

internal fun calculateAzimuth(
    from: GeoPoint,
    to: GeoPoint,
): Int {
    val fromLatRad = Math.toRadians(from.latitude)
    val toLatRad = Math.toRadians(to.latitude)
    val deltaLonRad = Math.toRadians(to.longitude - from.longitude)

    val y = sin(deltaLonRad) * cos(toLatRad)
    val x = cos(fromLatRad) * sin(toLatRad) -
            sin(fromLatRad) * cos(toLatRad) * cos(deltaLonRad)

    val angleRad = atan2(y, x)
    val angleDeg = Math.toDegrees(angleRad)

    return ((angleDeg + 360) % 360).roundToInt() // нормализация к [0, 360)
}
