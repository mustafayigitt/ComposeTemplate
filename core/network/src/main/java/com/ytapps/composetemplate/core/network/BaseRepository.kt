package com.ytapps.composetemplate.core.network

import com.ytapps.composetemplate.core.common.Result
import com.ytapps.composetemplate.core.common.Constants
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

abstract class BaseRepository {
    suspend fun <T> safeCall(call: suspend () -> Response<T>): Result<T> =
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
                    401 -> Result.Error("Unauthorized access")
                    403 -> Result.Error("Forbidden access")
                    404 -> Result.Error("Resource not found")
                    in 500..599 -> Result.Error("Server error occurred")
                    else -> Result.Error(errorMsg)
                }
            }
        } catch (e: HttpException) {
            Timber.e(e, "SafeCall failed")
            Result.Error(
                message = e.message ?: Constants.DEFAULT_ERROR,
                throwable = e,
            )
        } catch (e: IOException) {
            Timber.e(e, "SafeCall failed")
            Result.Error(
                message = e.message ?: Constants.DEFAULT_ERROR,
                throwable = e,
            )
        }
}
