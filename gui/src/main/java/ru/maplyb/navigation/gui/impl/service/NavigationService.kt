package ru.maplyb.navigation.gui.impl.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.maplyb.navigation.gui.api.NavigationLocationListener
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.api.model.toGeoPoint
import ru.maplyb.navigation.gui.impl.data.database.Database
import ru.maplyb.navigation.gui.impl.data.database.NavigationDatabase
import ru.maplyb.navigation.gui.impl.domain.model.StartRouteArgs
import ru.maplyb.navigation.gui.impl.domain.repository.StatisticRepository
import ru.maplyb.navigation.gui.impl.presentation.location.LibLocationManager
import ru.maplyb.navigation.gui.impl.util.serializable

internal class NavigationService() : Service() {

    private val binder = LocalBinder()

    override fun onBind(p0: Intent?): IBinder = binder

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private lateinit var database: NavigationDatabase
    private lateinit var locationManager: LibLocationManager
    private lateinit var repository: StatisticRepository

    private var locationListener: NavigationLocationListener? = null


    override fun onCreate() {
        super.onCreate()
        database = Database.provideDatabase(applicationContext)
        locationManager = LibLocationManager.create(application)
        repository = StatisticRepository.create(applicationContext)
    }

    fun setLocationListener(listener: NavigationLocationListener) {
        this.locationListener = listener
    }

    inner class LocalBinder : Binder() {
        fun getService(): NavigationService = this@NavigationService
    }

    private fun startRoute(args: StartRouteArgs) {
        coroutineScope.launch {
            val isCreatePossible = repository.checkStartRouteIsPossible()
            if (!isCreatePossible) throw IllegalStateException("Route already started")
            val lastKnowLocation = locationManager.getLastKnownLocation()?.let {
                GeoPoint(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    altitude = it.altitude
                )
            }
            val newStatisticId: Int =
                repository.createEmptyStatistic(lastKnowLocation, args.endPoint).id
            startForeground(
                NAVIGATION_NOTIFICATION_ID,
                createNotification(
                    title = "Маршрут",
                    description = ""
                )
            )
            withContext(Dispatchers.Main) {
                locationManager
                    .init()
                    .collect { location ->
                        ensureActive()
                        println("location altitude: ${location.altitude}")
                        repository.updateLastPosition(newStatisticId, location.toGeoPoint())
                        locationListener?.locationUpdated(
                            startLocation = GeoPoint(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                altitude = location.altitude
                            ),
                            endLocation = args.endPoint
                        )
                    }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val args = intent?.serializable<StartRouteArgs>(NAVIGATION_END_POINT)
        println("args: $args")
        check(args != null) { "args is null" }
        startRoute(args)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotification(
        title: String,
        description: String
    ): Notification {
        return NotificationCompat.Builder(this, NotificationChannel.DOWNLOAD_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val NAVIGATION_END_POINT = "NAVIGATION_END_POINT"
        const val NAVIGATION_NOTIFICATION_ID = 353224342

    }
}