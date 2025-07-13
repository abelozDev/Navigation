package ru.maplyb.navigation.gui.impl

import android.app.Activity
import android.app.Application
import android.location.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.maplyb.navigation.gui.api.MaplybNavigationApi
import ru.maplyb.navigation.gui.api.NavigationLocationListener
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel
import ru.maplyb.navigation.gui.impl.domain.repository.StatisticRepository
import ru.maplyb.navigation.gui.impl.presentation.location.LibLocationManager
import ru.maplyb.navigation.gui.impl.util.log

internal class MaplybNavigationApiImpl: MaplybNavigationApi {

    private lateinit var application: Application
    private lateinit var locationManager: LibLocationManager
    private lateinit var repository: StatisticRepository
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun init(activity: Activity) {
        application = activity.application
        repository = StatisticRepository.create(application)
        locationManager = LibLocationManager(application)
    }

    override fun startRoute(endPoint: GeoPoint, locationListener: NavigationLocationListener) {
        var startLocation: Location? = null
        var currentStatistic: StatisticModel
        scope.launch {
            currentStatistic = repository.createEmptyStatistic()
        }
        locationManager
            .init { location ->
                if (startLocation == null) startLocation = location
                locationListener.locationUpdated(
                    startLocation = GeoPoint(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        altitude = location.altitude
                    ),
                    endLocation = endPoint
                )
            }
            .onFailure { log(it.message.toString()) }
    }
}