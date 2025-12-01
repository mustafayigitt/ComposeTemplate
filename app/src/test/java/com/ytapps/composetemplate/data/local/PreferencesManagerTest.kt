package com.ytapps.composetemplate.data.local

import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test

/**
 * Created by mustafayigitt on 05/09/2023
 * mustafa.yt65@gmail.com
 */
class PreferencesManagerTest {

    private lateinit var preferencesManager: IPreferencesManager

    @Before
    fun setUp() {
        preferencesManager = MockPreferencesManager()
    }

    @Test
    fun `given while user signed when hasUser() then return true`() {
        // Given
        preferencesManager.setUUID("123")

        // When
        val result = preferencesManager.hasUser()

        // Then
        Truth.assertThat(result).isTrue()
    }

    @Test
    fun `given while user not signed when hasUser() then return false`() {
        // Given
        preferencesManager.clear()

        // When
        val result = preferencesManager.hasUser()

        // Then
        Truth.assertThat(result).isFalse()
    }

    @Test
    fun `given valid string when setAccessToken() then return given string`() {
        // Given
        val expectedAccessToken = "123"

        // When
        preferencesManager.setAccessToken(expectedAccessToken)

        // Then
        val result = preferencesManager.getAccessToken()
        Truth.assertThat(result).isEqualTo(expectedAccessToken)
    }

    @Test
    fun `given valid string when setRefreshToken() then return given string`() {
        // Given
        val expectedRefreshToken = "123"

        // When
        preferencesManager.setRefreshToken(expectedRefreshToken)

        // Then
        val result = preferencesManager.getRefreshToken()
        Truth.assertThat(result).isEqualTo(expectedRefreshToken)
    }

    @Test
    fun `given valid string when setTokenType() then return given string`() {
        // Given
        val expectedTokenType = "123"

        // When
        preferencesManager.setTokenType(expectedTokenType)

        // Then
        val result = preferencesManager.getTokenType()
        Truth.assertThat(result).isEqualTo(expectedTokenType)
    }

    @Test
    fun `given valid string when setUUID() then return given string`() {
        // Given
        val expectedUUID = "123"

        // When
        preferencesManager.setUUID(expectedUUID)

        // Then
        val result = preferencesManager.getUUID()
        Truth.assertThat(result).isEqualTo(expectedUUID)
    }

    @Test
    fun `given no-data when clear() then return any pref value null`() {
        // Given

        // When
        preferencesManager.clear()

        // Then
        val resultAccessToken = preferencesManager.getAccessToken()
        val resultRefreshToken = preferencesManager.getRefreshToken()
        val resultTokenType = preferencesManager.getTokenType()
        val resultUUID = preferencesManager.getUUID()

        Truth.assertThat(resultAccessToken).isNull()
        Truth.assertThat(resultRefreshToken).isNull()
        Truth.assertThat(resultTokenType).isNull()
        Truth.assertThat(resultUUID).isNull()
    }
}