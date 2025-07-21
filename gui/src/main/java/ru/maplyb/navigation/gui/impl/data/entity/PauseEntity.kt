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
internal data class PauseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val point: GeoPoint,
    val timestamp: Long,
    val statisticId: Int,
)