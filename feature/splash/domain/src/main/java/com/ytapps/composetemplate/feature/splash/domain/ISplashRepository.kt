package com.ytapps.composetemplate.feature.splash.domain

interface ISplashRepository {
    suspend fun isOnboardingCompleted(): Boolean
}
