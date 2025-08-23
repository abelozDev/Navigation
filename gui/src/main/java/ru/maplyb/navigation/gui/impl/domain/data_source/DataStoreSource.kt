package ru.maplyb.navigation.gui.impl.domain.data_source

import kotlinx.coroutines.flow.Flow
import ru.maplyb.navigation.gui.api.model.RouteType
import ru.maplyb.navigation.gui.impl.data.datastore.DataStoreSourceImpl

internal interface DataStoreSource {

	suspend fun savePauseState(state: Boolean)

	fun isPauseEnabled(): Flow<Boolean>

	fun getRouteType(): Flow<RouteType>

	suspend fun saveRouteType(routeType: RouteType)

	companion object {
		fun create(context: android.content.Context): DataStoreSource {
			return DataStoreSourceImpl.create(context)
		}
	}
}