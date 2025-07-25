package ru.maplyb.navigation.gui.impl.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.impl.domain.model.StatisticLifecycle
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel
import ru.maplyb.navigation.gui.impl.util.distanceInMeters
import java.util.Locale

internal typealias Meters = Int

internal typealias KmInHour = Double

/**
 * @param startTime начало пути
 * @param startPosition координата начала пути
 * @param leftToDo пройдено
 * @param lastPosition последняя известная позиция
 * @param lastPointTimestamp время последней известной позиции
 * @param endPoint конечная точка
 * @param currentSpeed текущая скорость
 * @param lifecycle жизненный цикл
 * */
@Entity
internal data class StatisticEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val startTime: Long,
    val startPosition: GeoPoint?,
    val leftToDo: Meters = 0,
    val lastPosition: GeoPoint?,
    val lastPointTimestamp: Long? = null,
    val endPoint: GeoPoint,
    val currentSpeed: KmInHour = 0.0,
    val lifecycle: StatisticLifecycle = StatisticLifecycle.CREATED
) {
    fun toModel(travelTime: Long): StatisticModel {

        val hours = travelTime.toDouble() / (1000 * 60 * 60)
        val averageSpeed = String.format(Locale.US, "%.1f", (leftToDo / 1000.0) / hours).toDouble()

        val totalDistance = lastPosition?.let { distanceInMeters(it, endPoint) } ?: 0

        return StatisticModel(
            id = id,
            startTime = startTime,
            totalDistance = totalDistance,
            leftToDo = leftToDo,
            lastPosition = lastPosition,
            endPoint = endPoint,
            averageSpeed = averageSpeed,
            currentSpeed = currentSpeed,
            lifecycle = lifecycle,
            startPosition = startPosition,
            travelTime = travelTime
        )
    }

    companion object {
        /*М*/
        const val MIN_DISTANCE = 5
        /*Км/Ч*/
        const val MAX_PAUSE_SPEED = 1.8f

    }
}

internal fun StatisticModel.toEntity(): StatisticEntity = StatisticEntity(
    id = id,
    startTime = startTime,
    leftToDo = leftToDo,
    lastPosition = lastPosition,
    endPoint = endPoint,
    currentSpeed = currentSpeed,
    lifecycle = lifecycle,
    startPosition = startPosition
)

