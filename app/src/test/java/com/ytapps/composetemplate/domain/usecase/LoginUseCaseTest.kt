package com.ytapps.composetemplate.domain.usecase

import com.google.common.truth.Truth
import com.ytapps.composetemplate.core.api.Result
import com.ytapps.composetemplate.data.model.AuthRequestModel
import com.ytapps.composetemplate.data.model.AuthResponseModel
import com.ytapps.composetemplate.domain.mapper.AuthMapper
import com.ytapps.composetemplate.domain.repository.IAuthRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

/**
 * Created by mustafa.yigit on 06/09/2023
 * mustafa.yt65@gmail.com
 */
class LoginUseCaseTest {

    private lateinit var autRepository: IAuthRepository
    private lateinit var authMapper: AuthMapper
    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setUp() {
        autRepository = mockk()
        authMapper = mockk()
        loginUseCase = LoginUseCase(autRepository, authMapper)
    }

    @Test
    fun `given Result-Success AuthRequestModel when LoginUseCase() return Result-Success`() {
        // Given
        val authRequestModel = mockk<AuthRequestModel>()
        val authResponseModel = mockk<AuthResponseModel>()
        val response = Result.Success(authResponseModel)

        every { authMapper.map(authResponseModel) } returns mockk()
        coEvery { autRepository.login(authRequestModel) } returns response

        // When
        val result = runBlocking { loginUseCase(authRequestModel) }

        // Then
        Truth.assertThat(result).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun `given Result-Error AuthRequestModel when LoginUseCase() return Result-Error`() {
        // Given
        val authRequestModel = mockk<AuthRequestModel>()
        val response = Result.Error<AuthResponseModel>("Login Failed")

        coEvery { autRepository.login(authRequestModel) } returns response

        // When
        val result = runBlocking { loginUseCase(authRequestModel) }

        // Then
        Truth.assertThat(result).isInstanceOf(Result.Error::class.java)
    }
}