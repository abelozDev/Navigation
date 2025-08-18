package ru.maplyb.navigation.gui.impl.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.maplyb.navigation.gui.impl.data.local.converters.GeoPointConverter
import ru.maplyb.navigation.gui.impl.data.local.converters.LifecycleConverter
import ru.maplyb.navigation.gui.impl.data.local.dao.PauseDao
import ru.maplyb.navigation.gui.impl.data.local.dao.RemoteRouteDao
import ru.maplyb.navigation.gui.impl.data.local.dao.RoutePointsDao
import ru.maplyb.navigation.gui.impl.data.local.dao.StatisticDao
import ru.maplyb.navigation.gui.impl.data.local.entity.PauseEntity
import ru.maplyb.navigation.gui.impl.data.local.entity.RemoteRouteEntity
import ru.maplyb.navigation.gui.impl.data.local.entity.RemoteRoutePointEntity
import ru.maplyb.navigation.gui.impl.data.local.entity.RoutePointEntity
import ru.maplyb.navigation.gui.impl.data.local.entity.StatisticEntity

@Database(
    entities = [
        StatisticEntity::class,
        RoutePointEntity::class,
        PauseEntity::class,
        RemoteRouteEntity::class,
        RemoteRoutePointEntity::class
    ],
    version = 2
)
@TypeConverters(value = [LifecycleConverter::class, GeoPointConverter::class])
internal abstract class NavigationDatabase : RoomDatabase() {
    abstract fun statisticDao(): StatisticDao
    abstract fun routePointsDao(): RoutePointsDao
    abstract fun pauseDao(): PauseDao
    abstract fun remoteRouteDao(): RemoteRouteDao
}