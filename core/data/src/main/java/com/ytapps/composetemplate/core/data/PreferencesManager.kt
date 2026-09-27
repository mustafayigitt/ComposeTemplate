package com.ytapps.composetemplate.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ytapps.composetemplate.core.common.IThemeManager
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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
@Suppress("TooManyFunctions")
class PreferencesManager
    @Inject
    constructor(
        @ApplicationContext private val appContext: Context,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : IPreferencesManager,
        IThemeManager {
        private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
        private val dataStore = appContext.dataStore

        private val cachedAccessToken = dataStore.data.map { it[Keys.ACCESS_TOKEN] }.stateIn(scope, SharingStarted.Eagerly, null)
        private val cachedRefreshToken = dataStore.data.map { it[Keys.REFRESH_TOKEN] }.stateIn(scope, SharingStarted.Eagerly, null)
        private val cachedTokenType = dataStore.data.map { it[Keys.TOKEN_TYPE] }.stateIn(scope, SharingStarted.Eagerly, null)
        private val cachedUUID = dataStore.data.map { it[Keys.UUID] }.stateIn(scope, SharingStarted.Eagerly, null)
        private val cachedIsDarkMode = dataStore.data.map { it[Keys.IS_DARK_MODE] ?: false }.stateIn(scope, SharingStarted.Eagerly, false)
        private val cachedLanguageCode = dataStore.data.map { it[Keys.LANGUAGE_CODE] }.stateIn(scope, SharingStarted.Eagerly, null)
        private val cachedIsOnboardingCompleted =
            dataStore.data.map { it[Keys.IS_ONBOARDING_COMPLETED] ?: false }.stateIn(scope, SharingStarted.Eagerly, false)

        override fun getAccessToken(): String? = cachedAccessToken.value
        override fun getRefreshToken(): String? = cachedRefreshToken.value
        override fun getTokenType(): String? = cachedTokenType.value
        override fun getUUID(): String? = cachedUUID.value
        override fun hasUser(): Boolean = !cachedAccessToken.value.isNullOrBlank()

        override suspend fun setAccessToken(accessToken: String) = dataStore.edit { it[Keys.ACCESS_TOKEN] = accessToken }.let { Unit }
        override suspend fun setRefreshToken(refreshToken: String) = dataStore.edit { it[Keys.REFRESH_TOKEN] = refreshToken }.let { Unit }
        override suspend fun setTokenType(tokenType: String) = dataStore.edit { it[Keys.TOKEN_TYPE] = tokenType }.let { Unit }
        override suspend fun setUUID(uuid: String) = dataStore.edit { it[Keys.UUID] = uuid }.let { Unit }
        override suspend fun setDarkMode(isEnabled: Boolean) = dataStore.edit { it[Keys.IS_DARK_MODE] = isEnabled }.let { Unit }
        override suspend fun setLanguageCode(languageCode: String) = dataStore.edit { it[Keys.LANGUAGE_CODE] = languageCode }.let { Unit }
        override suspend fun setOnboardingCompleted(isCompleted: Boolean) =
            dataStore.edit { it[Keys.IS_ONBOARDING_COMPLETED] = isCompleted }.let { Unit }

        override suspend fun clearAuth() {
            dataStore.edit {
                it.remove(Keys.ACCESS_TOKEN)
                it.remove(Keys.REFRESH_TOKEN)
                it.remove(Keys.TOKEN_TYPE)
                it.remove(Keys.UUID)
            }
        }

        override suspend fun clear() = dataStore.edit { it.clear() }.let { Unit }

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
