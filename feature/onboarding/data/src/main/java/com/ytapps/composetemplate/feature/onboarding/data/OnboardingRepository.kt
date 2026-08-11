package com.ytapps.composetemplate.feature.onboarding.data

import com.ytapps.composetemplate.core.data.IPreferencesManager
import com.ytapps.composetemplate.feature.onboarding.domain.IOnboardingRepository
import javax.inject.Inject

internal class OnboardingRepository
    @Inject
    constructor(
        private val preferencesManager: IPreferencesManager,
    ) : IOnboardingRepository {
        override suspend fun completeOnboarding() {
            preferencesManager.setOnboardingCompleted(true)
        }
    }
