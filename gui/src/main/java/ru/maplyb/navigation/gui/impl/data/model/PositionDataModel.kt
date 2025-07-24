package ru.maplyb.navigation.gui.impl.data.model

import ru.maplyb.navigation.gui.api.model.GeoPoint

internal data class PositionDataModel(
    val point: GeoPoint,
    val timestamp: Long,
    val type: PositionTypes
)

internal enum class PositionTypes {
    PAUSE, RUN
}
