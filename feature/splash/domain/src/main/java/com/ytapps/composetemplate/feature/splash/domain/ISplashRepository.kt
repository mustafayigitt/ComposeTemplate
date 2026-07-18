package com.ytapps.composetemplate.feature.splash.domain

interface ISplashRepository {
    suspend fun hasUser(): Boolean

    suspend fun isOnboardingCompleted(): Boolean
}
