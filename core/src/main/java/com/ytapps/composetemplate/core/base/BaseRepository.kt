package com.ytapps.composetemplate.core.base

import com.ytapps.composetemplate.core.api.Result
import com.ytapps.composetemplate.core.util.Constants
import retrofit2.Response
import timber.log.Timber

abstract class BaseRepository {
    protected suspend fun <T : Any> safeCall(call: suspend () -> Response<T>): Result<T> =
        try {
            val response = call.invoke()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error(message = "Empty response body")
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: Constants.DEFAULT_ERROR
                when (response.code()) {
                    401 -> Result.Error("Unauthorized access", null)
                    403 -> Result.Error("Forbidden access", null)
                    404 -> Result.Error("Resource not found", null)
                    in 500..599 -> Result.Error("Server error occurred", null)
                    else -> Result.Error(errorMsg)
                }
            }
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            Timber.e(e, "SafeCall failed")
            Result.Error(
                message = e.message ?: Constants.DEFAULT_ERROR,
                throwable = e,
            )
        }
}
