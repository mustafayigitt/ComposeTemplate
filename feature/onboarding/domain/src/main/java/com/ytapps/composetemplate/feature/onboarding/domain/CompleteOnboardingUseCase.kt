package com.ytapps.composetemplate.feature.onboarding.domain

import javax.inject.Inject

class CompleteOnboardingUseCase
    @Inject
    constructor(
        private val onboardingRepository: IOnboardingRepository,
    ) {
        suspend operator fun invoke() {
            onboardingRepository.completeOnboarding()
        }
    }
