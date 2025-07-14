package ru.maplyb.navigation.gui.api

import android.app.Activity
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.impl.MaplybNavigationApiImpl

public interface MaplybNavigationApi {

    /**
     * Инициализация библиотеки
     * */
    public fun init(activity: Activity): Unit

    /**Начало маршрута
     * @param endPoint конечная точка маршрута*/
    public fun startRoute(endPoint: GeoPoint, locationListener: NavigationLocationListener): Unit

    public companion object {
        public fun create(): MaplybNavigationApi = MaplybNavigationApiImpl
    }
}