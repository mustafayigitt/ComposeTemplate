package com.ytapps.composetemplate.feature.profile.domain

import com.ytapps.composetemplate.core.common.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLanguageUseCase
    @Inject
    constructor(
        private val profileRepository: IProfileRepository,
    ) {
        operator fun invoke(): Flow<Language> =
            profileRepository.languageCodeFlow.map { code ->
                Language.fromCode(code)
            }
    }
