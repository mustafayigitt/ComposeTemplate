package com.ytapps.composetemplate.core.data

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.ytapps.composetemplate.core.common.Language
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
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

        /**
         * Reads whatever is cached right now. The DataStore-backed [StateFlow] starts at `null`
         * and is filled asynchronously, so this is a best-effort call and does nothing when it
         * runs before the first emission. Prefer [awaitAndRestoreSavedLanguage] at startup.
         */
        fun restoreSavedLanguage() {
            val savedCode = preferencesManager.languageCodeFlow.value
            if (savedCode != null) {
                val language = Language.fromCode(savedCode)
                applyLanguage(language)
            }
        }

        /**
         * Suspends until a language has actually been persisted, then applies it. If the user has
         * never chosen a language this never resumes, which is the correct behaviour: there is
         * nothing to restore and the system locale stays in charge.
         *
         * Must be called from the main dispatcher, because it ends in
         * `AppCompatDelegate.setApplicationLocales`.
         */
        suspend fun awaitAndRestoreSavedLanguage() {
            val savedCode = preferencesManager.languageCodeFlow.filterNotNull().first()
            applyLanguage(Language.fromCode(savedCode))
        }
    }
