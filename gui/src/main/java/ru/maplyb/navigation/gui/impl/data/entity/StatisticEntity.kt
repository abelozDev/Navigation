package ru.maplyb.navigation.gui.impl.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.impl.domain.model.StatisticLifecycle
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel

internal typealias Meters = Int

internal typealias KmInHour = Double

/**
 * @param startTime начало пути
 * @param totalDistance пройдено всего
 * @param leftToDo пройдено
 * @param lastPosition последняя известная позиция
 * @param endPoint конечная точка
 * @param averageSpeed средняя скорость
 * @param currentSpeed текущая скорость
 * @param lifecycle жизненный цикл
 * */
@Entity
internal data class StatisticEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val startTime: Long,
    val totalDistance: Meters = 0,
    val leftToDo: Meters = 0,
    val lastPosition: GeoPoint?,
    val endPoint: GeoPoint,
    val averageSpeed: KmInHour = 0.0,
    val currentSpeed: KmInHour = 0.0,
    val lifecycle: StatisticLifecycle = StatisticLifecycle.CREATED
) {
    fun toModel(): StatisticModel = StatisticModel(
        id = id,
        startTime = startTime,
        totalDistance = totalDistance,
        leftToDo = leftToDo,
        lastPosition = lastPosition?.let { lastPosition.latitude to lastPosition.longitude },
        endPoint = endPoint.latitude to endPoint.longitude,
        averageSpeed = averageSpeed,
        currentSpeed = currentSpeed,
        lifecycle = lifecycle
    )
}

internal fun StatisticModel.toEntity(): StatisticEntity = StatisticEntity(
    id = id,
    startTime = startTime,
    totalDistance = totalDistance,
    leftToDo = leftToDo,
    lastPosition = lastPosition?.let {
        GeoPoint(
            lastPosition.first,
            lastPosition.second,
            0.0
        )
    },
    endPoint = GeoPoint(
        endPoint.first,
        endPoint.second,
        0.0
    ),
    averageSpeed = averageSpeed,
    currentSpeed = currentSpeed,
    lifecycle = lifecycle
)

