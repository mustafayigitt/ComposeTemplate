package com.ytapps.composetemplate.feature.profile.domain

import javax.inject.Inject

class LogoutUseCase
    @Inject
    constructor(
        private val profileRepository: IProfileRepository,
    ) {
        suspend operator fun invoke() {
            profileRepository.clearAuth()
        }
    }
