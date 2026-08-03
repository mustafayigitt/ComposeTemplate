package com.ytapps.composetemplate.feature.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ytapps.composetemplate.core.common.Language
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.ui.components.AppCard

@Composable
fun ProfileScreen(navigationManager: INavigationManager) {
    ProfileScreenInternal(navigationManager = navigationManager)
}

@Composable
internal fun ProfileScreenInternal(
    navigationManager: INavigationManager,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                ProfileEvent.NavigateToLogin -> navigationManager.navigateBackToRoot()
            }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.Start),
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Dark Mode")
                Switch(
                    checked = uiState.isDarkMode,
                    onCheckedChange = { viewModel.toggleTheme() },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Language",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Language.entries.forEach { language ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = language.displayName)
                        androidx.compose.material3.RadioButton(
                            selected = uiState.currentLanguage == language,
                            onClick = { viewModel.onLanguageSelected(language) },
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.logout() },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Logout")
        }
    }
}
