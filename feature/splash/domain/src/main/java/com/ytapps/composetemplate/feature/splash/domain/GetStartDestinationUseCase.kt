package com.ytapps.composetemplate.feature.splash.domain

import javax.inject.Inject

class GetStartDestinationUseCase
    @Inject
    constructor(
        private val splashRepository: ISplashRepository,
    ) {
        suspend operator fun invoke(): SplashDestination {
            val hasUser = splashRepository.hasUser()
            return if (hasUser) SplashDestination.Home else SplashDestination.Login
        }
    }
