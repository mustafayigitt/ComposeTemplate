package com.lhacenmed.budget.ui.common

import androidx.activity.BackEventCompat
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

/**
 * Wraps [content] and intercepts the system back gesture when [enabled] is true.
 * Shows a scaled-down exit card as the user swipes, then calls [onExit] on completion.
 */
@Composable
fun PredictiveExitHandler(
    enabled: Boolean = true,
    onExit: () -> Unit,
    content: @Composable () -> Unit,
) {
    var backProgress by remember { mutableFloatStateOf(0f) }
    var isGestureActive by remember { mutableStateOf(false) }

    // Smoothly spring back to 0 when gesture is cancelled
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(isGestureActive) {
        if (!isGestureActive) {
            animatedProgress.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 300),
            )
        }
    }

    LaunchedEffect(backProgress) {
        if (isGestureActive) {
            animatedProgress.snapTo(backProgress)
        }
    }

    PredictiveBackHandler(enabled = enabled) { progress: Flow<BackEventCompat> ->
        isGestureActive = true
        try {
            progress.collect { event ->
                backProgress = event.progress
            }
            // Gesture completed → animate out then exit
            animatedProgress.animateTo(1f, animationSpec = tween(150))
            onExit()
        } catch (e: CancellationException) {
            // Gesture cancelled → spring back (handled by LaunchedEffect above)
            isGestureActive = false
            backProgress = 0f
        }
    }

    val p = animatedProgress.value

    // Content scales/fades slightly during the gesture (mirrors system predictive back)
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val scale = 1f - (p * 0.08f)
                    scaleX = scale
                    scaleY = scale
                    alpha = 1f - (p * 0.3f)
                },
        ) {
            content()
        }

        // Exit hint card — appears progressively as user swipes
        if (p > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.scrim.copy(alpha = p * 0.32f)
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    tonalElevation = 6.dp,
                    modifier = Modifier
                        .scale(0.6f + (p * 0.4f))
                        .graphicsLayer { alpha = p },
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 40.dp, vertical = 32.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ExitToApp,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Exit app",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}
