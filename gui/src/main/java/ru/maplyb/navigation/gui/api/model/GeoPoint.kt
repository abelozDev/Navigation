package ru.maplyb.navigation.gui.api.model

import android.location.Location

public data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double
): java.io.Serializable

internal fun Location.toGeoPoint(): GeoPoint = GeoPoint(latitude, longitude, altitude)
