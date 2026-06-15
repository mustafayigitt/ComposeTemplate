package com.ytapps.composetemplate.feature.splash.presentation

import com.google.common.truth.Truth.assertThat
import com.ytapps.composetemplate.feature.auth.navigation.LoginRoute
import com.ytapps.composetemplate.feature.home.navigation.HomeRoute
import com.ytapps.composetemplate.feature.splash.domain.GetStartDestinationUseCase
import com.ytapps.composetemplate.feature.splash.domain.SplashDestination
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class SplashViewModelTest {
    private lateinit var getStartDestinationUseCase: GetStartDestinationUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        getStartDestinationUseCase = mockk()
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given user has account then navigate to HomeRoute`() = runTest(testDispatcher) {
        coEvery { getStartDestinationUseCase() } returns SplashDestination.Home

        val viewModel = SplashViewModel(getStartDestinationUseCase)
        viewModel.checkDestination()
        advanceUntilIdle()

        val event = viewModel.events.first()
        assertThat(event).isEqualTo(SplashEvent.NavigateTo(HomeRoute))
        assertThat(viewModel.uiState.value.isLoading).isFalse()
    }

    @Test
    fun `given user has no account then navigate to LoginRoute`() = runTest(testDispatcher) {
        coEvery { getStartDestinationUseCase() } returns SplashDestination.Login

        val viewModel = SplashViewModel(getStartDestinationUseCase)
        viewModel.checkDestination()
        advanceUntilIdle()

        val event = viewModel.events.first()
        assertThat(event).isEqualTo(SplashEvent.NavigateTo(LoginRoute))
        assertThat(viewModel.uiState.value.isLoading).isFalse()
    }
}
