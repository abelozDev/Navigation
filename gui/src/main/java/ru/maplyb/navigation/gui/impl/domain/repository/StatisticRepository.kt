package ru.maplyb.navigation.gui.impl.domain.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.impl.data.database.Database
import ru.maplyb.navigation.gui.impl.data.repository.StatisticRepositoryImpl
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel

internal interface StatisticRepository {

    suspend fun pause(statisticId: Int)
    suspend fun clear()
    fun getStatisticsFlow(): Flow<List<StatisticModel>>

    fun getStatisticByIdFlow(id: Int): Flow<StatisticModel?>

    suspend fun insertStatistic(statisticModel: StatisticModel)


    suspend fun deleteStatistic(statisticModel: StatisticModel)

    suspend fun checkStartRouteIsPossible(): Boolean

    fun getCurrentStatistic(): Flow<StatisticModel?>

    suspend fun createEmptyStatistic(currentPosition: GeoPoint?, endPosition: GeoPoint): StatisticModel

    suspend fun updateLastPosition(statisticId: Int, geoPoint: GeoPoint)

    companion object {
        fun create(context: Context): StatisticRepository {
            val database = Database.provideDatabase(context)
            return StatisticRepositoryImpl(database)
        }
    }

}