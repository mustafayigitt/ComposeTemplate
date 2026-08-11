package com.ytapps.composetemplate.core.config

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalConfigProvider
    @Inject
    constructor() : IConfigManager {
        private val defaults =
            mapOf(
                ConfigKey.MAINTENANCE_MODE to false,
                ConfigKey.MIN_VERSION to 1L,
                ConfigKey.WELCOME_MESSAGE to "Welcome to ComposeTemplate!",
            )

        override fun getBoolean(key: String): Boolean = (defaults[key] as? Boolean) ?: false

        override fun getString(key: String): String = (defaults[key] as? String) ?: ""

        override fun getLong(key: String): Long = (defaults[key] as? Long) ?: 0L
    }

object ConfigKey {
    const val MAINTENANCE_MODE = "maintenance_mode"
    const val MIN_VERSION = "min_version"
    const val WELCOME_MESSAGE = "welcome_message"
}
