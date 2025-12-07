package com.ytapps.composetemplate.core.base

import com.google.common.truth.Truth.assertThat
import com.ytapps.composetemplate.core.api.Result
import com.ytapps.composetemplate.core.api.getOrNull
import com.ytapps.composetemplate.core.api.isError
import com.ytapps.composetemplate.core.api.isSuccess
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.http.GET

/**
 * Tests for BaseRepository's safeCall functionality.
 * Uses MockK to mock service and creates real repository with mocked dependencies.
 * 
 * Created by mustafayigitt on 05/09/2023
 * mustafa.yt65@gmail.com
 */
class BaseRepositoryTest {

    private lateinit var mockService: TestService
    private lateinit var repository: TestableRepository

    @Before
    fun setUp() {
        mockService = mockk()
        repository = TestableRepository(mockService)
    }

    @Test
    fun `given successful response when repository makes call then returns Result-Success with data`() = runTest {
        // Given
        val expectedData = "success_data"
        coEvery { mockService.getData() } returns Response.success(expectedData)
        
        // When
        val result = repository.fetchData()
        
        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat(result.getOrNull()).isEqualTo(expectedData)
    }

    @Test
    fun `given error response when repository makes call then returns Result-Error`() = runTest {
        // Given
        val errorBody = "Bad Request".toResponseBody("application/json".toMediaType())
        coEvery { mockService.getData() } returns Response.error(400, errorBody)
        
        // When
        val result = repository.fetchData()
        
        // Then
        assertThat(result.isError).isTrue()
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `given exception thrown when repository makes call then returns Result-Error with exception`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        coEvery { mockService.getData() } throws exception
        
        // When
        val result = repository.fetchData()
        
        // Then
        assertThat(result.isError).isTrue()
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = result as Result.Error
        assertThat(error.message).isEqualTo("Network error")
        assertThat(error.throwable).isEqualTo(exception)
    }

    @Test
    fun `given null body in success response when repository makes call then returns Result-Error`() = runTest {
        // Given
        coEvery { mockService.getData() } returns Response.success(null)
        
        // When
        val result = repository.fetchData()
        
        // Then
        assertThat(result.isError).isTrue()
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    // Test Service Interface
    interface TestService {
        @GET("test")
        suspend fun getData(): Response<String>
    }

    // Concrete Repository for testing with mocked service
    class TestableRepository(
        private val service: TestService
    ) : BaseRepository() {
        
        suspend fun fetchData(): Result<String> = safeCall {
            service.getData()
        }
    }
}