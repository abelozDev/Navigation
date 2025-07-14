package ru.maplyb.navigation.gui.api

import android.app.Activity
import androidx.compose.runtime.Composable
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.impl.MaplybNavigationApiImpl

public interface MaplybNavigationApi {

    /**
     * Открывает экран со статистикой
     * */
    public fun show(): Unit
    /**
     * Закрывает экран со статистикой
     * */
    public fun hide(): Unit
    /**
     * Инициализация библиотеки
     * */
    public fun init(activity: Activity): Unit

     /**
      * UI BottomSheet отображение статискики
      *
      *
      * @throws IllegalStateException если нет начатой статистики (исправить логику)*/
    @Composable
    public fun ShowStatistic()

     /**Начало маршрута
     * @param endPoint конечная точка маршрута
     * @param locationListener возвращает начальную и конечную точки для посторения маршрута
     *
     * @throws IllegalStateException если есть начатый но не законченный маршрут*/
    public fun startRoute(endPoint: GeoPoint, locationListener: NavigationLocationListener): Unit

    public companion object {
        public fun create(): MaplybNavigationApi = MaplybNavigationApiImpl
    }
}