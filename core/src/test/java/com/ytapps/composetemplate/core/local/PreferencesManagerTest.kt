package com.ytapps.composetemplate.core.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat

/**
 * Unit tests for IPreferencesManager interface contract.
 * Using a fake implementation to verify the interface behavior.
 */
class PreferencesManagerContractTest {

    private lateinit var preferencesManager: FakePreferencesManager

    @Before
    fun setup() {
        preferencesManager = FakePreferencesManager()
    }

    @Test
    fun `setUUID updates uuidFlow and hasUser returns true`() = runTest {
        // Given - initially no user
        assertThat(preferencesManager.hasUser()).isFalse()
        
        // When
        preferencesManager.setUUID("user-uuid-789")
        
        // Then
        assertThat(preferencesManager.uuidFlow.first()).isEqualTo("user-uuid-789")
        assertThat(preferencesManager.getUUID()).isEqualTo("user-uuid-789")
        assertThat(preferencesManager.hasUser()).isTrue()
    }

    @Test
    fun `clear removes all data`() = runTest {
        // Given - set some data
        preferencesManager.setUUID("uuid")
        
        // Verify data was set
        assertThat(preferencesManager.hasUser()).isTrue()
        
        // When
        preferencesManager.clear()
        
        // Then - all should be null
        assertThat(preferencesManager.getUUID()).isNull()
        assertThat(preferencesManager.hasUser()).isFalse()
    }
}

/**
 * Fake implementation for testing the interface contract
 */
class FakePreferencesManager : IPreferencesManager {
    
    private val _uuid = MutableStateFlow<String?>(null)

    override fun getUUID(): String? = _uuid.value
    override fun hasUser(): Boolean = _uuid.value != null

    override suspend fun setUUID(uuid: String) {
        _uuid.value = uuid
    }

    override suspend fun clear() {
        _uuid.value = null
    }

    override val uuidFlow: Flow<String?> = _uuid
}
