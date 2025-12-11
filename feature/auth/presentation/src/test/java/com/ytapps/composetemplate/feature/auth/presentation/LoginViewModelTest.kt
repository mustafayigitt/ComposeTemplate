package com.ytapps.composetemplate.feature.auth.presentation

import com.google.common.truth.Truth
import com.ytapps.composetemplate.core.api.Result
import com.ytapps.composetemplate.feature.auth.domain.LoginUseCase
import com.ytapps.composetemplate.feature.auth.domain.model.AuthModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by mustafayigitt on 06/09/2023
 * mustafa.yt65@gmail.com
 */
class LoginViewModelTest {

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var viewModel: LoginViewModel

    @Before
    @OptIn(ExperimentalCoroutinesApi::class)
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        loginUseCase = mockk()
        viewModel = LoginViewModel(loginUseCase)
    }

    @After
    @OptIn(ExperimentalCoroutinesApi::class)
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given valid user credentials when login should set shouldNavigationSplash true`() {
        // Given
        val (email, password) = "email" to "password"
        val authModel = mockk<AuthModel>()
        val response = Result.Success(authModel)

        // When
        coEvery { loginUseCase.invoke(email, password) } returns response
        viewModel.login("email", "password")

        // Then
        Truth.assertThat(viewModel.uiState.value.shouldNavigateToSplash).isTrue()
        Truth.assertThat(viewModel.uiState.value.isLoading).isFalse()
        Truth.assertThat(viewModel.uiState.value.error).isNull()
    }

    @Test
    fun `given invalid user credentials when login should set error`() {
        // Given
        val response: Result<AuthModel> = Result.Error("error")

        // When
        coEvery { loginUseCase.invoke(any(), any()) } returns response
        viewModel.login("email", "password")

        // Then
        Truth.assertThat(viewModel.uiState.value.error)
            .isEqualTo((response as Result.Error).message)
        Truth.assertThat(viewModel.uiState.value.isLoading).isFalse()
        Truth.assertThat(viewModel.uiState.value.shouldNavigateToSplash).isFalse()
    }
}