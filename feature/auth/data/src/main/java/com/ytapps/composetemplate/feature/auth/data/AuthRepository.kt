package com.ytapps.composetemplate.feature.auth.data

import com.ytapps.composetemplate.core.api.Result
import com.ytapps.composetemplate.core.api.map
import com.ytapps.composetemplate.core.base.BaseRepository
import com.ytapps.composetemplate.core.local.IPreferencesManager
import com.ytapps.composetemplate.core.security.AuthTokens
import com.ytapps.composetemplate.core.security.TokenStore
import com.ytapps.composetemplate.feature.auth.data.model.AuthRequestModel
import com.ytapps.composetemplate.feature.auth.data.remote.AuthService
import com.ytapps.composetemplate.feature.auth.domain.IAuthRepository
import com.ytapps.composetemplate.feature.auth.domain.model.AuthModel
import retrofit2.Response
import javax.inject.Inject

internal class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val prefs: IPreferencesManager,
    private val tokenStore: TokenStore
) : BaseRepository(), IAuthRepository {

    override fun hasUser(): Boolean {
        return prefs.hasUser()
    }

    override suspend fun login(email: String, password: String): Result<AuthModel> {
        val result = safeCall(
            call = {
                val requestModel = AuthRequestModel(
                    email = email,
                    password = password
                )
                authService.login(requestModel)
            }
        ).map {
            AuthModel(
                accessToken = it.accessToken,
                refreshToken = it.refreshToken,
                expiresIn = it.expiresIn,
                tokenType = it.tokenType
            )
        }
        if (result is Result.Success) {
            val data = result.data
            tokenStore.saveTokens(
                AuthTokens(
                    accessToken = data.accessToken,
                    refreshToken = data.refreshToken,
                    tokenType = data.tokenType
                )
            )
        }
        return result
    }

    override suspend fun refreshTokens(): Result<AuthTokens> {
        return safeCall {
            // TODO: Replace this placeholder with the app's real refresh-token endpoint.
            // The endpoint should return a complete token set so TokenAuthenticator can
            // persist it atomically before retrying the failed request.
            Response.success(
                AuthTokens(
                    accessToken = "",
                    refreshToken = "",
                    tokenType = AuthTokens.DEFAULT_TOKEN_TYPE
                )
            )
        }
    }
}
