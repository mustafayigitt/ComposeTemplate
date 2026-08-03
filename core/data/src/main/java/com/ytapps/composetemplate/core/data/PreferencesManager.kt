package com.ytapps.composetemplate.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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
@Suppress("TooManyFunctions")
class PreferencesManager
    @Inject
    constructor(
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

        private val cachedIsDarkMode: StateFlow<Boolean> =
            dataStore.data
                .map { preferences -> preferences[Keys.IS_DARK_MODE] ?: false }
                .stateIn(scope, SharingStarted.Eagerly, false)

        private val cachedLanguageCode: StateFlow<String?> =
            dataStore.data
                .map { preferences -> preferences[Keys.LANGUAGE_CODE] }
                .stateIn(scope, SharingStarted.Eagerly, null)

        private val cachedIsOnboardingCompleted: StateFlow<Boolean> =
            dataStore.data
                .map { preferences -> preferences[Keys.IS_ONBOARDING_COMPLETED] ?: false }
                .stateIn(scope, SharingStarted.Eagerly, false)

        // Synchronous getters (use cached StateFlow values)
        override fun getAccessToken(): String? = cachedAccessToken.value

        override fun getRefreshToken(): String? = cachedRefreshToken.value

        override fun getTokenType(): String? = cachedTokenType.value

        override fun getUUID(): String? = cachedUUID.value

        override fun hasUser(): Boolean = !cachedAccessToken.value.isNullOrBlank()

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

        override suspend fun setDarkMode(isEnabled: Boolean) {
            dataStore.edit { preferences ->
                preferences[Keys.IS_DARK_MODE] = isEnabled
            }
        }

        override suspend fun setLanguageCode(languageCode: String) {
            dataStore.edit { preferences ->
                preferences[Keys.LANGUAGE_CODE] = languageCode
            }
        }

        override suspend fun setOnboardingCompleted(isCompleted: Boolean) {
            dataStore.edit { preferences ->
                preferences[Keys.IS_ONBOARDING_COMPLETED] = isCompleted
            }
        }

        override suspend fun clearAuth() {
            dataStore.edit { preferences ->
                preferences.remove(Keys.ACCESS_TOKEN)
                preferences.remove(Keys.REFRESH_TOKEN)
                preferences.remove(Keys.TOKEN_TYPE)
                preferences.remove(Keys.UUID)
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

        override val isDarkModeFlow: StateFlow<Boolean> get() = cachedIsDarkMode

        override val languageCodeFlow: StateFlow<String?> get() = cachedLanguageCode

        override val isOnboardingCompletedFlow: StateFlow<Boolean> get() = cachedIsOnboardingCompleted

        private object Keys {
            val ACCESS_TOKEN = stringPreferencesKey("key_access_token")
            val REFRESH_TOKEN = stringPreferencesKey("key_refresh_token")
            val TOKEN_TYPE = stringPreferencesKey("key_token_type")
            val UUID = stringPreferencesKey("key_uuid")
            val IS_DARK_MODE = booleanPreferencesKey("key_is_dark_mode")
            val LANGUAGE_CODE = stringPreferencesKey("key_language_code")
            val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("key_is_onboarding_completed")
        }
    }
