package ru.maplyb.navigation.gui.impl.util

import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.impl.data.entity.Meters
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

internal fun distanceInMeters(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Meters {
    val RInMeters = 6371000.0 // Радиус Земли в метрах

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2.0)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return (RInMeters * c).roundToInt()
}
internal fun distanceInMeters(
    geoPoint1: GeoPoint,
    geoPoint2: GeoPoint
): Meters {
    val RInMeters = 6371000.0 // Радиус Земли в метрах

    val dLat = Math.toRadians(geoPoint2.latitude - geoPoint1.latitude)
    val dLon = Math.toRadians(geoPoint2.longitude - geoPoint1.longitude)

    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(geoPoint1.latitude)) * cos(Math.toRadians(geoPoint2.latitude)) *
            sin(dLon / 2).pow(2.0)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return (RInMeters * c).roundToInt()
}