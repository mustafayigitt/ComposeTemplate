package com.ytapps.composetemplate.core.network

import com.google.common.truth.Truth.assertThat
import com.ytapps.composetemplate.core.common.Result
import com.ytapps.composetemplate.core.data.IPreferencesManager
import dagger.Lazy
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test

internal class TokenAuthenticatorTest {
    private lateinit var preferencesManager: IPreferencesManager
    private lateinit var tokenRefresher: ITokenRefresher
    private lateinit var authenticator: TokenAuthenticator

    @Before
    fun setUp() {
        preferencesManager = mockk()
        tokenRefresher = mockk()
        val lazyTokenRefresher = Lazy { tokenRefresher }
        authenticator = TokenAuthenticator(preferencesManager, lazyTokenRefresher)
    }

    @Test
    fun `given protected request returns unauthorized when refresh succeeds then retries with new token`() =
        runTest {
            every { preferencesManager.getAccessToken() } returns "old-token"
            every { preferencesManager.getTokenType() } returns "Bearer"
            coEvery { tokenRefresher.refreshToken() } returns Result.Success("new-token")

            val result = authenticator.authenticate(null, unauthorizedResponse("https://example.com/protected"))

            assertThat(result?.header("Authorization")).isEqualTo("Bearer new-token")
        }

    @Test
    fun `given refresh request returns unauthorized then does not retry`() =
        runTest {
            val result = authenticator.authenticate(null, unauthorizedResponse("https://example.com/auth/refresh"))

            assertThat(result).isNull()
        }

    private fun unauthorizedResponse(url: String): Response =
        Response
            .Builder()
            .request(Request.Builder().url(url).build())
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .body("".toResponseBody())
            .build()
}
