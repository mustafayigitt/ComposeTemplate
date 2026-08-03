package com.ytapps.composetemplate.feature.profile.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeUseCase
    @Inject
    constructor(
        private val profileRepository: IProfileRepository,
    ) {
        val isDarkModeFlow: Flow<Boolean> = profileRepository.isDarkModeFlow
    }
