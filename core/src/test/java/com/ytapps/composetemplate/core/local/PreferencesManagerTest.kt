package com.ytapps.composetemplate.core.local

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for IPreferencesManager interface contract.
 * Using a fake implementation to verify the interface behavior.
 */
class PreferencesManagerContractTest {
    private lateinit var preferencesManager: FakePreferencesManager

    @Before
    fun setup() {
        preferencesManager = FakePreferencesManager()
    }

    @Test
    fun `setAccessToken updates accessTokenFlow`() =
        runTest {
            // Given
            val token = "test_access_token_123"

            // When
            preferencesManager.setAccessToken(token)

            // Then
            assertThat(preferencesManager.accessTokenFlow.first()).isEqualTo(token)
            assertThat(preferencesManager.getAccessToken()).isEqualTo(token)
        }

    @Test
    fun `setRefreshToken updates refreshTokenFlow`() =
        runTest {
            // Given
            val refreshToken = "test_refresh_token_456"

            // When
            preferencesManager.setRefreshToken(refreshToken)

            // Then
            assertThat(preferencesManager.refreshTokenFlow.first()).isEqualTo(refreshToken)
            assertThat(preferencesManager.getRefreshToken()).isEqualTo(refreshToken)
        }

    @Test
    fun `setTokenType updates tokenTypeFlow`() =
        runTest {
            // Given
            val tokenType = "Bearer"

            // When
            preferencesManager.setTokenType(tokenType)

            // Then
            assertThat(preferencesManager.tokenTypeFlow.first()).isEqualTo(tokenType)
            assertThat(preferencesManager.getTokenType()).isEqualTo(tokenType)
        }

    @Test
    fun `setUUID updates uuidFlow and hasUser returns true`() =
        runTest {
            // Given - initially no user
            assertThat(preferencesManager.hasUser()).isFalse()

            // When
            preferencesManager.setUUID("user-uuid-789")

            // Then
            assertThat(preferencesManager.uuidFlow.first()).isEqualTo("user-uuid-789")
            assertThat(preferencesManager.getUUID()).isEqualTo("user-uuid-789")
            assertThat(preferencesManager.hasUser()).isTrue()
        }

    @Test
    fun `clear removes all data`() =
        runTest {
            // Given - set some data
            preferencesManager.setAccessToken("token")
            preferencesManager.setRefreshToken("refresh")
            preferencesManager.setTokenType("Bearer")
            preferencesManager.setUUID("uuid")

            // Verify data was set
            assertThat(preferencesManager.getAccessToken()).isEqualTo("token")
            assertThat(preferencesManager.hasUser()).isTrue()

            // When
            preferencesManager.clear()

            // Then - all should be null
            assertThat(preferencesManager.getAccessToken()).isNull()
            assertThat(preferencesManager.getRefreshToken()).isNull()
            assertThat(preferencesManager.getTokenType()).isNull()
            assertThat(preferencesManager.getUUID()).isNull()
            assertThat(preferencesManager.hasUser()).isFalse()
        }

    @Test
    fun `synchronous getters return cached values`() =
        runTest {
            // Given
            preferencesManager.setAccessToken("cached_token")

            // Then - sync getter should return the same value
            assertThat(preferencesManager.getAccessToken()).isEqualTo("cached_token")
        }
}

/**
 * Fake implementation for testing the interface contract
 */
class FakePreferencesManager : IPreferencesManager {
    private val backingAccessToken = MutableStateFlow<String?>(null)
    private val backingRefreshToken = MutableStateFlow<String?>(null)
    private val backingTokenType = MutableStateFlow<String?>(null)
    private val backingUuid = MutableStateFlow<String?>(null)

    override fun getAccessToken(): String? = backingAccessToken.value

    override fun getRefreshToken(): String? = backingRefreshToken.value

    override fun getTokenType(): String? = backingTokenType.value

    override fun getUUID(): String? = backingUuid.value

    override fun hasUser(): Boolean = backingUuid.value != null

    override suspend fun setAccessToken(accessToken: String) {
        backingAccessToken.value = accessToken
    }

    override suspend fun setRefreshToken(refreshToken: String) {
        backingRefreshToken.value = refreshToken
    }

    override suspend fun setTokenType(tokenType: String) {
        backingTokenType.value = tokenType
    }

    override suspend fun setUUID(uuid: String) {
        backingUuid.value = uuid
    }

    override suspend fun clear() {
        backingAccessToken.value = null
        backingRefreshToken.value = null
        backingTokenType.value = null
        backingUuid.value = null
    }

    override val accessTokenFlow: Flow<String?> = backingAccessToken
    override val refreshTokenFlow: Flow<String?> = backingRefreshToken
    override val tokenTypeFlow: Flow<String?> = backingTokenType
    override val uuidFlow: Flow<String?> = backingUuid
}
