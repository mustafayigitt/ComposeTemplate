package com.ytapps.composetemplate.core.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private val ShimmerColorLight = Color(0xFFE0E0E0)
private val ShimmerColorDark = Color(0xFFBDBDBD)

fun Modifier.shimmer(
    colors: List<Color> =
        listOf(
            ShimmerColorLight,
            ShimmerColorDark,
            ShimmerColorLight,
        ),
    startX: Float = 0f,
    endX: Float = 1f,
): Modifier =
    drawWithContent {
        drawContent()
        val gradientStartX = size.width * startX
        val gradientEndX = size.width * endX
        val brush =
            Brush.linearGradient(
                colors = colors,
                start =
                    androidx.compose.ui.geometry
                        .Offset(gradientStartX, 0f),
                end =
                    androidx.compose.ui.geometry
                        .Offset(gradientEndX, size.height),
            )
        drawRect(brush = brush)
    }
