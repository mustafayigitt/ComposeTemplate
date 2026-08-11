package com.ytapps.composetemplate.core.network

import com.ytapps.composetemplate.core.common.Constants
import com.ytapps.composetemplate.core.common.Result
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.net.HttpURLConnection

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
                    HttpURLConnection.HTTP_UNAUTHORIZED -> Result.Error("Unauthorized access")
                    HttpURLConnection.HTTP_FORBIDDEN -> Result.Error("Forbidden access")
                    HttpURLConnection.HTTP_NOT_FOUND -> Result.Error("Resource not found")
                    in SERVER_ERROR_START..SERVER_ERROR_END -> Result.Error("Server error occurred")
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

    companion object {
        private const val SERVER_ERROR_START = 500
        private const val SERVER_ERROR_END = 599
    }
}
