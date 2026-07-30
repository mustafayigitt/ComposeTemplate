package com.ytapps.composetemplate.core.security

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
    fun `setAccessToken updates accessTokenFlow`() = runTest {
        val token = "test_access_token_123"

        tokenStore.setAccessToken(token)

        assertThat(tokenStore.accessTokenFlow.first()).isEqualTo(token)
        assertThat(tokenStore.getAccessToken()).isEqualTo(token)
    }

    @Test
    fun `setRefreshToken updates refreshTokenFlow`() = runTest {
        val refreshToken = "test_refresh_token_456"

        tokenStore.setRefreshToken(refreshToken)

        assertThat(tokenStore.refreshTokenFlow.first()).isEqualTo(refreshToken)
        assertThat(tokenStore.getRefreshToken()).isEqualTo(refreshToken)
    }

    @Test
    fun `clear removes all tokens`() = runTest {
        tokenStore.setAccessToken("token")
        tokenStore.setRefreshToken("refresh")
        tokenStore.setTokenType("Bearer")

        tokenStore.clear()

        assertThat(tokenStore.getAccessToken()).isNull()
        assertThat(tokenStore.getRefreshToken()).isNull()
        assertThat(tokenStore.getTokenType()).isNull()
    }
}

private class FakeTokenStore : TokenStore {
    private val _accessToken = MutableStateFlow<String?>(null)
    private val _refreshToken = MutableStateFlow<String?>(null)
    private val _tokenType = MutableStateFlow<String?>(null)

    override fun getAccessToken(): String? = _accessToken.value
    override fun getRefreshToken(): String? = _refreshToken.value
    override fun getTokenType(): String? = _tokenType.value

    override suspend fun setAccessToken(accessToken: String) {
        _accessToken.value = accessToken
    }

    override suspend fun setRefreshToken(refreshToken: String) {
        _refreshToken.value = refreshToken
    }

    override suspend fun setTokenType(tokenType: String) {
        _tokenType.value = tokenType
    }

    override suspend fun clear() {
        _accessToken.value = null
        _refreshToken.value = null
        _tokenType.value = null
    }

    override val accessTokenFlow: Flow<String?> = _accessToken
    override val refreshTokenFlow: Flow<String?> = _refreshToken
    override val tokenTypeFlow: Flow<String?> = _tokenType
}
