package com.lhacenmed.budget.feature.auth.domain.usecase

import com.google.common.truth.Truth
import com.lhacenmed.budget.core.api.Result
import com.lhacenmed.budget.feature.auth.domain.IAuthRepository
import com.lhacenmed.budget.feature.auth.domain.LoginUseCase
import com.lhacenmed.budget.feature.auth.domain.model.AuthModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

/**
 * Created by mustafayigitt on 06/09/2023
 * mustafa.yt65@gmail.com
 */
internal class LoginUseCaseTest {

    private lateinit var autRepository: IAuthRepository
    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setUp() {
        autRepository = mockk()
        loginUseCase = LoginUseCase(autRepository)
    }

    @Test
    fun `given Result-Success AuthRequestModel when LoginUseCase() return Result-Success`() {
        // Given
        val email = "email"
        val password = "password"
        val response = Result.Success(mockk<AuthModel>())

        coEvery { autRepository.login(email, password) } returns response

        // When
        val result = runBlocking { loginUseCase(email, password) }

        // Then
        Truth.assertThat(result).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun `given Result-Error AuthRequestModel when LoginUseCase() return Result-Error`() {
        // Given
        val response: Result<AuthModel> = Result.Error("Login Failed")

        coEvery { autRepository.login(any(), any()) } returns response

        // When
        val result = runBlocking { loginUseCase("email", "password") }

        // Then
        Truth.assertThat(result).isInstanceOf(Result.Error::class.java)
    }
}