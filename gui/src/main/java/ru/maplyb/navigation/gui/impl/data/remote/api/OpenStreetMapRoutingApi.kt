package ru.maplyb.navigation.gui.impl.data.remote.api

internal interface OpenStreetMapRoutingApi {
	/**
	 * GET https://routing.openstreetmap.de/routed-car/route/v1/driving/{lon1},{lat1};{lon2},{lat2}
	 *   ?alternatives=false&overview=full&steps=false&geometries=geojson
	 */
	suspend fun getRoute(
		lon1: Double,
		lat1: Double,
		lon2: Double,
		lat2: Double,
		alternatives: Boolean = false,
		overview: String = "full",
		steps: Boolean = false,
		geometries: String = "geojson"
	): String

	/**
	 * GET https://routing.openstreetmap.de/routed-foot/route/v1/driving/{lon1},{lat1};{lon2},{lat2}
	 *   ?alternatives=false&overview=full&steps=false&geometries=geojson
	 */
	suspend fun getPedestrianRoute(
		lon1: Double,
		lat1: Double,
		lon2: Double,
		lat2: Double,
		alternatives: Boolean = false,
		overview: String = "full",
		steps: Boolean = false,
		geometries: String = "geojson"
	): String
} 