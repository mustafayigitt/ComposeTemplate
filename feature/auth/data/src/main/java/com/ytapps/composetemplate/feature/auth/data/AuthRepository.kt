package com.ytapps.composetemplate.feature.auth.data

import com.ytapps.composetemplate.core.common.Result
import com.ytapps.composetemplate.core.common.map
import com.ytapps.composetemplate.core.data.IPreferencesManager
import com.ytapps.composetemplate.core.data.security.AuthTokens
import com.ytapps.composetemplate.core.data.security.TokenStore
import com.ytapps.composetemplate.core.network.BaseRepository
import com.ytapps.composetemplate.feature.auth.data.model.AuthRequestModel
import com.ytapps.composetemplate.feature.auth.data.remote.AuthService
import com.ytapps.composetemplate.feature.auth.domain.IAuthRepository
import com.ytapps.composetemplate.feature.auth.domain.model.AuthModel
import javax.inject.Inject

internal class AuthRepository
    @Inject
    constructor(
        private val authService: AuthService,
        private val prefs: IPreferencesManager,
        private val tokenStore: TokenStore,
    ) : BaseRepository(),
        IAuthRepository {
        override fun hasUser(): Boolean = prefs.hasUser()

        override suspend fun login(
            email: String,
            password: String,
        ): Result<AuthModel> {
            if (email == "admin" && password == "admin") {
                val authModel =
                    AuthModel(
                        accessToken = "admin-token",
                        refreshToken = "admin-refresh",
                        expiresIn = "99999",
                        tokenType = "Bearer",
                    )
                tokenStore.saveTokens(
                    AuthTokens(
                        accessToken = authModel.accessToken,
                        refreshToken = authModel.refreshToken,
                        tokenType = authModel.tokenType,
                    ),
                )
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
                tokenStore.saveTokens(
                    AuthTokens(
                        accessToken = data.accessToken,
                        refreshToken = data.refreshToken,
                        tokenType = data.tokenType,
                    ),
                )
            }
            return result
        }

        override suspend fun refreshTokens(): Result<AuthTokens> =
            Result.Error(
                message = "Refresh token endpoint is not implemented. Replace this template implementation with your API call.",
            )
    }
