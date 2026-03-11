package com.lhacenmed.budget.ui.page.appearance

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.lhacenmed.budget.ui.common.LocalDarkTheme
import com.lhacenmed.budget.util.DarkThemePreference.Companion.FOLLOW_SYSTEM
import com.lhacenmed.budget.util.DarkThemePreference.Companion.OFF
import com.lhacenmed.budget.util.DarkThemePreference.Companion.ON
import com.lhacenmed.budget.util.PreferenceUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkThemePage(onNavigateBack: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val darkTheme      = LocalDarkTheme.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Dark Theme") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            // Follow system (API 29+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                item {
                    SingleChoiceItem(
                        text       = "Follow System",
                        selected   = darkTheme.darkThemeValue == FOLLOW_SYSTEM,
                        onClick    = { PreferenceUtil.modifyDarkThemePreference(FOLLOW_SYSTEM) },
                    )
                }
            }
            item {
                SingleChoiceItem(
                    text    = "On",
                    selected = darkTheme.darkThemeValue == ON,
                    onClick  = { PreferenceUtil.modifyDarkThemePreference(ON) },
                )
            }
            item {
                SingleChoiceItem(
                    text    = "Off",
                    selected = darkTheme.darkThemeValue == OFF,
                    onClick  = { PreferenceUtil.modifyDarkThemePreference(OFF) },
                )
            }

            item { HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }

            item {
                Text(
                    text     = "Additional settings",
                    modifier = Modifier.padding(start = 16.dp, bottom = 4.dp),
                )
            }
            item {
                ListItem(
                    headlineContent   = { Text("High Contrast") },
                    leadingContent    = { Icon(Icons.Outlined.Contrast, contentDescription = null) },
                    trailingContent   = {
                        Switch(
                            checked         = darkTheme.isHighContrastModeEnabled,
                            onCheckedChange = {
                                PreferenceUtil.modifyDarkThemePreference(
                                    isHighContrastModeEnabled = it,
                                )
                            },
                        )
                    },
                    modifier = Modifier.clickable {
                        PreferenceUtil.modifyDarkThemePreference(
                            isHighContrastModeEnabled = !darkTheme.isHighContrastModeEnabled,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun SingleChoiceItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(text) },
        trailingContent = { RadioButton(selected = selected, onClick = onClick) },
        modifier        = Modifier.clickable(onClick = onClick),
    )
}
