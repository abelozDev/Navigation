package ru.maplyb.navigation.gui.impl.data.remote.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.*

internal object OpenStreetMapRoutingHttpClient {
	internal const val BASE_URL: String = "https://routing.openstreetmap.de/routed-car/route/v1/driving"

	internal val client: HttpClient by lazy {
		HttpClient(OkHttp) {
			install(Logging) {
				logger = Logger.DEFAULT
				level = LogLevel.BODY
			}
			defaultRequest {
				url(BASE_URL)
			}
		}
	}
} 