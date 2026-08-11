package com.ytapps.composetemplate.feature.profile.domain

import com.ytapps.composetemplate.core.common.Language
import kotlinx.coroutines.flow.Flow

interface IProfileRepository {
    val isDarkModeFlow: Flow<Boolean>
    val languageCodeFlow: Flow<String>

    suspend fun setDarkMode(isDarkMode: Boolean)

    suspend fun applyLanguage(language: Language)

    suspend fun clearAuth()
}
