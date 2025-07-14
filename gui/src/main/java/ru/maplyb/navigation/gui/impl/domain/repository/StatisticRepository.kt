package ru.maplyb.navigation.gui.impl.domain.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import ru.maplyb.navigation.gui.impl.data.database.Database
import ru.maplyb.navigation.gui.impl.data.repository.StatisticRepositoryImpl
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel

internal interface StatisticRepository {

    fun getStatisticsFlow(): Flow<List<StatisticModel>>

    fun getStatisticByIdFlow(id: Int): Flow<StatisticModel?>

    suspend fun insertStatistic(statisticModel: StatisticModel)

    suspend fun deleteStatistic(statisticModel: StatisticModel)

    suspend fun checkStartRouteIsPossible(): Boolean

    suspend fun createEmptyStatistic(): StatisticModel

    companion object {
        fun create(context: Context): StatisticRepository {
            val database = Database.provideDatabase(context)
            return StatisticRepositoryImpl(database)
        }
    }

}