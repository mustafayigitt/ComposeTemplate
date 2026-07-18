package com.ytapps.composetemplate.feature.profile.domain

import com.ytapps.composetemplate.core.data.IPreferencesManager
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetThemeUseCase
    @Inject
    constructor(
        private val preferencesManager: IPreferencesManager,
    ) {
        val isDarkModeFlow: StateFlow<Boolean> = preferencesManager.isDarkModeFlow
    }
