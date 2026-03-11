package com.lhacenmed.budget.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextDirection

val Typography = Typography().run {
    copy(
        bodyLarge    = bodyLarge.withDefaults(),
        bodyMedium   = bodyMedium.withDefaults(),
        bodySmall    = bodySmall.withDefaults(),
        titleLarge   = titleLarge.withDefaults(),
        titleMedium  = titleMedium.withDefaults(),
        titleSmall   = titleSmall.withDefaults(),
        headlineSmall  = headlineSmall.withDefaults(),
        headlineMedium = headlineMedium.withDefaults(),
        headlineLarge  = headlineLarge.withDefaults(),
        labelLarge   = labelLarge.withDefaults(),
        labelMedium  = labelMedium.withDefaults(),
        labelSmall   = labelSmall.withDefaults(),
    )
}

private fun TextStyle.withDefaults(): TextStyle = copy(
    lineBreak = LineBreak.Paragraph,
    textDirection = TextDirection.Content,
)
