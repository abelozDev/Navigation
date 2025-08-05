package ru.maplyb.navigation.gui.impl.domain.data_source

import android.app.Application
import kotlinx.coroutines.flow.Flow
import ru.maplyb.navigation.gui.impl.data.datastore.DataStoreSourceImpl

internal interface DataStoreSource {

    suspend fun savePauseState(state: Boolean)

    fun isPauseEnabled(): Flow<Boolean>

    companion object {
        fun create(context: Application): DataStoreSource {
            return DataStoreSourceImpl.init(context)
        }
    }
}