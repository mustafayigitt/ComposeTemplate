package com.lhacenmed.budget.ui.page.appearance

import android.os.Build
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.lhacenmed.budget.ui.common.LocalDarkTheme
import com.lhacenmed.budget.ui.common.LocalDynamicColorSwitch
import com.lhacenmed.budget.ui.common.LocalThemeColorIndex
import com.lhacenmed.budget.ui.theme.ThemeColor
import com.lhacenmed.budget.ui.theme.colorPreferences
import com.lhacenmed.budget.util.DarkThemePreference.Companion.OFF
import com.lhacenmed.budget.util.DarkThemePreference.Companion.ON
import com.lhacenmed.budget.util.PreferenceUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearancePage(
    onNavigateBack: () -> Unit,
    onNavigateToDarkTheme: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val isDynamicColor = LocalDynamicColorSwitch.current
    val colorIndex     = LocalThemeColorIndex.current
    val darkTheme      = LocalDarkTheme.current
    val isDark         = darkTheme.isDarkTheme()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Appearance") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            // ── Color palette picker ───────────────────────────────────────
            Text(
                text = "Theme Color",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                itemsIndexed(colorPreferences) { index, themeColor ->
                    ColorButton(
                        themeColor  = themeColor,
                        isDark      = isDark,
                        isSelected  = !isDynamicColor && colorIndex == index,
                        onClick     = {
                            PreferenceUtil.switchDynamicColor(enabled = false)
                            PreferenceUtil.modifyThemeColor(index)
                        },
                    )
                }
            }

            // ── Dynamic color (API 31+) ────────────────────────────────────
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ListItem(
                    headlineContent   = { Text("Dynamic Color") },
                    supportingContent = { Text("Use wallpaper colors (Android 12+)") },
                    leadingContent    = { Icon(Icons.Outlined.Colorize, contentDescription = null) },
                    trailingContent   = {
                        Switch(
                            checked         = isDynamicColor,
                            onCheckedChange = { PreferenceUtil.switchDynamicColor(it) },
                        )
                    },
                    modifier = Modifier.clickable { PreferenceUtil.switchDynamicColor() },
                )
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // ── Dark theme ─────────────────────────────────────────────────
            ListItem(
                headlineContent   = { Text("Dark Theme") },
                supportingContent = { Text(darkTheme.getDarkThemeDesc()) },
                leadingContent    = {
                    Icon(
                        imageVector = if (isDark) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                        contentDescription = null,
                    )
                },
                trailingContent   = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Switch(
                            checked         = isDark,
                            onCheckedChange = {
                                PreferenceUtil.modifyDarkThemePreference(
                                    darkThemeValue = if (isDark) OFF else ON,
                                )
                            },
                        )
                        IconButton(onClick = onNavigateToDarkTheme) {
                            Icon(Icons.Outlined.KeyboardArrowRight, contentDescription = "More")
                        }
                    }
                },
                modifier = Modifier.clickable { onNavigateToDarkTheme() },
            )
        }
    }
}

@Composable
private fun ColorButton(
    themeColor: ThemeColor,
    isDark: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val primaryColor  = if (isDark) themeColor.primaryDark else themeColor.primaryLight
    val containerSize by animateDpAsState(
        targetValue = if (isSelected) 28.dp else 0.dp, label = "container"
    )
    val iconSize by animateDpAsState(
        targetValue = if (isSelected) 16.dp else 0.dp, label = "icon"
    )

    Surface(
        onClick   = onClick,
        modifier  = Modifier.padding(4.dp).size(64.dp),
        shape     = RoundedCornerShape(16.dp),
        color     = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .drawBehind { drawCircle(primaryColor) },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(containerSize)
                        .drawBehind {
                            drawCircle(Color.Black.copy(alpha = 0.2f))
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.Check,
                        contentDescription = null,
                        modifier           = Modifier.size(iconSize),
                        tint               = Color.White,
                    )
                }
            }
        }
    }
}
