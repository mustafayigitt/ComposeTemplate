package com.ytapps.composetemplate.core.config

/**
 * Interface for retrieving remote configuration values.
 */
interface IConfigManager {
    fun getBoolean(key: String): Boolean

    fun getString(key: String): String

    fun getLong(key: String): Long
}
