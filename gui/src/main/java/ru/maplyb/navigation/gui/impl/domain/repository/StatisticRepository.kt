package ru.maplyb.navigation.gui.impl.domain.repository

import android.app.Application
import kotlinx.coroutines.flow.Flow
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.impl.data.local.database.Database
import ru.maplyb.navigation.gui.impl.data.local.model.PositionDataModel
import ru.maplyb.navigation.gui.impl.data.local.repository.StatisticRepositoryImpl
import ru.maplyb.navigation.gui.impl.domain.data_source.DataStoreSource
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel

internal interface StatisticRepository {

    suspend fun updatePauseState(state: Boolean)
    fun isPauseEnabled(): Flow<Boolean>

    suspend fun pause(statisticId: Int)
    suspend fun forcePause(statisticId: Int)
    suspend fun clear()
    fun getStatisticsFlow(): Flow<List<StatisticModel>>

    fun getStatisticByIdFlow(id: Int): Flow<StatisticModel?>

    suspend fun insertStatistic(statisticModel: StatisticModel)


    suspend fun deleteStatistic(statisticModel: StatisticModel)

    suspend fun checkStartRouteIsPossible(): Boolean

    fun getLastStatistic(): Flow<StatisticModel?>

    fun getCurrentStatistic(): Flow<StatisticModel?>

    suspend fun stopStatistic(statisticId: Int)

    suspend fun resumeStatistic(statisticId: Int)

    suspend fun finishStatistic(statisticId: Int)

    suspend fun resumeCurrentStatistic(): StatisticModel?

    suspend fun createEmptyStatistic(currentPosition: GeoPoint?, endPosition: GeoPoint): StatisticModel

    fun logsFlow(statisticId: Int): Flow<List<PositionDataModel>>

    suspend fun updateLastPosition(statisticId: Int, geoPoint: GeoPoint)

    companion object {
        fun create(context: Application): StatisticRepository {
            val database = Database.provideDatabase(context)
            val datastore = DataStoreSource.create(context)
            return StatisticRepositoryImpl(database, datastore)
        }
    }

}