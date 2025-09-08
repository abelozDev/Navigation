package ru.maplyb.navigation.gui.impl.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import ru.maplyb.navigation.gui.impl.data.local.entity.RemoteRouteEntity
import ru.maplyb.navigation.gui.impl.data.local.entity.RemoteRoutePointEntity

@Dao
internal interface RemoteRouteDao {
	@Insert
	suspend fun insertRoute(route: RemoteRouteEntity): Long

	@Insert
	suspend fun insertRoutePoints(points: List<RemoteRoutePointEntity>)

	@Query("SELECT * FROM RemoteRouteEntity WHERE id = :routeId")
	suspend fun getRoute(routeId: Long): RemoteRouteEntity?

	@Query("SELECT * FROM RemoteRoutePointEntity WHERE routeId = :routeId ORDER BY orderIndex ASC")
	suspend fun getRoutePoints(routeId: Long): List<RemoteRoutePointEntity>

	@Query("DELETE FROM RemoteRoutePointEntity")
	suspend fun clearPoints()

	@Query("DELETE FROM RemoteRouteEntity")
	suspend fun clearRoutes()

	@Query("SELECT id FROM RemoteRouteEntity ORDER BY createdAt DESC LIMIT 1")
	suspend fun getLatestRouteId(): Long?

	@Transaction
	suspend fun insertRouteWithPoints(route: RemoteRouteEntity, points: List<RemoteRoutePointEntity>): Long {
		val id = insertRoute(route)
		if (points.isNotEmpty()) {
			insertRoutePoints(points.map { it.copy(routeId = id) })
		}
		return id
	}

	@Transaction
	suspend fun replaceAllWithRoute(route: RemoteRouteEntity, points: List<RemoteRoutePointEntity>): Long {
		clearPoints()
		clearRoutes()
		return insertRouteWithPoints(route, points)
	}
} 