package ru.maplyb.navigation.gui.impl.data.repository

import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.impl.data.database.NavigationDatabase
import ru.maplyb.navigation.gui.impl.data.entity.RoutePointEntity
import ru.maplyb.navigation.gui.impl.data.entity.StatisticEntity
import ru.maplyb.navigation.gui.impl.data.entity.toEntity
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

    override suspend fun clear() {
        database.statisticDao().clear()
    }

    override fun getStatisticByIdFlow(id: Int): Flow<StatisticModel?> {
        return database.statisticDao().getByIdFlow(id).map { it?.toModel() }
    }

    override suspend fun insertStatistic(statisticModel: StatisticModel) {
        database.statisticDao().insertStatistic(statisticModel.toEntity())
    }

    override suspend fun getCurrentStatistic(): Flow<StatisticModel?> {
        return database.statisticDao().getCurrentStatistic().map { it?.toModel() }
    }

    override suspend fun checkStartRouteIsPossible(): Boolean {
        val statistics = database.statisticDao().getAll()
        return statistics.isEmpty() || statistics.all { it.lifecycle == StatisticLifecycle.END }
    }

    override suspend fun deleteStatistic(statisticModel: StatisticModel) {
        database.statisticDao().deleteStatisticById(statisticModel.id)
    }

    override suspend fun createEmptyStatistic(currentPosition: GeoPoint?, endPosition: GeoPoint): StatisticModel {
        val statistic = StatisticEntity(
            lastPosition = currentPosition,
            startTime = System.currentTimeMillis(),
            endPoint = endPosition,
        )
        return insertAndGet(statistic).toModel()
    }

    @Transaction
    override suspend fun updateLastPosition(statisticId: Int, geoPoint: GeoPoint) {
        val statistic = database.statisticDao().getById(statisticId.toLong())

        check(statistic != null) { "statistic with id = $statisticId is null" }

        val newStatistic = if (statistic.lastPosition != null) {
            val distanceInMeters = distanceInMeters(
                lat1 = statistic.lastPosition.latitude,
                lon1 = statistic.lastPosition.longitude,
                lat2 = geoPoint.latitude,
                lon2 = geoPoint.longitude
            )
            statistic.copy(
                leftToDo = statistic.leftToDo + distanceInMeters,
                lastPosition = geoPoint
            )
        } else {
            statistic.copy(lastPosition = geoPoint)
        }
        database.statisticDao().updateStatistic(newStatistic)
        saveRoutePoint(statisticId, geoPoint)
    }

    private suspend fun saveRoutePoint(statisticId: Int, point: GeoPoint) {
        val timestamp = System.currentTimeMillis()
        val entity = RoutePointEntity(
            statisticId = statisticId,
            point = point,
            timestamp = timestamp
        )
        database.routePointsDao().insert(entity)
    }

    @Transaction
    private suspend fun insertAndGet(statistic: StatisticEntity): StatisticEntity {
        val id = database.statisticDao().insertStatistic(statistic)
        val createdStatistic = database.statisticDao().getById(id)
        check(createdStatistic != null) { "statistic with id = $id is null" }
        return createdStatistic
    }

}