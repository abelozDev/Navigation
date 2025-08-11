package ru.maplyb.navigation.gui.api

import ru.maplyb.navigation.gui.api.model.GeoPoint

public interface NavigationLocationListener {

    public fun locationUpdated(startLocation: GeoPoint, endLocation: GeoPoint): Unit

    public fun onFailure(message: String)
}