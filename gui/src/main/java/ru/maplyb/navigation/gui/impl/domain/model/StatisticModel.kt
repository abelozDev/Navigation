package ru.maplyb.navigation.gui.impl.domain.model

import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.impl.data.entity.KmInHour
import ru.maplyb.navigation.gui.impl.data.entity.Meters

/**
 * @param startTime начало пути
 * @param totalDistance осталось пройти
 * @param leftToDo пройдено
 * @param lastPosition последняя известная позиция
 * @param endPoint конечная точка
 * @param travelTime время в пути
 * @param averageSpeed средняя скорость
 * @param startPosition начальная позиция
 * @param currentSpeed текущая скорость
 * @param lifecycle жизненный цикл
 * */

internal data class StatisticModel(
    val id: Int,
    val startTime: Long,
    val totalDistance: Meters,
    val leftToDo: Meters = 0,
    val lastPosition: GeoPoint?,
    val endPoint: GeoPoint,
    val travelTime: Long,
    val averageSpeed: KmInHour,
    val startPosition: GeoPoint?,
    val currentSpeed: KmInHour,
    val lifecycle: StatisticLifecycle = StatisticLifecycle.CREATED
) {
    companion object {
        fun default(): StatisticModel = StatisticModel(
            id = 0,
            startTime = 0,
            totalDistance = 0,
            lastPosition = null,
            endPoint = GeoPoint(0.0,0.0,0.0),
            travelTime = 0,
            averageSpeed = 0.0,
            startPosition = null,
            currentSpeed = 0.0
        )
    }
}


internal enum class StatisticLifecycle {
    CREATED,
    PAUSED,
    IN_PROGRESS,
    END;
}

