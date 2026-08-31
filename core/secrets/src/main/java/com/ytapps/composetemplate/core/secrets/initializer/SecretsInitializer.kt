package com.ytapps.composetemplate.core.secrets.initializer

import android.app.Application
import com.ytapps.composetemplate.core.common.initializer.AppInitializer
import com.ytapps.composetemplate.core.secrets.SecretManager
import javax.inject.Inject

/**
 * Binds the application context into [SecretManager] at startup.
 */
internal class SecretsInitializer
    @Inject
    constructor() : AppInitializer {
        override val order: Int = AppInitializer.ORDER_SECRETS

        override fun init(app: Application) {
            SecretManager.initialize(app)
        }
    }
