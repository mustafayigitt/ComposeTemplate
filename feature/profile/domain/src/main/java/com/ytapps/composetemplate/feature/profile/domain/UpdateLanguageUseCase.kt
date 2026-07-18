package com.ytapps.composetemplate.feature.profile.domain

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.ytapps.composetemplate.core.common.Language
import com.ytapps.composetemplate.core.data.IPreferencesManager
import javax.inject.Inject

class UpdateLanguageUseCase
    @Inject
    constructor(
        private val preferencesManager: IPreferencesManager,
    ) {
        suspend operator fun invoke(language: Language) {
            preferencesManager.setLanguageCode(language.code)

            // Android 13+ and AppCompat backport support
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language.code)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }
