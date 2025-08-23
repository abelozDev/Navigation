package ru.maplyb.navigation.gui.impl.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import ru.maplyb.navigation.gui.impl.data.remote.client.OpenStreetMapRoutingHttpClient
import java.util.Locale

internal class OpenStreetMapRoutingApiImpl(
	private val httpClient: HttpClient = OpenStreetMapRoutingHttpClient.client
) : OpenStreetMapRoutingApi {

	private fun formatCoord(value: Double): String = String.format(Locale.US, "%.6f", value)

	override suspend fun getRoute(
		lon1: Double,
		lat1: Double,
		lon2: Double,
		lat2: Double,
		alternatives: Boolean,
		overview: String,
		steps: Boolean,
		geometries: String
	): String {
		val segment = "${formatCoord(lon1)},${formatCoord(lat1)};${formatCoord(lon2)},${formatCoord(lat2)}"
		val url = "${OpenStreetMapRoutingHttpClient.BASE_URL}/${OpenStreetMapRoutingHttpClient.CAR_PROFILE}/$segment"
		return httpClient.get(url) {
			parameter("alternatives", alternatives)
			parameter("overview", overview)
			parameter("steps", steps)
			parameter("geometries", geometries)
		}.body<String>()
	}

	override suspend fun getPedestrianRoute(
		lon1: Double,
		lat1: Double,
		lon2: Double,
		lat2: Double,
		alternatives: Boolean,
		overview: String,
		steps: Boolean,
		geometries: String
	): String {
		val segment = "${formatCoord(lon1)},${formatCoord(lat1)};${formatCoord(lon2)},${formatCoord(lat2)}"
		val url = "${OpenStreetMapRoutingHttpClient.BASE_URL}/${OpenStreetMapRoutingHttpClient.FOOT_PROFILE}/$segment"
		return httpClient.get(url) {
			parameter("alternatives", alternatives)
			parameter("overview", overview)
			parameter("steps", steps)
			parameter("geometries", geometries)
		}.body<String>()
	}
}                        