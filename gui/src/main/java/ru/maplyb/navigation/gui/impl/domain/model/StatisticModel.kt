package ru.maplyb.navigation.gui.impl.domain.model

import ru.maplyb.navigation.gui.impl.data.entity.KmInHour
import ru.maplyb.navigation.gui.impl.data.entity.Meters

internal data class StatisticModel(
    val id: Int,
    val startTime: Long,
    val totalDistance: Meters,
    val averageSpeed: KmInHour,
    val currentSpeed: KmInHour,
)

