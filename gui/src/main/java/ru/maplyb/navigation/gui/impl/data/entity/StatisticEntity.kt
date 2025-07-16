package ru.maplyb.navigation.gui.impl.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.impl.domain.model.StatisticLifecycle
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel
import ru.maplyb.navigation.gui.impl.util.distanceInMeters

internal typealias Meters = Int

internal typealias KmInHour = Double

/**
 * @param startTime начало пути
 * @param startPosition координата начала пути
 * @param leftToDo пройдено
 * @param lastPosition последняя известная позиция
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
    val endPoint: GeoPoint,
    val currentSpeed: KmInHour = 0.0,
    val lifecycle: StatisticLifecycle = StatisticLifecycle.CREATED
) {
    fun toModel(): StatisticModel {
        val currentTime = System.currentTimeMillis()

        val durationMillis = currentTime - startTime
        val hours = durationMillis.toDouble() / (1000 * 60 * 60)
        val averageSpeed = String.format("%.1f", (leftToDo / 1000) / hours).toDouble()

        val totalDistance = lastPosition?.let { distanceInMeters(it, endPoint) } ?: 0

        return StatisticModel(
            id = id,
            startTime = startTime,
            totalDistance = totalDistance,
            leftToDo = leftToDo,
            lastPosition = lastPosition?.let { lastPosition.latitude to lastPosition.longitude },
            endPoint = endPoint.latitude to endPoint.longitude,
            averageSpeed = averageSpeed,
            currentSpeed = currentSpeed,
            lifecycle = lifecycle,
            startPosition = startPosition?.let { startPosition.latitude to startPosition.longitude }
        )
    }
}

internal fun StatisticModel.toEntity(): StatisticEntity = StatisticEntity(
    id = id,
    startTime = startTime,
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
    currentSpeed = currentSpeed,
    lifecycle = lifecycle,
    startPosition = startPosition?.let {
        GeoPoint(
            startPosition.first,
            startPosition.second,
            0.0
        )
    }
)

