package com.ytapps.composetemplate.domain.mapper

import com.google.common.truth.Truth
import com.ytapps.composetemplate.data.model.AuthResponseModel
import org.junit.Before
import org.junit.Test

/**
 * Created by mustafa.yigit on 06/09/2023
 * mustafa.yt65@gmail.com
 */
class AuthMapperTest {

    private lateinit var authMapper: AuthMapper

    @Before
    fun setUp() {
        authMapper = AuthMapper()
    }

    @Test
    fun `given valid AuthResponseModel when map return valid AuthModel`() {
        // Given
        val authResponseModel = AuthResponseModel(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            expiresIn = "1000",
            tokenType = "tokenType"
        )

        // When
        val authModel = authMapper.map(authResponseModel)

        // Then
        Truth.assertThat(authModel.accessToken).isEqualTo(authResponseModel.accessToken)
        Truth.assertThat(authModel.refreshToken).isEqualTo(authResponseModel.refreshToken)
        Truth.assertThat(authModel.expiresIn).isEqualTo(authResponseModel.expiresIn)
        Truth.assertThat(authModel.tokenType).isEqualTo(authResponseModel.tokenType)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `given invalid AuthResponseModel when map throw IllegalArgumentException`() {
        // Given
        val authResponseModel = AuthResponseModel(
            accessToken = "",
            refreshToken = "",
            expiresIn = "",
            tokenType = ""
        )

        // When
        authMapper.map(authResponseModel)

        // Then
        // IllegalArgumentException is thrown
    }
}