package com.lhacenmed.budget.feature.auth.data.remote

import com.lhacenmed.budget.feature.auth.data.model.AuthRequestModel
import com.lhacenmed.budget.feature.auth.data.model.AuthResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

internal interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body authRequestModel: AuthRequestModel): Response<AuthResponseModel>
}