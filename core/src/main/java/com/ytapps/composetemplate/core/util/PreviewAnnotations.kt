package com.ytapps.composetemplate.core.util

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Multipreview annotation that includes dark and light themes.
 */
@Preview(
    name = "Light Mode",
    group = "Themes",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
)
@Preview(
    name = "Dark Mode",
    group = "Themes",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
annotation class ThemePreviews

/**
 * Multipreview annotation that includes different device sizes.
 */
@Preview(name = "Phone", device = "spec:width=360dp,height=640dp,dpi=480")
@Preview(name = "Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
annotation class DevicePreviews
