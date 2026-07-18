package com.ytapps.composetemplate.feature.splash.data

import com.ytapps.composetemplate.core.data.IPreferencesManager
import com.ytapps.composetemplate.feature.splash.domain.ISplashRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class SplashRepository
    @Inject
    constructor(
        private val preferencesManager: IPreferencesManager,
    ) : ISplashRepository {
        override suspend fun hasUser(): Boolean = preferencesManager.hasUser()

        override suspend fun isOnboardingCompleted(): Boolean = preferencesManager.isOnboardingCompletedFlow.first()
    }
