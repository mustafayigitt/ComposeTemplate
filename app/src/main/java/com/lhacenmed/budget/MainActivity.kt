package com.lhacenmed.budget

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lhacenmed.budget.ui.common.PredictiveExitHandler
import com.lhacenmed.budget.ui.common.SettingsProvider
import com.lhacenmed.budget.ui.common.motion.materialSharedAxisZIn
import com.lhacenmed.budget.ui.common.motion.materialSharedAxisZOut
import com.lhacenmed.budget.ui.page.AppEntry
import com.lhacenmed.budget.ui.page.auth.AuthEntry
import com.lhacenmed.budget.ui.theme.BudgetTheme
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var supabase: SupabaseClient

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SettingsProvider {
                BudgetTheme {
                    PredictiveExitHandler(onExit = { finish() }) {
                        val sessionStatus by supabase.auth.sessionStatus
                            .collectAsStateWithLifecycle(initialValue = SessionStatus.Initializing)

                        AnimatedContent(
                            targetState    = sessionStatus,
                            label          = "auth_gate",
                            transitionSpec = {
                                materialSharedAxisZIn(forward = true) togetherWith
                                        materialSharedAxisZOut(forward = true)
                            },
                        ) { status ->
                            when {
                                // Session loaded from local storage or freshly authenticated
                                status is SessionStatus.Authenticated ->
                                    AppEntry()

                                // Explicit sign-out — user intentionally logged out
                                status is SessionStatus.NotAuthenticated && status.isSignOut ->
                                    AuthEntry()

                                // Initializing OR network error OR token couldn't refresh offline
                                // → stay on loading screen; the session will resolve once
                                //   connectivity returns or alwaysAutoRefresh kicks in
                                else ->
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        LoadingIndicator()
                                    }
                            }
                        }
                    }
                }
            }
        }
    }
}
