package com.ytapps.composetemplate.core.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ytapps.composetemplate.core.ui.ThemePreviews
import com.ytapps.composetemplate.core.ui.shimmer
import com.ytapps.composetemplate.core.ui.theme.ComposeTemplateTheme

private const val SHIMMER_DURATION = 1000
private val shimmerColors =
    listOf(
        Color(0xFFE0E0E0),
        Color(0xFFBDBDBD),
        Color(0xFFE0E0E0),
    )

@Composable
fun AppListSkeleton(
    count: Int = 5,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val offset by transition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(SHIMMER_DURATION),
                repeatMode = RepeatMode.Restart,
            ),
        label = "shimmerOffset",
    )

    Column(modifier = modifier.padding(16.dp)) {
        repeat(count) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .shimmer(
                                colors = shimmerColors,
                                startX = offset,
                                endX = offset + 1f,
                            ),
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Box(
                        modifier =
                            Modifier
                                .width(150.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .shimmer(
                                    colors = shimmerColors,
                                    startX = offset,
                                    endX = offset + 1f,
                                ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .shimmer(
                                    colors = shimmerColors,
                                    startX = offset,
                                    endX = offset + 1f,
                                ),
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun AppListSkeletonPreview() {
    ComposeTemplateTheme {
        AppListSkeleton(count = 3)
    }
}
