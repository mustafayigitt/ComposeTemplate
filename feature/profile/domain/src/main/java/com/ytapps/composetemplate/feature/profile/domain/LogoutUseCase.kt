package com.ytapps.composetemplate.feature.profile.domain

import com.ytapps.composetemplate.core.data.IPreferencesManager
import javax.inject.Inject

class LogoutUseCase
    @Inject
    constructor(
        private val preferencesManager: IPreferencesManager,
    ) {
        suspend operator fun invoke() {
            preferencesManager.clearAuth()
        }
    }
