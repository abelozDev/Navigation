package ru.maplyb.navigation.gui.impl.data.datastore

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.maplyb.navigation.gui.impl.domain.data_source.DataStoreSource


internal object DataStoreSourceImpl : DataStoreSource {

    private lateinit var context: Application

    private val PAUSE_STATE_KEY = booleanPreferencesKey("PAUSE_STATE_KEY")

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "navigation_lib_datastore")

    fun init(context: Application): DataStoreSource {
        this.context = context
        return this
    }

    override suspend fun savePauseState(state: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PAUSE_STATE_KEY] = state
        }
    }

    override fun isPauseEnabled(): Flow<Boolean> {
        return context.dataStore.data.map {
            it[PAUSE_STATE_KEY] ?: false
        }
    }

}