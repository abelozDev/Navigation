package ru.maplyb.navigation.gui.impl.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.appendPathSegments
import ru.maplyb.navigation.gui.impl.data.remote.client.OpenStreetMapRoutingHttpClient

internal class OpenStreetMapRoutingApiImpl(
	private val httpClient: HttpClient = OpenStreetMapRoutingHttpClient.client
) : OpenStreetMapRoutingApi {
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
		return httpClient.get {
			url {
				appendPathSegments("${lon1},${lat1};${lon2},${lat2}")
			}
			parameter("alternatives", alternatives)
			parameter("overview", overview)
			parameter("steps", steps)
			parameter("geometries", geometries)
		}.body<String>()
	}
}                        