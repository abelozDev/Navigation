package ru.maplyb.navigation.gui.api.model

import ru.maplyb.navigation.gui.impl.data.local.entity.Meters

/**
 * @param totalDistance - осталось пройти
 * @param endPoint - конечная точка
 * @param lastPosition последняя известная позиция
 * */
public data class RouteStatistic(
    val totalDistance: Meters,
    val endPoint: GeoPoint,
    val lastPosition: GeoPoint?,
)
