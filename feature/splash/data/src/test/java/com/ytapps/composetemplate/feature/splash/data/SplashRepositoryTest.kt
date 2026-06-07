package com.ytapps.composetemplate.feature.splash.data

import com.google.common.truth.Truth
import com.ytapps.composetemplate.core.data.IPreferencesManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
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
    fun `given signed user when hasUser then return true`() {
        every { preferencesManager.hasUser() } returns true

        val result = runBlocking { splashRepository.hasUser() }

        Truth.assertThat(result).isTrue()
    }

    @Test
    fun `given unsigned user when hasUser then return false`() {
        every { preferencesManager.hasUser() } returns false

        val result = runBlocking { splashRepository.hasUser() }

        Truth.assertThat(result).isFalse()
    }
}
