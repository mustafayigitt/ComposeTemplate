package com.ytapps.composetemplate.domain.mapper

import com.ytapps.composetemplate.core.base.IMapper
import com.ytapps.composetemplate.data.model.AuthResponseModel
import com.ytapps.composetemplate.domain.model.AuthModel
import javax.inject.Inject

class AuthMapper @Inject constructor() : IMapper<AuthResponseModel, AuthModel> {
    override fun map(input: AuthResponseModel): AuthModel {
        require(input.accessToken.isNotEmpty()) {
            throw IllegalArgumentException("AuthResponseModel.accessToken is not valid")
        }

        require(input.refreshToken.isNotEmpty()) {
            throw IllegalArgumentException("AuthResponseModel.refreshToken is not valid")
        }

        require(input.expiresIn.isNotEmpty()) {
            throw IllegalArgumentException("AuthResponseModel.expiresIn is not valid")
        }

        require(input.tokenType.isNotEmpty()) {
            throw IllegalArgumentException("AuthResponseModel.tokenType is not valid")
        }

        return AuthModel(
            accessToken = input.accessToken,
            refreshToken = input.refreshToken,
            expiresIn = input.expiresIn,
            tokenType = input.tokenType
        )
    }
}