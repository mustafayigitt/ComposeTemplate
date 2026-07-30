package com.ytapps.composetemplate.core.data.security

import com.ytapps.composetemplate.core.data.IPreferencesManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Clears all local session state in one place.
 *
 * Token storage is intentionally separate from regular preferences, so callers
 * should use this class for logout/session reset instead of clearing only user
 * preferences.
 */
@Singleton
class SessionManager
    @Inject
    constructor(
        private val preferencesManager: IPreferencesManager,
        private val tokenStore: TokenStore,
    ) {
        suspend fun clearSession() {
            tokenStore.clear()
            preferencesManager.clear()
        }
    }
