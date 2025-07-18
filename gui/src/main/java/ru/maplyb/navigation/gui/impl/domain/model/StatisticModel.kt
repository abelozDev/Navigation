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
    val averageSpeed: KmInHour,
    val startPosition: GeoPoint?,
    val currentSpeed: KmInHour,
    val lifecycle: StatisticLifecycle = StatisticLifecycle.CREATED
)


internal enum class StatisticLifecycle {
    CREATED,
    PAUSED,
    IN_PROGRESS,
    END;
}

