package com.ytapps.composetemplate.feature.auth.data.repository

import com.google.common.truth.Truth
import com.ytapps.composetemplate.core.common.Result
import com.ytapps.composetemplate.core.data.IPreferencesManager
import com.ytapps.composetemplate.core.data.security.AuthTokens
import com.ytapps.composetemplate.core.data.security.TokenStore
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
    private lateinit var tokenStore: TokenStore

    @Before
    fun setUp() {
        authService = mockk<AuthService>()
        preferencesManager = mockk<IPreferencesManager>(relaxed = true)
        tokenStore = mockk<TokenStore>(relaxed = true)
        authRepository = AuthRepository(authService, preferencesManager, tokenStore)
    }

    @Test
    fun `given signed user when hasUser() then return true`() {
        every { preferencesManager.hasUser() } returns true

        val result = authRepository.hasUser()

        Truth.assertThat(result).isTrue()
    }

    @Test
    fun `given has not signed user when hasUser() then return false`() {
        every { preferencesManager.hasUser() } returns false

        val result = authRepository.hasUser()

        Truth.assertThat(result).isFalse()
    }

    @Test
    fun `given valid authRequestModel when login then verify tokens are stored atomically`() =
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
            coVerify {
                tokenStore.saveTokens(
                    AuthTokens(
                        accessToken = "token",
                        refreshToken = "refresh",
                        tokenType = "Bearer",
                    ),
                )
            }
        }

    @Test
    fun `given admin admin credentials when login then verify session is created`() =
        runTest {
            val result = authRepository.login("admin", "admin")

            Truth.assertThat(result).isInstanceOf(Result.Success::class.java)
            coVerify { preferencesManager.setUUID("admin-uuid") }
            coVerify {
                tokenStore.saveTokens(
                    AuthTokens(
                        accessToken = "admin-token",
                        refreshToken = "admin-refresh",
                        tokenType = "Bearer",
                    ),
                )
            }
        }

    @Test
    fun `given invalid authRequestModel when login then verify tokens are not stored`() =
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
            coVerify(exactly = 0) { tokenStore.saveTokens(any()) }
        }
}
