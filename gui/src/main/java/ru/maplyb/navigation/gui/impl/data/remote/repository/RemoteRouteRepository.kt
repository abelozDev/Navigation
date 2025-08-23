package ru.maplyb.navigation.gui.impl.data.remote.repository

import android.app.Application
import ru.maplyb.navigation.gui.impl.data.local.database.Database
import ru.maplyb.navigation.gui.impl.data.remote.api.OpenStreetMapRoutingApiImpl

internal interface RemoteRouteRepository {
	suspend fun fetchAndSaveRoute(
		lon1: Double,
		lat1: Double,
		lon2: Double,
		lat2: Double
	): Long

	suspend fun fetchAndSavePedestrianRoute(
		lon1: Double,
		lat1: Double,
		lon2: Double,
		lat2: Double
	): Long

	/**
	 * Fetch and save route using the currently configured route type
	 */
	suspend fun fetchAndSaveRouteByCurrentType(
		lon1: Double,
		lat1: Double,
		lon2: Double,
		lat2: Double
	): Long

	companion object {
		fun create(application: Application): RemoteRouteRepository {
			val dao = Database.provideDatabase(application).remoteRouteDao()
			val api = OpenStreetMapRoutingApiImpl()
			val datastore = ru.maplyb.navigation.gui.impl.domain.data_source.DataStoreSource.create(application)
			return RemoteRouteRepositoryImpl(api, dao, datastore)
		}
	}
} 