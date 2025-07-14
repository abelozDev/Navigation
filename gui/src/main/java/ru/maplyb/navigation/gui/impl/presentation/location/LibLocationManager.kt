package ru.maplyb.navigation.gui.impl.presentation.location

import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import ru.maplyb.navigation.gui.impl.presentation.permission.PermissionHelper
import ru.maplyb.navigation.gui.impl.util.log
import kotlin.math.log

internal class LibLocationManager(
    private val context: Application
) {
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val provider = when {
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
        else -> null
    }

    fun init(
        onLocationUpdated: (Location) -> Unit
    ): Result<Unit> {
        if (provider == null) {
            return Result.failure(IllegalStateException("Location provider not found"))
        }
        if (!PermissionHelper().checkLocationPermission(context)) {
            return Result.failure(IllegalStateException("Location permission not granted"))
        }
        locationManager.requestLocationUpdates(provider, 1_000, 10f, { location ->
            onLocationUpdated(location)
        }, null)
        return Result.success(Unit)
    }
}