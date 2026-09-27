package com.ytapps.composetemplate.core.network

import com.google.common.truth.Truth.assertThat
import com.ytapps.composetemplate.core.common.Result
import kotlinx.coroutines.CancellationException
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
    fun `successful response returns Success`() =
        runTest {
            val result = repository.safeCall { Response.success("test_data") }

            assertThat(result).isInstanceOf(Result.Success::class.java)
            assertThat((result as Result.Success).data).isEqualTo("test_data")
        }

    @Test
    fun `successful response with null body returns Error`() =
        runTest {
            val response: Response<String?> = Response.success(null)

            val result = repository.safeCall { response }

            assertThat(result).isInstanceOf(Result.Error::class.java)
            assertThat((result as Result.Error).message).isEqualTo("Empty response body")
        }

    @Test
    fun `known HTTP status codes are mapped`() =
        runTest {
            assertErrorMessage(401, "Unauthorized access")
            assertErrorMessage(403, "Forbidden access")
            assertErrorMessage(404, "Resource not found")
            assertErrorMessage(500, "Server error occurred")
        }

    @Test
    fun `IOException returns Error`() =
        runTest {
            val result = repository.safeCall<String> { throw IOException("Network error") }

            assertThat(result).isInstanceOf(Result.Error::class.java)
            assertThat((result as Result.Error).message).isEqualTo("Network error")
        }

    @Test
    fun `HttpException returns Error`() =
        runTest {
            val response = errorResponse(400, "bad request")

            val result = repository.safeCall<String> { throw HttpException(response) }

            assertThat(result).isInstanceOf(Result.Error::class.java)
        }

    @Test(expected = CancellationException::class)
    fun `cancellation is never converted to a domain error`() =
        runTest {
            repository.safeCall<String> { throw CancellationException("cancelled") }
        }

    private suspend fun assertErrorMessage(
        code: Int,
        expected: String,
    ) {
        val result = repository.safeCall { errorResponse(code, "{}") }

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo(expected)
    }

    private fun errorResponse(
        code: Int,
        body: String,
    ): Response<String> =
        Response.error(
            code,
            body.toResponseBody("application/json".toMediaType()),
        )
}
