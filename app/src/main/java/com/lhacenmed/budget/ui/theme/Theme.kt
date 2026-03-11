package com.lhacenmed.budget.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import com.lhacenmed.budget.ui.common.LocalDarkTheme
import com.lhacenmed.budget.ui.common.LocalDynamicColorSwitch
import com.lhacenmed.budget.ui.common.LocalThemeColorIndex

private tailrec fun Context.findWindow(): Window? = when (this) {
    is Activity      -> window
    is ContextWrapper -> baseContext.findWindow()
    else             -> null
}

@Composable
fun BudgetTheme(content: @Composable () -> Unit) {
    val darkThemePref   = LocalDarkTheme.current
    val isDynamicColor  = LocalDynamicColorSwitch.current
    val colorIndex      = LocalThemeColorIndex.current
    val isSystemDark    = isSystemInDarkTheme()
    val isDark          = darkThemePref.isDarkTheme(isSystemDark)
    val context         = LocalContext.current

    val colorScheme = when {
        isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        else -> colorPreferences
            .getOrElse(colorIndex) { colorPreferences[0] }
            .run { if (isDark) darkScheme else lightScheme }
    }.let { scheme ->
        if (darkThemePref.isHighContrastModeEnabled && isDark)
            scheme.copy(surface = Color.Black, background = Color.Black)
        else scheme
    }

    val view   = LocalView.current
    val window = view.context.findWindow()

    SideEffect {
        window?.let {
            WindowInsetsControllerCompat(it, view).apply {
                isAppearanceLightStatusBars     = !isDark
                isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content,
    )
}
