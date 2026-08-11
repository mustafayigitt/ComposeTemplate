package com.ytapps.composetemplate.feature.profile.data

import com.ytapps.composetemplate.core.common.Language
import com.ytapps.composetemplate.core.data.IPreferencesManager
import com.ytapps.composetemplate.core.data.LocaleManager
import com.ytapps.composetemplate.feature.profile.domain.IProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ProfileRepository
    @Inject
    constructor(
        private val preferencesManager: IPreferencesManager,
        private val localeManager: LocaleManager,
    ) : IProfileRepository {
        override val isDarkModeFlow: Flow<Boolean> = preferencesManager.isDarkModeFlow
        override val languageCodeFlow: Flow<String> = preferencesManager.languageCodeFlow.map { it ?: DEFAULT_LANGUAGE_CODE }

        override suspend fun setDarkMode(isDarkMode: Boolean) {
            preferencesManager.setDarkMode(isDarkMode)
        }

        override suspend fun applyLanguage(language: Language) {
            preferencesManager.setLanguageCode(language.code)
            localeManager.applyLanguage(language)
        }

        override suspend fun clearAuth() {
            preferencesManager.clearAuth()
        }

        private companion object {
            const val DEFAULT_LANGUAGE_CODE = "en"
        }
    }
