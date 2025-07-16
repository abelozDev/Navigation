package ru.maplyb.navigation.gui.impl.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.maplyb.navigation.gui.impl.data.converters.GeoPointConverter
import ru.maplyb.navigation.gui.impl.data.converters.LifecycleConverter
import ru.maplyb.navigation.gui.impl.data.dao.RoutePointsDao
import ru.maplyb.navigation.gui.impl.data.dao.StatisticDao
import ru.maplyb.navigation.gui.impl.data.entity.RoutePointEntity
import ru.maplyb.navigation.gui.impl.data.entity.StatisticEntity

@Database(
    entities = [
        StatisticEntity::class,
        RoutePointEntity::class
    ],
    version = 1
)
@TypeConverters(value = [LifecycleConverter::class, GeoPointConverter::class])
internal abstract class NavigationDatabase : RoomDatabase() {
    abstract fun statisticDao(): StatisticDao
    abstract fun routePointsDao(): RoutePointsDao
}