package com.ytapps.composetemplate.core.data.initializer

import android.app.Application
import com.ytapps.composetemplate.core.common.initializer.AppInitializer
import com.ytapps.composetemplate.core.data.LocaleManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Restores the saved application language at startup.
 *
 * This was a `LaunchedEffect` in `MainActivity`, which meant `:app` had to inject `LocaleManager`.
 * As an [AppInitializer] the concern stays inside `core:data` and the Activity keeps only
 * navigation dependencies.
 */
@Singleton
class LocaleInitializer
    @Inject
    constructor(
        private val localeManager: LocaleManager,
    ) : AppInitializer {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

        override fun init(app: Application) {
            scope.launch { localeManager.awaitAndRestoreSavedLanguage() }
        }
    }
