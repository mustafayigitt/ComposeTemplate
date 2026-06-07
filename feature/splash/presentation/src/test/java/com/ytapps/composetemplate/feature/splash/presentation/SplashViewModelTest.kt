package com.ytapps.composetemplate.feature.splash.presentation

import com.google.common.truth.Truth
import com.ytapps.composetemplate.feature.auth.navigation.LoginRoute
import com.ytapps.composetemplate.feature.home.navigation.HomeRoute
import com.ytapps.composetemplate.feature.splash.domain.GetStartDestinationUseCase
import com.ytapps.composetemplate.feature.splash.domain.SplashDestination
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class SplashViewModelTest {
    private lateinit var getStartDestinationUseCase: GetStartDestinationUseCase
    private lateinit var viewModel: SplashViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    @OptIn(ExperimentalCoroutinesApi::class)
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getStartDestinationUseCase = mockk()
    }

    @After
    @OptIn(ExperimentalCoroutinesApi::class)
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given user has account when splash then navigate to Home`() =
        runTest(testDispatcher) {
            coEvery { getStartDestinationUseCase() } returns SplashDestination.Home
            viewModel = SplashViewModel(getStartDestinationUseCase)
            advanceUntilIdle()

            Truth.assertThat(viewModel.uiState.value.destinationRoute).isEqualTo(HomeRoute)
            Truth.assertThat(viewModel.uiState.value.isLoading).isFalse()
        }

    @Test
    fun `given user has no account when splash then navigate to Login`() =
        runTest(testDispatcher) {
            coEvery { getStartDestinationUseCase() } returns SplashDestination.Login
            viewModel = SplashViewModel(getStartDestinationUseCase)
            advanceUntilIdle()

            Truth.assertThat(viewModel.uiState.value.destinationRoute).isEqualTo(LoginRoute)
            Truth.assertThat(viewModel.uiState.value.isLoading).isFalse()
        }
}
