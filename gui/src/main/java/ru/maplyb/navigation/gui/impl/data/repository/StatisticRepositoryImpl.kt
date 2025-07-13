package ru.maplyb.navigation.gui.impl.data.repository

import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.maplyb.navigation.gui.impl.data.database.NavigationDatabase
import ru.maplyb.navigation.gui.impl.data.entity.StatisticEntity
import ru.maplyb.navigation.gui.impl.data.entity.toEntity
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel
import ru.maplyb.navigation.gui.impl.domain.repository.StatisticRepository

internal class StatisticRepositoryImpl(
    private val database: NavigationDatabase
): StatisticRepository {

    override fun getStatisticsFlow(): Flow<List<StatisticModel>> {
        return database.statisticDao().getAllFlow().map { list ->
            list.map {
                it.toModel()
            }
        }
    }

    override fun getStatisticByIdFlow(id: Int): Flow<StatisticModel?> {
        return database.statisticDao().getByIdFlow(id).map { it?.toModel() }
    }

    override suspend fun insertStatistic(statisticModel: StatisticModel) {
        database.statisticDao().insertStatistic(statisticModel.toEntity())
    }

    override suspend fun deleteStatistic(statisticModel: StatisticModel) {
        database.statisticDao().deleteStatisticById(statisticModel.id)
    }

    override suspend fun createEmptyStatistic(): StatisticModel {
        val statistic = StatisticEntity(startTime = System.currentTimeMillis())
        return insertAndGet(statistic).toModel()
    }

    @Transaction
    private suspend fun insertAndGet(statistic: StatisticEntity): StatisticEntity {
        val id = database.statisticDao().insertStatistic(statistic)
        val createdStatistic = database.statisticDao().getById(id)
        check(createdStatistic != null) {"statistic with id = $id is null"}
        return createdStatistic
    }

}