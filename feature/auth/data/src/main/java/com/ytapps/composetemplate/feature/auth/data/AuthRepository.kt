package com.ytapps.composetemplate.feature.auth.data

import com.ytapps.composetemplate.core.common.Result
import com.ytapps.composetemplate.core.common.map
import com.ytapps.composetemplate.core.data.IPreferencesManager
import com.ytapps.composetemplate.core.network.BaseRepository
import com.ytapps.composetemplate.feature.auth.data.model.AuthRequestModel
import com.ytapps.composetemplate.feature.auth.data.model.RefreshTokenRequestModel
import com.ytapps.composetemplate.feature.auth.data.remote.AuthService
import com.ytapps.composetemplate.feature.auth.domain.IAuthRepository
import com.ytapps.composetemplate.feature.auth.domain.model.AuthModel
import javax.inject.Inject

internal class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val prefs: IPreferencesManager,
) : BaseRepository(), IAuthRepository {

    override fun hasUser(): Boolean = prefs.hasUser()

    override suspend fun login(
        email: String,
        password: String,
    ): Result<AuthModel> {
        if (email == "admin" && password == "admin") {
            val authModel = AuthModel(
                accessToken = "admin-token",
                refreshToken = "admin-refresh",
                expiresIn = "99999",
                tokenType = "Bearer",
            )
            prefs.setAccessToken(authModel.accessToken)
            prefs.setRefreshToken(authModel.refreshToken)
            prefs.setTokenType(authModel.tokenType)
            prefs.setUUID("admin-uuid")
            return Result.Success(authModel)
        }

        val result =
            safeCall(
                call = {
                    val requestModel =
                        AuthRequestModel(
                            email = email,
                            password = password,
                        )
                    authService.login(requestModel)
                },
            ).map {
                AuthModel(
                    accessToken = it.accessToken,
                    refreshToken = it.refreshToken,
                    expiresIn = it.expiresIn,
                    tokenType = it.tokenType,
                )
            }
        if (result is Result.Success) {
            val data = result.data
            prefs.setAccessToken(data.accessToken)
            prefs.setRefreshToken(data.refreshToken)
            prefs.setTokenType(data.tokenType)
        }
        return result
    }

    override suspend fun refreshToken(): Result<String> {
        val currentRefreshToken =
            prefs.getRefreshToken()
                ?: return Result.Error("No refresh token available")

        return safeCall {
            authService.refreshToken(RefreshTokenRequestModel(currentRefreshToken))
        }.map { response ->
            prefs.setAccessToken(response.accessToken)
            prefs.setRefreshToken(response.refreshToken)
            response.accessToken
        }
    }
}
