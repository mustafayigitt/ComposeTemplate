package com.ytapps.composetemplate.feature.auth.presentation

import com.google.common.truth.Truth.assertThat
import com.ytapps.composetemplate.core.common.Result
import com.ytapps.composetemplate.feature.auth.domain.LoginUseCase
import com.ytapps.composetemplate.feature.auth.domain.model.AuthModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
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
internal class LoginViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        loginUseCase = mockk()
        viewModel = LoginViewModel(loginUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given login is called when collect state then isLoading is true during operation`() =
        runTest(testDispatcher) {
            val deferred = CompletableDeferred<Result<AuthModel>>()
            coEvery { loginUseCase.invoke(any(), any()) } coAnswers { deferred.await() }

            viewModel.login("email", "password")

            assertThat(viewModel.uiState.value.isLoading).isTrue()

            deferred.complete(Result.Error("error"))
            advanceUntilIdle()

            assertThat(viewModel.uiState.value.isLoading).isFalse()
        }

    @Test
    fun `given valid user credentials when login should send NavigateTo event`() =
        runTest(testDispatcher) {
            val (email, password) = "email" to "password"
            val authModel = mockk<AuthModel>()
            val response = Result.Success(authModel)

            coEvery { loginUseCase.invoke(email, password) } returns response
            viewModel.login("email", "password")
            advanceUntilIdle()

            val event = viewModel.events.first()
            assertThat(event).isEqualTo(LoginEvent.NavigateToSplash)
            assertThat(viewModel.uiState.value.isLoading).isFalse()
        }

    @Test
    fun `given invalid user credentials when login should send error event`() =
        runTest(testDispatcher) {
            val response: Result<AuthModel> = Result.Error("error")

            coEvery { loginUseCase.invoke(any(), any()) } returns response
            viewModel.login("email", "password")
            advanceUntilIdle()

            val event = viewModel.events.first()
            assertThat(event).isEqualTo(LoginEvent.ShowSnackbar("error"))
        }
}
