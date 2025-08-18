package ru.maplyb.navigation.gui.impl.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.maplyb.navigation.gui.api.model.GeoPoint

@Entity
internal data class RemoteRouteEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Long = 0,
	val createdAt: Long,
	val startPoint: GeoPoint,
	val endPoint: GeoPoint,
	val distanceMeters: Int,
	val durationSeconds: Double
)

@Entity(
	foreignKeys = [
		ForeignKey(
			entity = RemoteRouteEntity::class,
			parentColumns = ["id"],
			childColumns = ["routeId"],
			onDelete = ForeignKey.CASCADE
		)
	]
)
internal data class RemoteRoutePointEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Long = 0,
	val routeId: Long,
	val point: GeoPoint,
	val orderIndex: Int
) 