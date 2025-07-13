package ru.maplyb.navigation.gui.impl.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel

internal typealias Meters = Int

internal typealias KmInHour = Double

/**
 * @param startTime начало пути
 * @param totalDistance пройдено всего
 * */
@Entity
internal data class StatisticEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val startTime: Long,
    val totalDistance: Meters = 0,
    val averageSpeed: KmInHour = 0.0,
    val currentSpeed: KmInHour = 0.0,
) {
    fun toModel(): StatisticModel = StatisticModel(id, startTime, totalDistance, averageSpeed, currentSpeed)
}

internal fun StatisticModel.toEntity(): StatisticEntity = StatisticEntity(id, startTime, totalDistance, averageSpeed, currentSpeed)

