package com.ytapps.composetemplate.core.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

// Extension to create DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val appContext: Context,
) : IPreferencesManager {

    private val dataStore = appContext.dataStore
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val cachedUUID: StateFlow<String?> = dataStore.data
        .map { preferences -> preferences[Keys.UUID] }
        .stateIn(scope, SharingStarted.Eagerly, null)

    override fun getUUID(): String? = cachedUUID.value
    
    override fun hasUser(): Boolean = cachedUUID.value != null

    override suspend fun setUUID(uuid: String) {
        dataStore.edit { preferences ->
            preferences[Keys.UUID] = uuid
        }
    }

    override suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    override val uuidFlow: Flow<String?>
        get() = dataStore.data.map { it[Keys.UUID] }

    private object Keys {
        val UUID = stringPreferencesKey("key_uuid")
    }
}
