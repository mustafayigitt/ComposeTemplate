package com.ytapps.composetemplate.feature.profile.domain

import javax.inject.Inject

class UpdateThemeUseCase
    @Inject
    constructor(
        private val profileRepository: IProfileRepository,
    ) {
        suspend operator fun invoke(isDarkMode: Boolean) {
            profileRepository.setDarkMode(isDarkMode)
        }
    }
