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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.maplyb.navigation.gui.api.NavigationLocationListener
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.api.model.toGeoPoint
import ru.maplyb.navigation.gui.impl.data.local.database.Database
import ru.maplyb.navigation.gui.impl.data.local.database.NavigationDatabase
import ru.maplyb.navigation.gui.impl.domain.model.StartRouteArgs
import ru.maplyb.navigation.gui.impl.domain.model.StartRouteByPointsArgs
import ru.maplyb.navigation.gui.impl.domain.repository.StatisticRepository
import ru.maplyb.navigation.gui.impl.presentation.location.LibLocationManager
import ru.maplyb.navigation.gui.impl.util.distanceInMeters
import ru.maplyb.navigation.gui.impl.util.serializable

internal class NavigationService() : Service() {

	private val binder = LocalBinder()

	override fun onBind(p0: Intent?): IBinder = binder

	private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

	private lateinit var database: NavigationDatabase
	private lateinit var locationManager: LibLocationManager
	private lateinit var repository: StatisticRepository

	private var locationListener: NavigationLocationListener? = null

	private var statisticId: Int? = null

	override fun onCreate() {
		super.onCreate()
		database = Database.provideDatabase(applicationContext)
		locationManager = LibLocationManager.create(application)
		repository = StatisticRepository.create(application)
	}

	override fun onDestroy() {
		super.onDestroy()
		coroutineScope.cancel()
		locationListener = null
		println("TEST SERVICE onDestroy")
	}


	fun stopServiceFromClient() {
		/*stopForeground(STOP_FOREGROUND_REMOVE)*/
		stopSelf()
	}

	fun setLocationListener(listener: NavigationLocationListener) {
		this.locationListener = listener
	}

	inner class LocalBinder : Binder() {
		fun getService(): NavigationService = this@NavigationService
	}

	private fun startRoute(args: StartRouteArgs, isRedelivered: Boolean) {
		coroutineScope.launch {
			statisticId = if (args.statisticId == null) {
				val haveStartedRoute = repository.getLastStatistic().first()
				if (isRedelivered && haveStartedRoute != null) {
					haveStartedRoute.id
				} else {
					if (haveStartedRoute != null) {
						repository.deleteStatistic(haveStartedRoute)
					}
					val lastKnowLocation = locationManager.getLastKnownLocation()?.let {
						GeoPoint(
							latitude = it.latitude,
							longitude = it.longitude,
							altitude = it.altitude
						)
					}
					repository.createEmptyStatistic(lastKnowLocation, args.endPoint).id
				}
			} else args.statisticId

			withContext(Dispatchers.Main) {
				locationManager
					.init()
					.collect { location ->
						ensureActive()
						check(statisticId != null) { "statistic id is null" }
						location
							.onSuccess {
								repository.updateLastPosition(
									statisticId!!,
									it.toGeoPoint()
								)
								locationListener?.locationUpdated(
									startLocation = GeoPoint(
										latitude = it.latitude,
										longitude = it.longitude,
										altitude = it.altitude
									),
									endLocation = args.endPoint
								)

							}
							.onFailure {
								locationListener?.onFailure(it.message ?: "Геолокация недоступна")
							}
						}
			}
		}
	}

	private fun startRouteByPoints(args: StartRouteByPointsArgs, isRedelivered: Boolean) {
		coroutineScope.launch {
			val remoteRoute = database.remoteRouteDao().getRoute(args.routeId)
			val remotePoints = database.remoteRouteDao().getRoutePoints(args.routeId)
			check(remoteRoute != null && remotePoints.isNotEmpty()) { "Saved route not found or has no points" }

			val finalEndPoint = remoteRoute.endPoint

			statisticId = if (args.statisticId == null) {
				val haveStartedRoute = repository.getLastStatistic().first()
				if (isRedelivered && haveStartedRoute != null) {
					haveStartedRoute.id
				} else {
					if (haveStartedRoute != null) {
						repository.deleteStatistic(haveStartedRoute)
					}
					val lastKnowLocation = locationManager.getLastKnownLocation()?.let {
						GeoPoint(
							latitude = it.latitude,
							longitude = it.longitude,
							altitude = it.altitude
						)
					}
					repository.createEmptyStatistic(lastKnowLocation, finalEndPoint).id
				}
			} else args.statisticId

			var currentTargetIndex = 0
			withContext(Dispatchers.Main) {
				locationManager
					.init()
					.collect { location ->
						ensureActive()
						check(statisticId != null) { "statistic id is null" }
						location
							.onSuccess {
								repository.updateLastPosition(
									statisticId!!,
									it.toGeoPoint()
								)

								val target = remotePoints[currentTargetIndex].point
								val distance = distanceInMeters(
									lat1 = it.latitude,
									lon1 = it.longitude,
									lat2 = target.latitude,
									lon2 = target.longitude
								)
								if (distance <= 15) {
									if (currentTargetIndex < remotePoints.lastIndex) {
										currentTargetIndex++
									} else {
										repository.finishStatistic(statisticId!!)
										stopServiceFromClient()
										return@collect
									}
								}

								val dynamicEnd = remotePoints[currentTargetIndex].point
								locationListener?.locationUpdated(
									startLocation = GeoPoint(
										latitude = it.latitude,
										longitude = it.longitude,
										altitude = it.altitude
									),
									endLocation = dynamicEnd
								)
							}
							.onFailure {
								locationListener?.onFailure(it.message ?: "Геолокация недоступна")
							}
						}
			}
		}
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		val multiArgs = intent?.serializable<StartRouteByPointsArgs>(NAVIGATION_ROUTE_ID)
		if (multiArgs != null) {
			val isRedeliveredMulti = flags and START_FLAG_REDELIVERY != 0
			startRouteByPoints(multiArgs, isRedeliveredMulti)
			println("TEST SERVICE onStartCommand multi $multiArgs")
			return START_REDELIVER_INTENT
		}

		val args = intent?.serializable<StartRouteArgs>(NAVIGATION_END_POINT)
		check(args != null) { "args is null" }
		val isRedelivered = flags and START_FLAG_REDELIVERY != 0
		startRoute(args, isRedelivered)
		println("TEST SERVICE onStartCommand $args")
		return START_REDELIVER_INTENT
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
		const val NAVIGATION_ROUTE_ID = "NAVIGATION_ROUTE_ID"
		const val NAVIGATION_NOTIFICATION_ID = 353224342

	}
}