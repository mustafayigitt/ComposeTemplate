package com.ytapps.composetemplate.core.data.security

import com.ytapps.composetemplate.core.data.IPreferencesManager
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SessionManagerTest {
    private lateinit var preferencesManager: IPreferencesManager
    private lateinit var tokenStore: TokenStore
    private lateinit var sessionManager: SessionManager

    @Before
    fun setup() {
        preferencesManager = mockk(relaxed = true)
        tokenStore = mockk(relaxed = true)
        sessionManager = SessionManager(preferencesManager, tokenStore)
    }

    @Test
    fun `clearSession clears tokens and preferences`() =
        runTest {
            sessionManager.clearSession()

            coVerify { tokenStore.clear() }
            coVerify { preferencesManager.clear() }
        }
}
