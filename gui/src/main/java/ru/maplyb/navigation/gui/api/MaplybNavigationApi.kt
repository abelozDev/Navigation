package ru.maplyb.navigation.gui.api

import android.app.Activity
import androidx.compose.runtime.Composable
import ru.maplyb.navigation.gui.api.model.GeoPoint
import ru.maplyb.navigation.gui.api.model.RouteStatistic
import ru.maplyb.navigation.gui.impl.MaplybNavigationApiImpl

public interface MaplybNavigationApi {

	public fun onStatisticChanged(callback: (RouteStatistic) -> Unit)

	public fun setLocationListener(locationListener: NavigationLocationListener)
	public fun resetLocationListener()
	public fun onStop(callback: () -> Unit)
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

	/**Пауза текущего маршрута*/
	public fun pause()

	/**Останавливает статистику (эмитация остановки сервиса)*/
	public fun stopStatistic()
	/**Возобновить последнюю статистику*/
	public fun resumeCurrentStatistic()
	/**Возвращает [GeoPoint] конечной точки, если есть начаный маршрут. Если нет - null */
	public fun currentRouteEndPoint(): GeoPoint?

	 /**Начало маршрута
	 * @param endPoint конечная точка маршрута
	 * @param locationListener возвращает начальную и конечную точки для посторения маршрута
	 *
	 * @throws IllegalStateException если есть начатый но не законченный маршрут*/
	public fun startRoute(endPoint: GeoPoint): Unit

	/**
	 * Запустить навигацию по всем точкам сохраненного маршрута (OSRM geometry)
	 * @param routeId id сохраненного маршрута в БД
	 */
	public fun startRouteByRouteId(routeId: Long): Unit

	/**
	 * Построить маршрут до точки через OSRM, сохранить в БД и запустить навигацию по точкам
	 */
	public fun startRouteByOsrm(endPoint: GeoPoint): Unit

	public companion object {
		public fun create(): MaplybNavigationApi = MaplybNavigationApiImpl
	}
}