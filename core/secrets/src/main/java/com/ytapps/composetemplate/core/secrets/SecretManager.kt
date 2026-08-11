package com.ytapps.composetemplate.core.secrets

import android.content.Context
import com.ytapps.composetemplate.core.secrets.BuildConfig

/**
 * Manager for retrieving secrets from the NDK layer.
 */
object SecretManager {
    private var appContext: Context? = null

    init {
        if (BuildConfig.NATIVE_SECRETS_ENABLED) {
            System.loadLibrary("native-lib")
        }
    }

    /**
     * Initializes the SecretManager with the application context.
     * This must be called once at app startup.
     */
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    private fun getSafeContext(): Context =
        checkNotNull(appContext) {
            "SecretManager must be initialized before use"
        }

    /**
     * Returns the API key based on the build type.
     */
    fun getApiKey(): String {
        val apiKey =
            if (BuildConfig.NATIVE_SECRETS_ENABLED) {
                getApiKeyNative(getSafeContext(), BuildConfig.DEBUG, BuildConfig.K_PART)
            } else {
                if (BuildConfig.DEBUG) BuildConfig.API_KEY_DEBUG else BuildConfig.API_KEY_RELEASE
            }
        return apiKey.requireAuthorizedSecret("API key")
    }

    /**
     * Returns the Base URL based on the build type.
     */
    fun getBaseUrl(): String {
        val baseUrl =
            if (BuildConfig.NATIVE_SECRETS_ENABLED) {
                getBaseUrlNative(getSafeContext(), BuildConfig.DEBUG, BuildConfig.K_PART)
            } else {
                if (BuildConfig.DEBUG) BuildConfig.BASE_URL_DEBUG else BuildConfig.BASE_URL_RELEASE
            }
        return baseUrl
            .requireAuthorizedSecret("Base URL")
            .also { requireValidBaseUrl(it) }
    }

    fun getExpectedSignatureHash(): String = BuildConfig.EXPECTED_SIGNATURE_HASH

    fun isNativeRuntimeChecksEnabled(): Boolean = BuildConfig.NATIVE_RUNTIME_CHECKS_ENABLED

    fun isCertificatePinningEnabled(): Boolean = BuildConfig.CERTIFICATE_PINNING_ENABLED

    fun getCertificatePins(): List<String> =
        BuildConfig.CERTIFICATE_PINS
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }

    private external fun getApiKeyNative(
        context: Context,
        isDebug: Boolean,
        runtimeMask: String,
    ): String

    private external fun getBaseUrlNative(
        context: Context,
        isDebug: Boolean,
        runtimeMask: String,
    ): String
}

private fun String.requireAuthorizedSecret(label: String): String {
    if (isBlank() || this == "UNAUTHORIZED_ACCESS") {
        throw SecretAccessException("$label is unavailable. Device or app integrity checks failed.")
    }
    return this
}

private fun requireValidBaseUrl(baseUrl: String) {
    require(baseUrl.startsWith("https://") || BuildConfig.DEBUG && baseUrl.startsWith("http://")) {
        "Base URL must use HTTPS outside debug builds."
    }
    require(baseUrl.endsWith("/")) {
        "Base URL must end with '/'. Retrofit requires a trailing slash."
    }
}
