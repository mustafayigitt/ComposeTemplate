package com.ytapps.composetemplate.feature.profile.domain

import com.ytapps.composetemplate.core.common.Language
import com.ytapps.composetemplate.core.data.IPreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLanguageUseCase
    @Inject
    constructor(
        private val preferencesManager: IPreferencesManager,
    ) {
        operator fun invoke(): Flow<Language> =
            preferencesManager.languageCodeFlow.map { code ->
                Language.fromCode(code)
            }
    }
