package ru.maplyb.navigation.gui.impl.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.maplyb.navigation.gui.impl.data.entity.PauseEntity
import ru.maplyb.navigation.gui.impl.data.entity.RoutePointEntity

@Dao
internal interface RoutePointsDao {

    @Insert()
    suspend fun insert(routePointEntity: RoutePointEntity)

    @Query("SELECT * FROM RoutePointEntity WHERE statisticId = :statisticId ORDER BY timestamp ASC")
    suspend fun getRoutePoints(statisticId: Int): List<RoutePointEntity>

    @Query("SELECT * FROM RoutePointEntity WHERE statisticId = :statisticId ORDER BY timestamp ASC")
    fun getRoutePointsFlow(statisticId: Int): Flow<List<RoutePointEntity>>

    @Query("SELECT * FROM RoutePointEntity WHERE statisticId = :statisticId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastRoutePoint(statisticId: Int): RoutePointEntity?

    @Query("DELETE FROM RoutePointEntity WHERE statisticId = :statisticId")
    suspend fun deleteRoutePoints(statisticId: Int)

    @Query("DELETE FROM RoutePointEntity")
    suspend fun clear()

    @Query("SELECT * FROM RoutePointEntity")
    fun getAllRoutePointsFlow(): Flow<List<RoutePointEntity>>

}