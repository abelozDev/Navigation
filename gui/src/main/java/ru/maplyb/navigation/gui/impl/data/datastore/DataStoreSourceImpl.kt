package ru.maplyb.navigation.gui.impl.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.maplyb.navigation.gui.api.model.RouteType
import ru.maplyb.navigation.gui.impl.domain.data_source.DataStoreSource

private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "navigation_prefs")

internal class DataStoreSourceImpl(private val context: Context) : DataStoreSource {

	private val pauseStateKey = booleanPreferencesKey("pause_state")
	private val routeTypeKey = intPreferencesKey("route_type")

	override fun isPauseEnabled(): Flow<Boolean> {
		return context.datastore.data.map { preferences ->
			preferences[pauseStateKey] ?: false
		}
	}

	override suspend fun savePauseState(state: Boolean) {
		context.datastore.edit { preferences ->
			preferences[pauseStateKey] = state
		}
	}

	override fun getRouteType(): Flow<RouteType> {
		return context.datastore.data.map { preferences ->
			val typeOrdinal = preferences[routeTypeKey] ?: RouteType.FOOT.ordinal
			RouteType.entries[typeOrdinal]
		}
	}

	override suspend fun saveRouteType(routeType: RouteType) {
		context.datastore.edit { preferences ->
			preferences[routeTypeKey] = routeType.ordinal
		}
	}

	companion object {
		fun create(context: Context): DataStoreSource {
			return DataStoreSourceImpl(context)
		}
	}
}