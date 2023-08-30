package com.ytapps.composetemplate.domain.mapper

import com.ytapps.composetemplate.core.base.IMapper
import com.ytapps.composetemplate.data.model.AuthResponseModel
import com.ytapps.composetemplate.domain.model.AuthModel
import javax.inject.Inject

class AuthMapper @Inject constructor(): IMapper<AuthResponseModel, AuthModel> {
    override fun map(input: AuthResponseModel): AuthModel {
        return AuthModel(
            accessToken = input.accessToken,
            refreshToken = input.refreshToken,
            expiresIn = input.expiresIn,
            tokenType = input.tokenType
        )
    }
}