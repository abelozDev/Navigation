package ru.maplyb.navigation.gui.impl.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.maplyb.navigation.gui.impl.data.entity.StatisticEntity
import ru.maplyb.navigation.gui.impl.domain.model.StatisticLifecycle

@Dao
internal interface StatisticDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatistic(statisticEntity: StatisticEntity): Long

    @Query("SELECT * FROM StatisticEntity")
    fun getAllFlow(): Flow<List<StatisticEntity>>

    @Query("SELECT * FROM StatisticEntity")
    fun getAll(): List<StatisticEntity>

    @Query("SELECT * FROM StatisticEntity WHERE lifecycle = :lifecycle")
    fun getCurrentStatistic(lifecycle: StatisticLifecycle = StatisticLifecycle.CREATED): Flow<StatisticEntity?>

    @Query("SELECT * FROM StatisticEntity WHERE id = :id")
    suspend fun getById(id: Long): StatisticEntity?

    @Query("SELECT * FROM StatisticEntity WHERE id = :id")
    fun getByIdFlow(id: Int): Flow<StatisticEntity?>

    @Query("DELETE FROM StatisticEntity WHERE id = :id")
    suspend fun deleteStatisticById(id: Int)
}