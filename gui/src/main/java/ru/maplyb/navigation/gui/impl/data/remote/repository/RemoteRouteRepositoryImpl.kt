package ru.maplyb.navigation.gui.impl.data.remote.repository

import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.api.model.RouteType
import ru.maplyb.navigation.gui.impl.data.local.dao.RemoteRouteDao
import ru.maplyb.navigation.gui.impl.data.local.entity.RemoteRouteEntity
import ru.maplyb.navigation.gui.impl.data.local.entity.RemoteRoutePointEntity
import ru.maplyb.navigation.gui.impl.data.remote.api.OpenStreetMapRoutingApi
import ru.maplyb.navigation.gui.impl.data.remote.model.osrm.OsrmRouteResponse
import ru.maplyb.navigation.gui.impl.domain.data_source.DataStoreSource
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

internal class RemoteRouteRepositoryImpl(
	private val routingApi: OpenStreetMapRoutingApi,
	private val remoteRouteDao: RemoteRouteDao,
	private val datastore: DataStoreSource
) : RemoteRouteRepository {

	private val json: Json = Json { ignoreUnknownKeys = true }

	private fun rdpSimplify(
		coordinates: List<List<Double>>,
		epsilonMeters: Double = 2.0
	): List<List<Double>> {
		if (coordinates.size <= 2) return coordinates

		fun perpendicularDistanceMeters(
			aLon: Double, aLat: Double,
			bLon: Double, bLat: Double,
			pLon: Double, pLat: Double
		): Double {
			val lat0 = (aLat + bLat) / 2.0
			val metersPerLat = 111_320.0
			val metersPerLon = 111_320.0 * cos(Math.toRadians(lat0))
			val ax = 0.0
			val ay = 0.0
			val bx = (bLon - aLon) * metersPerLon
			val by = (bLat - aLat) * metersPerLat
			val px = (pLon - aLon) * metersPerLon
			val py = (pLat - aLat) * metersPerLat

			val abx = bx - ax
			val aby = by - ay
			val apx = px - ax
			val apy = py - ay
			val abLenSq = abx * abx + aby * aby
			if (abLenSq == 0.0) {
				return sqrt((px - ax).pow(2) + (py - ay).pow(2))
			}
			var t = (apx * abx + apy * aby) / abLenSq
			t = max(0.0, min(1.0, t))
			val projx = ax + t * abx
			val projy = ay + t * aby
			return sqrt((px - projx).pow(2) + (py - projy).pow(2))
		}

		fun rdp(start: Int, end: Int, out: MutableList<List<Double>>) {
			var maxDist = -1.0
			var index = -1
			val a = coordinates[start]
			val b = coordinates[end]
			val aLon = a.getOrNull(0) ?: return
			val aLat = a.getOrNull(1) ?: return
			val bLon = b.getOrNull(0) ?: return
			val bLat = b.getOrNull(1) ?: return

			for (i in start + 1 until end) {
				val p = coordinates[i]
				val pLon = p.getOrNull(0) ?: continue
				val pLat = p.getOrNull(1) ?: continue
				val d = perpendicularDistanceMeters(aLon, aLat, bLon, bLat, pLon, pLat)
				if (d > maxDist) {
					maxDist = d
					index = i
				}
			}

			if (maxDist > epsilonMeters && index != -1) {
				rdp(start, index, out)
				out.removeAt(out.lastIndex)
				rdp(index, end, out)
			} else {
				out.add(a)
				out.add(b)
			}
		}

		val result = mutableListOf<List<Double>>()
		rdp(0, coordinates.lastIndex, result)
		return result
	}

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

		return saveRouteFromResponse(responseString, lon1, lat1, lon2, lat2)
	}

	override suspend fun fetchAndSavePedestrianRoute(
		lon1: Double,
		lat1: Double,
		lon2: Double,
		lat2: Double
	): Long {
		val responseString = routingApi.getPedestrianRoute(
			lon1 = lon1,
			lat1 = lat1,
			lon2 = lon2,
			lat2 = lat2,
			alternatives = false,
			overview = "full",
			steps = false,
			geometries = "geojson"
		)

		return saveRouteFromResponse(responseString, lon1, lat1, lon2, lat2)
	}

	override suspend fun fetchAndSaveRouteByCurrentType(
		lon1: Double,
		lat1: Double,
		lon2: Double,
		lat2: Double
	): Long {
		val currentRouteType = datastore.getRouteType().first()
		return when (currentRouteType) {
			RouteType.CAR -> fetchAndSaveRoute(lon1, lat1, lon2, lat2)
			RouteType.FOOT -> fetchAndSavePedestrianRoute(lon1, lat1, lon2, lat2)
		}
	}

	private suspend fun saveRouteFromResponse(
		responseString: String,
		lon1: Double,
		lat1: Double,
		lon2: Double,
		lat2: Double
	): Long {
		val response = json.decodeFromString<OsrmRouteResponse>(responseString)
		val route = response.routes.firstOrNull()
			?: throw IllegalStateException("OSRM returned no routes")

		val coordinates = route.geometry?.coordinates.orEmpty()
		val startPoint = coordinates.firstOrNull()?.let { first ->
			val lon = first.getOrNull(0) ?: lon1
			val lat = first.getOrNull(1) ?: lat1
			GeoPoint(latitude = lat, longitude = lon, altitude = 0.0)
		} ?: GeoPoint(latitude = lat1, longitude = lon1, altitude = 0.0)
		val endPoint = coordinates.lastOrNull()?.let { last ->
			val lon = last.getOrNull(0) ?: lon2
			val lat = last.getOrNull(1) ?: lat2
			GeoPoint(latitude = lat, longitude = lon, altitude = 0.0)
		} ?: GeoPoint(latitude = lat2, longitude = lon2, altitude = 0.0)

		val distanceMeters = route.distance.toInt()
		val durationSeconds = route.duration

		val routeEntity = RemoteRouteEntity(
			createdAt = System.currentTimeMillis(),
			startPoint = startPoint,
			endPoint = endPoint,
			distanceMeters = distanceMeters,
			durationSeconds = durationSeconds
		)

		val simplified = rdpSimplify(coordinates, epsilonMeters = 2.0)
		val points = simplified.mapIndexed { index, lonLat ->
			val lon = lonLat.getOrNull(0) ?: 0.0
			val lat = lonLat.getOrNull(1) ?: 0.0
			RemoteRoutePointEntity(
				routeId = 0, // filled in DAO transaction
				point = GeoPoint(latitude = lat, longitude = lon, altitude = 0.0),
				orderIndex = index
			)
		}

		return remoteRouteDao.replaceAllWithRoute(routeEntity, points)
	}
} 