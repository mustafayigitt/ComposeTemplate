package com.ytapps.composetemplate.core.data

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for managing non-sensitive user preferences with DataStore.
 */
interface IPreferencesManager {
    fun getUUID(): String?

    fun hasUser(): Boolean

    suspend fun setUUID(uuid: String)

    suspend fun clear()

    val uuidFlow: StateFlow<String?>
    val isDarkModeFlow: StateFlow<Boolean>
    val languageCodeFlow: StateFlow<String?>
    val isOnboardingCompletedFlow: StateFlow<Boolean>

    suspend fun setDarkMode(isEnabled: Boolean)

    suspend fun setLanguageCode(languageCode: String)

    suspend fun setOnboardingCompleted(isCompleted: Boolean)
}
