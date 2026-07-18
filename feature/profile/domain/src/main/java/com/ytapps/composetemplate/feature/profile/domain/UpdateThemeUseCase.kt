package com.ytapps.composetemplate.feature.profile.domain

import com.ytapps.composetemplate.core.data.IPreferencesManager
import javax.inject.Inject

class UpdateThemeUseCase
    @Inject
    constructor(
        private val preferencesManager: IPreferencesManager,
    ) {
        suspend operator fun invoke(isDarkMode: Boolean) {
            preferencesManager.setDarkMode(isDarkMode)
        }
    }
