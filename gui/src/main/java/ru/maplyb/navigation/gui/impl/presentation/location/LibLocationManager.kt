package ru.maplyb.navigation.gui.impl.presentation.location

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import ru.maplyb.navigation.gui.impl.presentation.permission.PermissionHelper
import ru.maplyb.navigation.gui.impl.util.log
import kotlin.math.log

internal interface LibLocationManager {

    fun getLastKnownLocation(): Location?
    fun init(): Flow<Location>
    companion object {
        fun create(application: Application): LibLocationManager {
            return LibLocationManagerImpl.create(application)
        }
    }
}

/**Инициализация только через функцию create!*/
private object LibLocationManagerImpl : LibLocationManager {

    private lateinit var locationManager: LocationManager
    private lateinit var application: Application
    private var provider: String? = null

    fun create(application: Application): LibLocationManager {
        if (!::application.isInitialized || !::locationManager.isInitialized) {
            this.application = application
            locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            provider = when {
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                else -> null
            }
        }
        return this
    }


    override fun getLastKnownLocation(): Location? {
        val currentProvider = provider ?: throw IllegalStateException("provider must not be null")
        return if (ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw IllegalStateException("Location permission not granted")
        } else {
            locationManager.getLastKnownLocation(currentProvider)
        }

    }

    override fun init(): Flow<Location> = callbackFlow {
        val onLocationUpdated = LocationListener { location ->
            trySend(location)
        }
        val currentProvider = provider ?: throw IllegalStateException("Location provider not found")
        if (!PermissionHelper().checkLocationPermission(application)) throw IllegalStateException("Location permission not granted")
        locationManager.requestLocationUpdates(
            currentProvider, 1_000, 10f, onLocationUpdated,
            null
        )
        awaitClose {
            locationManager.removeUpdates(onLocationUpdated)
        }
    }
}