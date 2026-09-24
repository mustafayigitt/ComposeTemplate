package com.ytapps.composetemplate.feature.splash.domain

import javax.inject.Inject

class GetStartDestinationUseCase
    @Inject
    constructor(
        private val splashRepository: ISplashRepository,
    ) {
        suspend operator fun invoke(): SplashDestination =
            if (splashRepository.isOnboardingCompleted()) {
                SplashDestination.Home
            } else {
                SplashDestination.Onboarding
            }
    }
