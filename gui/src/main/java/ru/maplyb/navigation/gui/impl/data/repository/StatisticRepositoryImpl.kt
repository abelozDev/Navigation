package ru.maplyb.navigation.gui.impl.data.repository

import androidx.compose.animation.core.updateTransition
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.impl.data.database.NavigationDatabase
import ru.maplyb.navigation.gui.impl.data.entity.Meters
import ru.maplyb.navigation.gui.impl.data.entity.PauseEntity
import ru.maplyb.navigation.gui.impl.data.entity.RoutePointEntity
import ru.maplyb.navigation.gui.impl.data.entity.StatisticEntity
import ru.maplyb.navigation.gui.impl.data.entity.toEntity
import ru.maplyb.navigation.gui.impl.data.model.PositionDataModel
import ru.maplyb.navigation.gui.impl.data.model.PositionTypes
import ru.maplyb.navigation.gui.impl.domain.model.StatisticLifecycle
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel
import ru.maplyb.navigation.gui.impl.domain.repository.StatisticRepository
import ru.maplyb.navigation.gui.impl.util.distanceInMeters

internal class StatisticRepositoryImpl(
    private val database: NavigationDatabase
) : StatisticRepository {

    /**Тут время в пути не учитывается. Если в дальнейшем надо будет - сделать*/
    override fun getStatisticsFlow(): Flow<List<StatisticModel>> {
        return database.statisticDao().getAllFlow().map { list ->
            list.map {
                it.toModel(
                    0
                )
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
        return combine(
            database.statisticDao().getByIdFlow(id),
            database.pauseDao().getPausesByStatistic(id),
        ) { statistic, pauses ->
            val timestamp = System.currentTimeMillis()
            val travelTime = pauses
                .groupBy { it.pauseNumber }
                .map {
                    val sorted = it.value.sortedBy { value -> value.timestamp }
                    val first = sorted.firstOrNull()?.timestamp ?: 0
                    val last = sorted.lastOrNull()?.timestamp ?: 0
                    println("first: $first")
                    println("last: $last")
                    last - first
                }
                .sum()
                .let { allPausesTime ->
                    if (statistic?.startTime == null) return@let 0
                    timestamp - statistic.startTime - allPausesTime
                }
            statistic?.toModel(travelTime)

        }
    }

    override suspend fun insertStatistic(statisticModel: StatisticModel) {
        database.statisticDao().insertStatistic(statisticModel.toEntity())
    }

    override fun getCurrentStatistic(): Flow<StatisticModel?> {
        return database.statisticDao().getCurrentStatisticWithPoints()
            .map { statWithPoints ->
                if (statWithPoints == null) return@map null
                val statistic = statWithPoints.statistic
                val pauses = statWithPoints.pauses
                val timestamp = System.currentTimeMillis()
                val travelTime = pauses
                    .groupBy { it.pauseNumber }
                    .map {
                        val sorted = it.value.sortedBy { value -> value.timestamp }
                        val first = sorted.firstOrNull()?.timestamp ?: 0
                        val last = sorted.lastOrNull()?.timestamp ?: 0
                        println("first: $first")
                        println("last: $last")
                        last - first
                    }
                    .sum()
                    .let { allPausesTime ->
                        timestamp - statistic.startTime - allPausesTime
                    }
                statistic.toModel(travelTime)
            }

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
        return insertAndGet(statistic).toModel(0)
    }

    override fun logsFlow(statisticId: Int): Flow<List<PositionDataModel>> {
        return combine(
            database.pauseDao().getPausesByStatistic(statisticId),
            database.routePointsDao().getRoutePointsFlow(statisticId)
        ) { pauses, points ->
            val mappedAndSortedPauses = pauses.map {
                PositionDataModel(
                    it.point,
                    it.timestamp,
                    PositionTypes.PAUSE
                )
            }
            val mappedAndSortedPoints = points.map {
                PositionDataModel(
                    it.point,
                    it.timestamp,
                    PositionTypes.RUN
                )
            }
            mappedAndSortedPoints.plus(mappedAndSortedPauses).sortedBy { it.timestamp }
        }
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
        if (isPaused) {
            savePausePoint(
                statisticId = statisticId,
                point = geoPoint,
                timestamp = timestamp,
                statisticLifecycle = statistic.lifecycle
            )
        } else {
            saveRoutePoint(
                statisticId = statisticId,
                point = geoPoint,
                timestamp = timestamp,
                statisticLifecycle = statistic.lifecycle
            )
        }
        database.statisticDao().updateStatistic(newStatistic)
    }

    private suspend fun savePausePoint(
        statisticId: Int,
        point: GeoPoint,
        timestamp: Long,
        statisticLifecycle: StatisticLifecycle
    ) {
        val lastPauseNumber = database.pauseDao().getLastPausePoint(statisticId)
        val pauseNumber = when {
            lastPauseNumber == null -> 1
            statisticLifecycle == StatisticLifecycle.PAUSED -> lastPauseNumber.pauseNumber
            else -> lastPauseNumber.pauseNumber + 1
        }
        val entity = PauseEntity(
            point = point,
            timestamp = timestamp,
            statisticId = statisticId,
            pauseNumber = pauseNumber
        )
        database.pauseDao().insertPause(entity)
    }

    private suspend fun saveRoutePoint(
        statisticId: Int,
        point: GeoPoint,
        timestamp: Long,
        statisticLifecycle: StatisticLifecycle
    ) {
        val lastRouteNumber = database.routePointsDao().getLastRouteNumber(statisticId)
        val pauseNumber = when {
            lastRouteNumber == null -> 1
            statisticLifecycle == StatisticLifecycle.PAUSED -> lastRouteNumber.routeNumber
            else -> lastRouteNumber.routeNumber + 1
        }
        val entity = RoutePointEntity(
            statisticId = statisticId,
            point = point,
            timestamp = timestamp,
            routeNumber = pauseNumber
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