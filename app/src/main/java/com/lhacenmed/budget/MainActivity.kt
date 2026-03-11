package com.lhacenmed.budget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lhacenmed.budget.core.navigation.INavigationManager
import com.lhacenmed.budget.core.navigation.ScreenRegistry
import com.lhacenmed.budget.ui.AppNavigation
import com.lhacenmed.budget.core.theme.ComposeTemplateTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var navigationManager: INavigationManager
    
    @Inject
    lateinit var screenRegistry: ScreenRegistry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTemplateTheme {
                AppNavigation(
                    navigationManager = navigationManager,
                    screenRegistry = screenRegistry
                )
            }
        }
    }
}