package ru.maplyb.navigation.gui.impl.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.maplyb.navigation.gui.impl.data.local.entity.PauseEntity

@Dao
internal interface PauseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPause(pauseDao: PauseEntity)

    @Query("SELECT * FROM PauseEntity WHERE statisticId = :statisticId")
    fun getPausesByStatistic(statisticId: Int): Flow<List<PauseEntity>>

    @Query("SELECT * FROM PauseEntity WHERE statisticId = :statisticId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastPausePoint(statisticId: Int): PauseEntity?

    @Query("SELECT * FROM PauseEntity")
    fun getAllPausesFlow(): Flow<List<PauseEntity>>
}