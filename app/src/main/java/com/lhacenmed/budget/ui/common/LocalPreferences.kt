package com.lhacenmed.budget.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lhacenmed.budget.util.DarkThemePreference
import com.lhacenmed.budget.util.PreferenceUtil

val LocalDarkTheme          = compositionLocalOf { PreferenceUtil.DEFAULT_DARK_THEME }
val LocalDynamicColorSwitch = compositionLocalOf { PreferenceUtil.DEFAULT_DYNAMIC_COLOR }
val LocalThemeColorIndex    = compositionLocalOf { PreferenceUtil.DEFAULT_COLOR_INDEX }

@Composable
fun SettingsProvider(content: @Composable () -> Unit) {
    val darkTheme    by PreferenceUtil.darkThemePreference.collectAsStateWithLifecycle()
    val dynamicColor by PreferenceUtil.isDynamicColorEnabled.collectAsStateWithLifecycle()
    val colorIndex   by PreferenceUtil.themeColorIndex.collectAsStateWithLifecycle()

    CompositionLocalProvider(
        LocalDarkTheme          provides darkTheme,
        LocalDynamicColorSwitch provides dynamicColor,
        LocalThemeColorIndex    provides colorIndex,
    ) {
        content()
    }
}
