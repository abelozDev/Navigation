package ru.maplyb.navigation.gui.impl.domain.model

import ru.maplyb.navigation.gui.api.model.GeoPoint


internal data class StartRouteArgs(
    val endPoint: GeoPoint,
    val statisticId: Int?,
    val isResume: Boolean
): java.io.Serializable
