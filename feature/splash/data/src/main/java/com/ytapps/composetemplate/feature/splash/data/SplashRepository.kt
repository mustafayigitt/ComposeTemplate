package com.ytapps.composetemplate.feature.splash.data

import com.ytapps.composetemplate.core.data.IPreferencesManager
import com.ytapps.composetemplate.feature.splash.domain.ISplashRepository
import javax.inject.Inject

internal class SplashRepository
    @Inject
    constructor(
        private val preferencesManager: IPreferencesManager,
    ) : ISplashRepository {
        override suspend fun hasUser(): Boolean = preferencesManager.hasUser()
    }
