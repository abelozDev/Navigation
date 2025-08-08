package ru.maplyb.navigation.gui.impl.data.use_cases

import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.impl.data.local.entity.RoutePointEntity
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

internal fun calculateSpeedKph(points: List<RoutePointEntity>): Double {
    val distanceMeters = calculateTotalDistance(points.map { it.point })
    val timeSeconds = calculateTotalTimeSeconds(points.map { it.timestamp })

    if (timeSeconds == 0.0) return 0.0

    val speedMps = distanceMeters / timeSeconds
    return speedMps * 3.6 // перевод в км/ч
}

internal fun calculateTotalTimeSeconds(points: List<Long>): Double {
    if (points.size < 2) return 0.0
    return (points.last() - points.first()) / 1000.0
}
private fun calculateTotalDistance(points: List<GeoPoint>): Double {
    var distance = 0.0
    for (i in 1 until points.size) {
        val p1 = points[i - 1]
        val p2 = points[i]
        distance += haversine(p1.latitude, p1.longitude, p2.latitude, p2.longitude)
    }
    return distance // в метрах
}

/**
 * Вычисляет расстояние между двумя координатами в метрах
 */
private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371000.0 // радиус Земли в метрах
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2.0)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}