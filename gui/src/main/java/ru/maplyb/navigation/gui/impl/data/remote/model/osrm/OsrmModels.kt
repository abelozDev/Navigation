package ru.maplyb.navigation.gui.impl.data.remote.model.osrm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OsrmRouteResponse(
	@SerialName("code") val code: String = "",
	@SerialName("waypoints") val waypoints: List<OsrmWaypoint> = emptyList(),
	@SerialName("routes") val routes: List<OsrmRoute> = emptyList()
)

@Serializable
internal data class OsrmWaypoint(
	@SerialName("name") val name: String = "",
	@SerialName("location") val location: List<Double> = emptyList(), // [lon, lat]
	@SerialName("distance") val distance: Double? = null
)

@Serializable
internal data class OsrmRoute(
	@SerialName("distance") val distance: Double = 0.0,
	@SerialName("duration") val duration: Double = 0.0,
	@SerialName("weight") val weight: Double? = null,
	@SerialName("weight_name") val weightName: String? = null,
	@SerialName("geometry") val geometry: OsrmGeometryLineString? = null,
	@SerialName("legs") val legs: List<OsrmLeg> = emptyList()
)

@Serializable
internal data class OsrmLeg(
	@SerialName("summary") val summary: String = "",
	@SerialName("distance") val distance: Double = 0.0,
	@SerialName("duration") val duration: Double = 0.0
)

@Serializable
internal data class OsrmGeometryLineString(
	@SerialName("type") val type: String = "LineString",
	@SerialName("coordinates") val coordinates: List<List<Double>> = emptyList() // [[lon, lat], ...]
) 