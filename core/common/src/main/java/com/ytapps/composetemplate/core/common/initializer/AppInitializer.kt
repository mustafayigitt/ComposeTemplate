package com.ytapps.composetemplate.core.common.initializer

import android.app.Application

/**
 * Contract for startup work contributed by an optional module.
 *
 * A module contributes its own startup logic with `@Binds @IntoSet` instead of being
 * called explicitly from the application class. The application class only iterates the
 * injected set, so deleting a contributing module removes its startup work without any
 * edit in `:app`.
 *
 * Implementations must be side-effect free when their module is absent, and must not
 * assume any other initializer already ran unless they declare a higher [order].
 */
interface AppInitializer {
    /**
     * Lower values run first. Only use an explicit value when the initializer depends on
     * another one having already run.
     */
    val order: Int
        get() = ORDER_DEFAULT

    fun init(app: Application)

    companion object {
        /** Secrets must be ready before anything reads a secret-backed value. */
        const val ORDER_SECRETS = 0

        /** Hardening reads secret-backed flags, so it runs after secrets. */
        const val ORDER_SECURITY = 50

        /** Default slot for initializers without ordering requirements. */
        const val ORDER_DEFAULT = 100
    }
}
