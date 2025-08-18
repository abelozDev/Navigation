package ru.maplyb.navigation.gui.impl.data.remote.repository

import kotlinx.serialization.json.Json
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.impl.data.local.dao.RemoteRouteDao
import ru.maplyb.navigation.gui.impl.data.local.entity.RemoteRouteEntity
import ru.maplyb.navigation.gui.impl.data.local.entity.RemoteRoutePointEntity
import ru.maplyb.navigation.gui.impl.data.remote.api.OpenStreetMapRoutingApi
import ru.maplyb.navigation.gui.impl.data.remote.model.osrm.OsrmRouteResponse

internal class RemoteRouteRepositoryImpl(
	private val routingApi: OpenStreetMapRoutingApi,
	private val remoteRouteDao: RemoteRouteDao
) : RemoteRouteRepository {

	private val json: Json = Json { ignoreUnknownKeys = true }

	override suspend fun fetchAndSaveRoute(
		lon1: Double,
		lat1: Double,
		lon2: Double,
		lat2: Double
	): Long {
		val responseString = routingApi.getRoute(
			lon1 = lon1,
			lat1 = lat1,
			lon2 = lon2,
			lat2 = lat2,
			alternatives = false,
			overview = "full",
			steps = false,
			geometries = "geojson"
		)

		val response = json.decodeFromString<OsrmRouteResponse>(responseString)
		val route = response.routes.firstOrNull()
			?: throw IllegalStateException("OSRM returned no routes")

		val distanceMeters = route.distance.toInt()
		val durationSeconds = route.duration

		val startPoint = GeoPoint(latitude = lat1, longitude = lon1, altitude = 0.0)
		val endPoint = GeoPoint(latitude = lat2, longitude = lon2, altitude = 0.0)

		val routeEntity = RemoteRouteEntity(
			createdAt = System.currentTimeMillis(),
			startPoint = startPoint,
			endPoint = endPoint,
			distanceMeters = distanceMeters,
			durationSeconds = durationSeconds
		)

		val coordinates = route.geometry?.coordinates.orEmpty()
		val points = coordinates.mapIndexed { index, lonLat ->
			val lon = lonLat.getOrNull(0) ?: 0.0
			val lat = lonLat.getOrNull(1) ?: 0.0
			RemoteRoutePointEntity(
				routeId = 0, // filled in DAO transaction
				point = GeoPoint(latitude = lat, longitude = lon, altitude = 0.0),
				orderIndex = index
			)
		}

		return remoteRouteDao.insertRouteWithPoints(routeEntity, points)
	}
} 