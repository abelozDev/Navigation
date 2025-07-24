package ru.maplyb.navigation.gui.impl.data.entity

import androidx.room.Embedded
import androidx.room.Relation

internal data class StatisticWithPoints(
    @Embedded val statistic: StatisticEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "statisticId"
    )
    val pauses: List<PauseEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "statisticId"
    )
    val points: List<RoutePointEntity>
)
