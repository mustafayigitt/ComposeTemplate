package com.ytapps.composetemplate.core.data.security

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class TokenStoreContractTest {
    private lateinit var tokenStore: FakeTokenStore

    @Before
    fun setup() {
        tokenStore = FakeTokenStore()
    }

    @Test
    fun `saveTokens updates tokensFlow atomically`() =
        runTest {
            val tokens =
                AuthTokens(
                    accessToken = "test_access_token_123",
                    refreshToken = "test_refresh_token_456",
                    tokenType = "Bearer",
                )

            tokenStore.saveTokens(tokens)

            assertThat(tokenStore.tokensFlow.first()).isEqualTo(tokens)
            assertThat(tokenStore.getTokens()).isEqualTo(tokens)
            assertThat(tokenStore.getAccessToken()).isEqualTo(tokens.accessToken)
            assertThat(tokenStore.getRefreshToken()).isEqualTo(tokens.refreshToken)
            assertThat(tokenStore.getTokenType()).isEqualTo(tokens.tokenType)
        }

    @Test
    fun `getTokenType returns Bearer by default`() {
        assertThat(tokenStore.getTokenType()).isEqualTo(AuthTokens.DEFAULT_TOKEN_TYPE)
    }

    @Test
    fun `clear removes all tokens`() =
        runTest {
            tokenStore.saveTokens(
                AuthTokens(
                    accessToken = "token",
                    refreshToken = "refresh",
                    tokenType = "Bearer",
                ),
            )

            tokenStore.clear()

            assertThat(tokenStore.getTokens()).isNull()
            assertThat(tokenStore.getAccessToken()).isNull()
            assertThat(tokenStore.getRefreshToken()).isNull()
            assertThat(tokenStore.getTokenType()).isEqualTo(AuthTokens.DEFAULT_TOKEN_TYPE)
        }
}

private class FakeTokenStore : TokenStore {
    private val tokens = MutableStateFlow<AuthTokens?>(null)

    override fun getTokens(): AuthTokens? = tokens.value

    override suspend fun saveTokens(tokens: AuthTokens) {
        this.tokens.value = tokens
    }

    override suspend fun clear() {
        tokens.value = null
    }

    override val tokensFlow: Flow<AuthTokens?> = tokens
}
