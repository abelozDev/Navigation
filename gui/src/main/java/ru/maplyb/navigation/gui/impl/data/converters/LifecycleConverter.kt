package ru.maplyb.navigation.gui.impl.data.converters

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import ru.maplyb.navigation.gui.impl.domain.model.StatisticLifecycle

internal class LifecycleConverter {

    @TypeConverter
    fun toLifecycle(value: String): StatisticLifecycle {
        return StatisticLifecycle.valueOf(value)
    }

    @TypeConverter
    fun fromLifecycle(value: StatisticLifecycle): String {
        return value.name
    }
}