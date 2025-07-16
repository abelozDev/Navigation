package ru.maplyb.navigation.gui.impl.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import ru.maplyb.navigation.gui.api.model.GeoPoint

internal class GeoPointConverter {

    @TypeConverter
    fun toGeoPoint(value: String?): GeoPoint? {
        return value?.let {
            val gson = Gson()
            gson.fromJson(it, GeoPoint::class.java)
        }
    }

    @TypeConverter
    fun fromGeoPoint(value: GeoPoint?): String? {
        return value?.let {
            val gson = Gson()
            gson.toJson(value)
        }
    }
}