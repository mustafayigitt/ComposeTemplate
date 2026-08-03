package com.ytapps.composetemplate.feature.profile.domain

import com.ytapps.composetemplate.core.common.Language
import javax.inject.Inject

class UpdateLanguageUseCase
    @Inject
    constructor(
        private val profileRepository: IProfileRepository,
    ) {
        suspend operator fun invoke(language: Language) {
            profileRepository.setLanguageCode(language.code)
        }
    }
