package ru.maplyb.navigation.gui.impl.domain.model

import androidx.compose.ui.graphics.Color
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.api.model.RouteStatistic
import ru.maplyb.navigation.gui.impl.data.entity.KmInHour
import ru.maplyb.navigation.gui.impl.data.entity.Meters
import java.io.Serializable

/**
 * @param startTime начало пути
 * @param totalDistance осталось пройти
 * @param leftToDo пройдено
 * @param lastPosition последняя известная позиция
 * @param endPoint конечная точка
 * @param travelTime время в пути
 * @param averageSpeed средняя скорость
 * @param startPosition начальная позиция
 * @param currentSpeed текущая скорость
 * @param lifecycle жизненный цикл
 * */

internal data class StatisticModel(
    val id: Int,
    val startTime: Long,
    val totalDistance: Meters,
    val leftToDo: Meters = 0,
    val lastPosition: GeoPoint?,
    val endPoint: GeoPoint,
    val travelTime: Long,
    val averageSpeed: KmInHour,
    val startPosition: GeoPoint?,
    val currentSpeed: KmInHour,
    val lifecycle: StatisticLifecycle = StatisticLifecycle.CREATED
): Serializable {
    companion object {
        fun default(): StatisticModel = StatisticModel(
            id = 0,
            startTime = 0,
            totalDistance = 0,
            lastPosition = null,
            endPoint = GeoPoint(0.0,0.0,0.0),
            travelTime = 0,
            averageSpeed = 0.0,
            startPosition = null,
            currentSpeed = 0.0
        )
    }

    fun toRouteStatistic(): RouteStatistic = RouteStatistic(
        totalDistance = totalDistance,
        endPoint = endPoint,
        lastPosition = lastPosition
    )
}


/*
* CREATED - статистика создана и работает
* PAUSED - пауза (нет движения)
* FORCE_PAUSE - пользователь поставил на паузу
* STOPPED - сервис перестал работать, можно перезапустить
* END - маршрут закончен*/
internal enum class StatisticLifecycle(val ruName: String, val color: Color) {
    CREATED("Начат", Color.Green),
    PAUSED("Пауза", Color(0xffFFB02C)),
    FORCE_PAUSE("Пауза", Color(0xffFFB02C)),
    STOPPED("Остановлен", Color(0xffFFB02C)),
    END("Завешен", Color.Red);
}

