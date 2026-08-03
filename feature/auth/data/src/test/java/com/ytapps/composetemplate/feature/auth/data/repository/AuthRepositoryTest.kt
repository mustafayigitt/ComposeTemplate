package com.ytapps.composetemplate.feature.auth.data.repository

import com.google.common.truth.Truth
import com.ytapps.composetemplate.core.common.Result
import com.ytapps.composetemplate.core.data.IPreferencesManager
import com.ytapps.composetemplate.feature.auth.data.AuthRepository
import com.ytapps.composetemplate.feature.auth.data.model.AuthRequestModel
import com.ytapps.composetemplate.feature.auth.data.model.AuthResponseModel
import com.ytapps.composetemplate.feature.auth.data.remote.AuthService
import com.ytapps.composetemplate.feature.auth.domain.IAuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response

internal class AuthRepositoryTest {
    private lateinit var authRepository: IAuthRepository
    private lateinit var authService: AuthService
    private lateinit var preferencesManager: IPreferencesManager

    @Before
    fun setUp() {
        authService = mockk<AuthService>()
        preferencesManager = mockk<IPreferencesManager>(relaxed = true)
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
    fun `given valid authRequestModel when login() then verify setAccessToken called`() =
        runTest {
            val authRequestModel =
                AuthRequestModel(
                    email = "email",
                    password = "password",
                )
            val authResponseModel =
                AuthResponseModel(
                    accessToken = "token",
                    refreshToken = "refresh",
                    tokenType = "Bearer",
                    expiresIn = "3600",
                )

            coEvery { authService.login(authRequestModel) } returns Response.success(authResponseModel)
            val result = authRepository.login("email", "password")

            Truth.assertThat(result).isInstanceOf(Result.Success::class.java)
            coVerify { preferencesManager.setAccessToken("token") }
            coVerify { preferencesManager.setRefreshToken("refresh") }
            coVerify { preferencesManager.setTokenType("Bearer") }
        }

    @Test
    fun `given admin credentials when login then calls auth service`() =
        runTest {
            val authRequestModel =
                AuthRequestModel(
                    email = "admin",
                    password = "admin",
                )
            val authResponseModel =
                AuthResponseModel(
                    accessToken = "token",
                    refreshToken = "refresh",
                    tokenType = "Bearer",
                    expiresIn = "3600",
                )

            coEvery { authService.login(authRequestModel) } returns Response.success(authResponseModel)

            val result = authRepository.login("admin", "admin")

            Truth.assertThat(result).isInstanceOf(Result.Success::class.java)
            coVerify { authService.login(authRequestModel) }
            coVerify(exactly = 0) { preferencesManager.setUUID(any()) }
            coVerify { preferencesManager.setAccessToken("token") }
            coVerify { preferencesManager.setRefreshToken("refresh") }
            coVerify { preferencesManager.setTokenType("Bearer") }
        }

    @Test
    fun `given invalid authRequestModel when login() then verify setAccessToken not called`() =
        runTest {
            val authRequestModel =
                AuthRequestModel(
                    email = "email",
                    password = "password",
                )

            coEvery { authService.login(authRequestModel) } returns
                Response.error(
                    400,
                    "Bad Request".toResponseBody(),
                )
            val result = authRepository.login("email", "password")

            Truth.assertThat(result).isInstanceOf(Result.Error::class.java)
            coVerify(exactly = 0) { preferencesManager.setAccessToken(any()) }
        }
}
