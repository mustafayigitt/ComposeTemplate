package com.ytapps.composetemplate.feature.splash.data

import com.google.common.truth.Truth
import com.ytapps.composetemplate.core.data.IPreferencesManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class SplashRepositoryTest {
    private lateinit var preferencesManager: IPreferencesManager
    private lateinit var splashRepository: SplashRepository

    @Before
    fun setUp() {
        preferencesManager = mockk<IPreferencesManager>(relaxed = true)
        splashRepository = SplashRepository(preferencesManager)
    }

    @Test
    fun `given onboarding completed when queried then return true`() =
        runTest {
            every { preferencesManager.isOnboardingCompletedFlow } returns MutableStateFlow(true)

            val result = splashRepository.isOnboardingCompleted()

            Truth.assertThat(result).isTrue()
        }

    @Test
    fun `given onboarding not completed when queried then return false`() =
        runTest {
            every { preferencesManager.isOnboardingCompletedFlow } returns MutableStateFlow(false)

            val result = splashRepository.isOnboardingCompleted()

            Truth.assertThat(result).isFalse()
        }
}
