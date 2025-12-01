package com.ytapps.composetemplate.presentation.login

import com.google.common.truth.Truth
import com.ytapps.composetemplate.core.api.Result
import com.ytapps.composetemplate.data.model.AuthRequestModel
import com.ytapps.composetemplate.domain.model.AuthModel
import com.ytapps.composetemplate.domain.usecase.LoginUseCase
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
        val authRequestModel = AuthRequestModel("email", "password")
        val authModel = mockk<AuthModel>()
        val response = Result.Success(authModel)

        // When
        coEvery { loginUseCase.invoke(authRequestModel) } returns response
        viewModel.login(authRequestModel.email, authRequestModel.password)

        // Then
        Truth.assertThat(viewModel.uiState.value.shouldNavigateToSplash).isTrue()
        Truth.assertThat(viewModel.uiState.value.isLoading).isFalse()
        Truth.assertThat(viewModel.uiState.value.error).isNull()
    }

    @Test
    fun `given invalid user credentials when login should set error`() {
        // Given
        val authRequestModel = AuthRequestModel("email", "password")
        val response = Result.Error<AuthModel>("error")

        // When
        coEvery { loginUseCase.invoke(authRequestModel) } returns response
        viewModel.login(authRequestModel.email, authRequestModel.password)

        // Then
        Truth.assertThat(viewModel.uiState.value.error).isEqualTo(response.error)
        Truth.assertThat(viewModel.uiState.value.isLoading).isFalse()
        Truth.assertThat(viewModel.uiState.value.shouldNavigateToSplash).isFalse()
    }
}