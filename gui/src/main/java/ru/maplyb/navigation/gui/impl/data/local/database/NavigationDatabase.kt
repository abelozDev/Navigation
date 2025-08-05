package ru.maplyb.navigation.gui.impl.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.maplyb.navigation.gui.impl.data.local.converters.GeoPointConverter
import ru.maplyb.navigation.gui.impl.data.local.converters.LifecycleConverter
import ru.maplyb.navigation.gui.impl.data.local.dao.PauseDao
import ru.maplyb.navigation.gui.impl.data.local.dao.RoutePointsDao
import ru.maplyb.navigation.gui.impl.data.local.dao.StatisticDao
import ru.maplyb.navigation.gui.impl.data.local.entity.PauseEntity
import ru.maplyb.navigation.gui.impl.data.local.entity.RoutePointEntity
import ru.maplyb.navigation.gui.impl.data.local.entity.StatisticEntity

@Database(
    entities = [
        StatisticEntity::class,
        RoutePointEntity::class,
        PauseEntity::class
    ],
    version = 1
)
@TypeConverters(value = [LifecycleConverter::class, GeoPointConverter::class])
internal abstract class NavigationDatabase : RoomDatabase() {
    abstract fun statisticDao(): StatisticDao
    abstract fun routePointsDao(): RoutePointsDao
    abstract fun pauseDao(): PauseDao
}