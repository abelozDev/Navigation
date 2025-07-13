package ru.maplyb.navigation.gui.api

import ru.maplyb.navigation.gui.api.model.GeoPoint

public fun interface NavigationLocationListener {

    public fun locationUpdated(startLocation: GeoPoint, endLocation: GeoPoint): Unit
}