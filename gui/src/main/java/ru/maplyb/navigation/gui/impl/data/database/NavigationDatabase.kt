package ru.maplyb.navigation.gui.impl.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.maplyb.navigation.gui.impl.data.converters.LifecycleConverter
import ru.maplyb.navigation.gui.impl.data.dao.StatisticDao
import ru.maplyb.navigation.gui.impl.data.entity.StatisticEntity

@Database(
    entities = [StatisticEntity::class],
    version = 1
)
@TypeConverters(value = [LifecycleConverter::class])
internal abstract class NavigationDatabase : RoomDatabase() {
    abstract fun statisticDao(): StatisticDao
}