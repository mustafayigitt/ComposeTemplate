package com.ytapps.composetemplate.core.data

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.ytapps.composetemplate.core.common.Language
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocaleManager
    @Inject
    constructor(
        private val preferencesManager: IPreferencesManager,
    ) {
        fun applyLanguage(language: Language) {
            val localeList = LocaleListCompat.forLanguageTags(language.code)
            AppCompatDelegate.setApplicationLocales(localeList)
        }

        fun restoreSavedLanguage() {
            val savedCode = preferencesManager.languageCodeFlow.value
            if (savedCode != null) {
                val language = Language.fromCode(savedCode)
                applyLanguage(language)
            }
        }
    }
