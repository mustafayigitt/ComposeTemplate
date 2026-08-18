package com.ytapps.composetemplate.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.ytapps.composetemplate.core.ui.ThemePreviews
import com.ytapps.composetemplate.core.ui.shimmer
import com.ytapps.composetemplate.core.ui.theme.ComposeTemplateTheme

/**
 * Coil-backed image loader with a shimmer placeholder while loading and a fallback icon on error.
 * Prefer this over a raw [AsyncImage] so loading/error states stay consistent across features.
 */
@Composable
fun AppAsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = MaterialTheme.shapes.small,
) {
    val painter = rememberAsyncImagePainter(model = model)
    val state = painter.state.value

    Box(
        modifier = modifier.clip(shape),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is AsyncImagePainter.State.Loading, is AsyncImagePainter.State.Empty ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .shimmer(),
                )

            is AsyncImagePainter.State.Error ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

            is AsyncImagePainter.State.Success ->
                AsyncImage(
                    model = model,
                    contentDescription = contentDescription,
                    contentScale = contentScale,
                    modifier = Modifier.fillMaxSize(),
                )
        }
    }
}

@ThemePreviews
@Composable
private fun AppAsyncImagePreview() {
    ComposeTemplateTheme {
        AppAsyncImage(
            model = null,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
