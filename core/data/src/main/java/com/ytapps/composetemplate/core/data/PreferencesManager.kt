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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences",
)

@Singleton
class PreferencesManager
    @Inject
    constructor(
        @ApplicationContext private val appContext: Context,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : IPreferencesManager {
        private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
        private val dataStore = appContext.dataStore

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

        override fun getUUID(): String? = cachedUUID.value

        override fun hasUser(): Boolean = cachedUUID.value != null

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

        override suspend fun clear() {
            dataStore.edit { preferences ->
                preferences.clear()
            }
        }

        override val uuidFlow: StateFlow<String?> get() = cachedUUID

        override val isDarkModeFlow: StateFlow<Boolean> get() = cachedIsDarkMode

        override val languageCodeFlow: StateFlow<String?> get() = cachedLanguageCode

        override val isOnboardingCompletedFlow: StateFlow<Boolean> get() = cachedIsOnboardingCompleted

        private object Keys {
            val UUID = stringPreferencesKey("key_uuid")
            val IS_DARK_MODE = booleanPreferencesKey("key_is_dark_mode")
            val LANGUAGE_CODE = stringPreferencesKey("key_language_code")
            val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("key_is_onboarding_completed")
        }
    }
