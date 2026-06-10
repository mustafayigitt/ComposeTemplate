package com.ytapps.composetemplate.feature.splash.domain

import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class GetStartDestinationUseCaseTest {
    private lateinit var splashRepository: ISplashRepository
    private lateinit var getStartDestinationUseCase: GetStartDestinationUseCase

    @Before
    fun setUp() {
        splashRepository = mockk()
        getStartDestinationUseCase = GetStartDestinationUseCase(splashRepository)
    }

    @Test
    fun `given user has account when invoke then return Home destination`() = runTest {
        coEvery { splashRepository.hasUser() } returns true

        val result = getStartDestinationUseCase()

        Truth.assertThat(result).isEqualTo(SplashDestination.Home)
    }

    @Test
    fun `given user has no account when invoke then return Login destination`() = runTest {
        coEvery { splashRepository.hasUser() } returns false

        val result = getStartDestinationUseCase()

        Truth.assertThat(result).isEqualTo(SplashDestination.Login)
    }
}
