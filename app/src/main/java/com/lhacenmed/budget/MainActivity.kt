package com.lhacenmed.budget

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lhacenmed.budget.ui.common.PredictiveExitHandler
import com.lhacenmed.budget.ui.common.SettingsProvider
import com.lhacenmed.budget.ui.page.AppEntry
import com.lhacenmed.budget.ui.page.auth.AuthEntry
import com.lhacenmed.budget.ui.theme.BudgetTheme
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import javax.inject.Inject

private enum class AuthGate { Loading, App, Auth }

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

                        val gate = when (sessionStatus) {
                            is SessionStatus.Initializing     -> AuthGate.Loading
                            is SessionStatus.Authenticated    -> AuthGate.App
                            is SessionStatus.NotAuthenticated -> AuthGate.Auth
                            is SessionStatus.RefreshFailure   -> AuthGate.App
                            else                              -> AuthGate.Loading
                        }

                        AnimatedContent(
                            targetState    = gate,
                            label          = "auth_gate",
                            transitionSpec = {
                                val forward = targetState == AuthGate.App
                                slideInHorizontally  { if (forward) it else -it } + fadeIn() togetherWith
                                        slideOutHorizontally { if (forward) -it else it } + fadeOut()
                            },
                        ) { target ->
                            when (target) {
                                AuthGate.App     -> AppEntry()
                                AuthGate.Auth    -> AuthEntry()
                                AuthGate.Loading -> Box(
                                    Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) { LoadingIndicator() }
                            }
                        }
                    }
                }
            }
        }
    }
}
