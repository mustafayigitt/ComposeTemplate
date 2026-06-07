package com.ytapps.composetemplate.core.base

import com.ytapps.composetemplate.core.api.Result
import com.ytapps.composetemplate.core.util.Constants
import retrofit2.Response

abstract class BaseRepository {
    protected suspend fun <T : Any> safeCall(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call.invoke()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error(message = "Empty response body")
                }
            } else {
                Result.Error(
                    message = response.errorBody()?.string() ?: Constants.DEFAULT_ERROR,
                )
            }
        } catch (e: Exception) {
            Result.Error(
                message = e.message ?: Constants.DEFAULT_ERROR,
                throwable = e,
            )
        }
    }
}
