package com.ytapps.composetemplate.data.repository

import com.google.common.truth.Truth
import com.ytapps.composetemplate.core.api.Result
import com.ytapps.composetemplate.data.local.IPreferencesManager
import com.ytapps.composetemplate.data.model.AuthRequestModel
import com.ytapps.composetemplate.data.model.AuthResponseModel
import com.ytapps.composetemplate.data.remote.AuthService
import com.ytapps.composetemplate.domain.repository.IAuthRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * Created by mustafayigitt on 05/09/2023
 * mustafa.yt65@gmail.com
 */
class AuthRepositoryTest {

    private lateinit var authRepository: IAuthRepository
    private lateinit var authService: AuthService
    private lateinit var preferencesManager: IPreferencesManager

    @Before
    fun setUp() {
        authService = mockk<AuthService>()
        preferencesManager = mockk<IPreferencesManager>()
        authRepository = AuthRepository(authService, preferencesManager)
    }

    @Test
    fun `given signed user when hasUser() then return true`() {
        // Given
        every { preferencesManager.hasUser() } returns true

        // When
        val result = authRepository.hasUser()

        // Then
        Truth.assertThat(result).isTrue()
    }

    @Test
    fun `given has not signed user when hasUser() then return false`() {
        // Given
        every { preferencesManager.hasUser() } returns false

        // When
        val result = authRepository.hasUser()

        // Then
        Truth.assertThat(result).isFalse()
    }

    @Test
    fun `given valid authRequestModel when login() then verify saveCredentials called`() {
        // Given
        val authRequestModel = mockk<AuthRequestModel>()
        val authResponseModel = mockk<AuthResponseModel>()

        // When
        coEvery { authService.login(authRequestModel) } returns Response.success(authResponseModel)
        every { preferencesManager.saveCredentials(authResponseModel) } returns Unit
        val result = runBlocking { authRepository.login(authRequestModel) }

        // Then
        Truth.assertThat(result).isInstanceOf(Result.Success::class.java)
        verify { preferencesManager.saveCredentials(authResponseModel) }
    }

    @Test
    fun `given invalid authRequestModel when login() then verify saveCredentials not called`() {
        // Given
        val authRequestModel = mockk<AuthRequestModel>()
        val authResponseModel = mockk<AuthResponseModel>()

        // When
        coEvery { authService.login(authRequestModel) } returns Response.error(
            400,
            "Bad Request".toResponseBody()
        )
        val result = runBlocking { authRepository.login(authRequestModel) }

        // Then
        Truth.assertThat(result).isInstanceOf(Result.Error::class.java)
        verify(exactly = 0) { preferencesManager.saveCredentials(authResponseModel) }
    }

}