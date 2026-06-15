package com.ytapps.composetemplate.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ytapps.composetemplate.core.common.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

// Extension to create DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences",
)

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val appContext: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : IPreferencesManager {
    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
    private val dataStore = appContext.dataStore

    // Cached StateFlows for synchronous access
    private val cachedAccessToken: StateFlow<String?> =
        dataStore.data
            .map { preferences -> preferences[Keys.ACCESS_TOKEN] }
            .stateIn(scope, SharingStarted.Eagerly, null)

    private val cachedRefreshToken: StateFlow<String?> =
        dataStore.data
            .map { preferences -> preferences[Keys.REFRESH_TOKEN] }
            .stateIn(scope, SharingStarted.Eagerly, null)

    private val cachedTokenType: StateFlow<String?> =
        dataStore.data
            .map { preferences -> preferences[Keys.TOKEN_TYPE] }
            .stateIn(scope, SharingStarted.Eagerly, null)

    private val cachedUUID: StateFlow<String?> =
        dataStore.data
            .map { preferences -> preferences[Keys.UUID] }
            .stateIn(scope, SharingStarted.Eagerly, null)

    // Synchronous getters (use cached StateFlow values)
    override fun getAccessToken(): String? = cachedAccessToken.value

    override fun getRefreshToken(): String? = cachedRefreshToken.value

    override fun getTokenType(): String? = cachedTokenType.value

    override fun getUUID(): String? = cachedUUID.value

    override fun hasUser(): Boolean = cachedUUID.value != null

    // Async setters (DataStore operations)
    override suspend fun setAccessToken(accessToken: String) {
        dataStore.edit { preferences ->
            preferences[Keys.ACCESS_TOKEN] = accessToken
        }
    }

    override suspend fun setRefreshToken(refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[Keys.REFRESH_TOKEN] = refreshToken
        }
    }

    override suspend fun setTokenType(tokenType: String) {
        dataStore.edit { preferences ->
            preferences[Keys.TOKEN_TYPE] = tokenType
        }
    }

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

    // Flow-based reactive access (delegates to cached StateFlows)
    override val accessTokenFlow: StateFlow<String?> get() = cachedAccessToken

    override val refreshTokenFlow: StateFlow<String?> get() = cachedRefreshToken

    override val tokenTypeFlow: StateFlow<String?> get() = cachedTokenType

    override val uuidFlow: StateFlow<String?> get() = cachedUUID

    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("key_access_token")
        val REFRESH_TOKEN = stringPreferencesKey("key_refresh_token")
        val TOKEN_TYPE = stringPreferencesKey("key_token_type")
        val UUID = stringPreferencesKey("key_uuid")
    }
}
