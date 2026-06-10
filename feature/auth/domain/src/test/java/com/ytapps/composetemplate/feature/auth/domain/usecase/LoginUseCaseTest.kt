package com.ytapps.composetemplate.feature.auth.domain.usecase

import com.google.common.truth.Truth
import com.ytapps.composetemplate.core.common.Result
import com.ytapps.composetemplate.feature.auth.domain.IAuthRepository
import com.ytapps.composetemplate.feature.auth.domain.LoginUseCase
import com.ytapps.composetemplate.feature.auth.domain.model.AuthModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class LoginUseCaseTest {
    private lateinit var authRepository: IAuthRepository
    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setUp() {
        authRepository = mockk()
        loginUseCase = LoginUseCase(authRepository)
    }

    @Test
    fun `given Result-Success AuthRequestModel when LoginUseCase() return Result-Success`() = runTest {
        val email = "email"
        val password = "password"
        val response = Result.Success(mockk<AuthModel>())

        coEvery { authRepository.login(email, password) } returns response

        val result = loginUseCase(email, password)

        Truth.assertThat(result).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun `given Result-Error AuthRequestModel when LoginUseCase() return Result-Error`() = runTest {
        val response: Result<AuthModel> = Result.Error("Login Failed")

        coEvery { authRepository.login(any(), any()) } returns response

        val result = loginUseCase("email", "password")

        Truth.assertThat(result).isInstanceOf(Result.Error::class.java)
    }
}
