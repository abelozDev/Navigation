package ru.maplyb.navigation.gui.impl.data.remote.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*

internal object OpenStreetMapRoutingHttpClient {
	internal const val BASE_URL: String = "https://routing.openstreetmap.de/routed-car/route/v1/driving"

	internal val client: HttpClient by lazy {
		HttpClient(OkHttp) {
			defaultRequest {
				url(BASE_URL)
			}
		}
	}
} 