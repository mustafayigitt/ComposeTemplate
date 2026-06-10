package com.ytapps.composetemplate.core.network

import com.google.common.truth.Truth.assertThat
import com.ytapps.composetemplate.core.common.Result
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class BaseRepositoryTest {

    private val repository = object : BaseRepository() {}

    @Test
    fun `given successful response with body then return Success with data`() = runTest {
        val response = Response.success("test_data")

        val result = repository.safeCall { response }

        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo("test_data")
    }

    @Test
    fun `given successful response with null body then return Error`() = runTest {
        val response: Response<String?> = Response.success(null)

        val result = repository.safeCall { response }

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo("Empty response body")
    }

    @Test
    fun `given 401 response then return Unauthorized error`() = runTest {
        val response = Response.error<String>(
            401,
            "{}".toResponseBody("application/json".toMediaType()),
        )

        val result = repository.safeCall { response }

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo("Unauthorized access")
    }

    @Test
    fun `given 403 response then return Forbidden error`() = runTest {
        val response = Response.error<String>(
            403,
            "{}".toResponseBody("application/json".toMediaType()),
        )

        val result = repository.safeCall { response }

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo("Forbidden access")
    }

    @Test
    fun `given 404 response then return Resource not found error`() = runTest {
        val response = Response.error<String>(
            404,
            "{}".toResponseBody("application/json".toMediaType()),
        )

        val result = repository.safeCall { response }

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo("Resource not found")
    }

    @Test
    fun `given 500 response then return Server error`() = runTest {
        val response = Response.error<String>(
            500,
            "{}".toResponseBody("application/json".toMediaType()),
        )

        val result = repository.safeCall { response }

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo("Server error occurred")
    }

    @Test
    fun `given IOException when calling API then return Error`() = runTest {
        val result = repository.safeCall<String> {
            throw IOException("Network error")
        }

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo("Network error")
    }

    @Test
    fun `given HttpException when calling API then return Error`() = runTest {
        val errorResponse = Response.error<String>(
            400,
            "bad request".toResponseBody("application/json".toMediaType()),
        )

        val result = repository.safeCall<String> {
            throw HttpException(errorResponse)
        }

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `given custom error body then return error with body message`() = runTest {
        val response = Response.error<String>(
            422,
            """{"error":"Invalid input"}""".toResponseBody("application/json".toMediaType()),
        )

        val result = repository.safeCall { response }

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).contains("Invalid input")
    }
}
