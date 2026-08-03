package com.ytapps.composetemplate.feature.profile.domain

import kotlinx.coroutines.flow.Flow

interface IProfileRepository {
    val isDarkModeFlow: Flow<Boolean>
    val languageCodeFlow: Flow<String>

    suspend fun setDarkMode(isDarkMode: Boolean)

    suspend fun setLanguageCode(languageCode: String)

    suspend fun clearAuth()
}
