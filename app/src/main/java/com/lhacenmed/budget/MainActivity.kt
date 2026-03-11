package com.lhacenmed.budget

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lhacenmed.budget.core.theme.BudgetTheme
import com.lhacenmed.budget.ui.HomeScreen
import com.lhacenmed.budget.ui.auth.LoginScreen
import com.lhacenmed.budget.ui.auth.RegisterScreen
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var supabase: SupabaseClient

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BudgetTheme {
                val sessionStatus by supabase.auth.sessionStatus.collectAsStateWithLifecycle(
                    initialValue = SessionStatus.Initializing
                )
                var showRegister by remember { mutableStateOf(false) }

                // Intercept back gesture when on register screen → go to login, not exit
                BackHandler(enabled = showRegister) {
                    showRegister = false
                }

                AnimatedContent(targetState = sessionStatus, label = "auth") { status ->
                    when (status) {
                        SessionStatus.Initializing -> Box(
                            Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }

                        is SessionStatus.Authenticated -> HomeScreen()

                        else -> if (showRegister) {
                            RegisterScreen(onNavigateBack = { showRegister = false })
                        } else {
                            LoginScreen(onNavigateToRegister = { showRegister = true })
                        }
                    }
                }
            }
        }
    }
}
