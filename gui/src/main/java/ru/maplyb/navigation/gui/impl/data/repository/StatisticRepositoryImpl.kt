package ru.maplyb.navigation.gui.impl.data.repository

import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.impl.data.database.NavigationDatabase
import ru.maplyb.navigation.gui.impl.data.entity.Meters
import ru.maplyb.navigation.gui.impl.data.entity.PauseEntity
import ru.maplyb.navigation.gui.impl.data.entity.RoutePointEntity
import ru.maplyb.navigation.gui.impl.data.entity.StatisticEntity
import ru.maplyb.navigation.gui.impl.data.entity.toEntity
import ru.maplyb.navigation.gui.impl.data.model.PositionDataModel
import ru.maplyb.navigation.gui.impl.domain.model.StatisticLifecycle
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel
import ru.maplyb.navigation.gui.impl.domain.repository.StatisticRepository
import ru.maplyb.navigation.gui.impl.util.distanceInMeters

internal class StatisticRepositoryImpl(
    private val database: NavigationDatabase
) : StatisticRepository {

    override fun getStatisticsFlow(): Flow<List<StatisticModel>> {
        return database.statisticDao().getAllFlow().map { list ->
            list.map {
                it.toModel()
            }
        }
    }

    override suspend fun pause(statisticId: Int) {
        database.statisticDao().getById(statisticId.toLong())?.let {
            database.statisticDao().updateStatistic(
                it.copy(
                    lifecycle = StatisticLifecycle.PAUSED
                )
            )
        }
    }

    override suspend fun clear() {
        database.statisticDao().clear()
    }

    override fun getStatisticByIdFlow(id: Int): Flow<StatisticModel?> {
        return database.statisticDao().getByIdFlow(id).map { it?.toModel() }
    }

    override suspend fun insertStatistic(statisticModel: StatisticModel) {
        database.statisticDao().insertStatistic(statisticModel.toEntity())
    }

    override fun getCurrentStatistic(): Flow<StatisticModel?> {
        return database.statisticDao().getCurrentStatistic().map { it?.toModel() }
    }

    override suspend fun checkStartRouteIsPossible(): Boolean {
        val statistics = database.statisticDao().getAll()
        return statistics.isEmpty() || statistics.all { it.lifecycle == StatisticLifecycle.END }
    }

    override suspend fun deleteStatistic(statisticModel: StatisticModel) {
        database.statisticDao().deleteStatisticById(statisticModel.id)
    }

    override suspend fun createEmptyStatistic(
        currentPosition: GeoPoint?,
        endPosition: GeoPoint
    ): StatisticModel {
        val statistic = StatisticEntity(
            lastPosition = currentPosition,
            startTime = System.currentTimeMillis(),
            endPoint = endPosition,
            startPosition = currentPosition
        )
        return insertAndGet(statistic).toModel()
    }

    @Transaction
    override suspend fun updateLastPosition(statisticId: Int, geoPoint: GeoPoint) {
        val statistic = database.statisticDao().getById(statisticId.toLong())
        /**Может быть null если удалили статистику, но пришла геолокация*/
        if (statistic == null) return
        val timestamp = System.currentTimeMillis()
        var isPaused = false
        val newStatistic = if (statistic.lastPosition != null) {
            val distanceInMeters = distanceInMeters(
                lat1 = statistic.lastPosition.latitude,
                lon1 = statistic.lastPosition.longitude,
                lat2 = geoPoint.latitude,
                lon2 = geoPoint.longitude
            )
            isPaused = checkPause(statistic, geoPoint, timestamp, distanceInMeters)
            val lifecycle = if (isPaused) StatisticLifecycle.PAUSED else StatisticLifecycle.CREATED
            statistic.copy(
                lifecycle = lifecycle,
                leftToDo = if (isPaused) statistic.leftToDo else statistic.leftToDo + distanceInMeters,
                lastPosition = geoPoint,
                lastPointTimestamp = timestamp
            )
        } else {
            statistic.copy(
                startPosition = statistic.startPosition ?: geoPoint,
                lastPosition = geoPoint,
                lastPointTimestamp = timestamp
            )
        }
        database.statisticDao().updateStatistic(newStatistic)
        if (isPaused) {
            savePausePoint(statisticId, geoPoint, timestamp)
        } else {
            saveRoutePoint(statisticId, geoPoint, timestamp)
        }
    }

    private suspend fun savePausePoint(statisticId: Int, point: GeoPoint, timestamp: Long) {
        val entity = PauseEntity(
            point = point,
            timestamp = timestamp,
            statisticId = statisticId
        )
        database.pauseDao().insertPause(entity)
    }

    private suspend fun saveRoutePoint(statisticId: Int, point: GeoPoint, timestamp: Long) {
        val entity = RoutePointEntity(
            statisticId = statisticId,
            point = point,
            timestamp = timestamp
        )
        database.routePointsDao().insert(entity)
    }

    /**Если паузы еще нет. Нужна логика если уже пауза*/
    private fun checkPause(
        statistic: StatisticEntity,
        geoPoint: GeoPoint,
        timestamp: Long,
        distanceBetween: Meters
    ): Boolean {
        if (statistic.lastPosition == null || statistic.lastPointTimestamp == null) return false
        val timeBetweenPoints = timestamp - statistic.lastPointTimestamp
        val speedKmh = (distanceBetween / (timeBetweenPoints * 0.001)) / 3.6
        return speedKmh < StatisticEntity.MAX_PAUSE_SPEED
    }

    @Transaction
    private suspend fun insertAndGet(statistic: StatisticEntity): StatisticEntity {
        val id = database.statisticDao().insertStatistic(statistic)
        val createdStatistic = database.statisticDao().getById(id)
        check(createdStatistic != null) { "statistic with id = $id is null" }
        return createdStatistic
    }

}