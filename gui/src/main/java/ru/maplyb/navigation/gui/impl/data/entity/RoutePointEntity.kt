package ru.maplyb.navigation.gui.impl.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.maplyb.navigation.gui.api.model.GeoPoint

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = StatisticEntity::class,
            parentColumns = ["id"],
            childColumns = ["statisticId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal data class RoutePointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val statisticId: Int,
    val point: GeoPoint,
    val timestamp: Long
)
