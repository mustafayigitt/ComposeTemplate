package com.lhacenmed.budget.ui.page.status

import android.content.Intent
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.lhacenmed.budget.data.model.StatusItem

@Composable
fun MediaPreviewScreen(
    item:     StatusItem,
    isSaving: Boolean,
    onBack:   () -> Unit,
    onSave:   () -> Unit
) {
    var showInfo by remember { mutableStateOf(false) }
    val context  = LocalContext.current

    BackHandler(onBack = onBack)

    Box(Modifier.fillMaxSize().background(Color.Black)) {

        // ── Media ─────────────────────────────────────────────────────────────
        if (item.isVideo) {
            VideoPlayer(uri = item.uri, modifier = Modifier.fillMaxSize())
        } else {
            AsyncImage(
                model              = item.uri,
                contentDescription = item.name,
                contentScale       = ContentScale.Fit,
                modifier           = Modifier.fillMaxSize()
            )
        }

        // ── Top bar ───────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text  = item.name,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                maxLines = 1
            )
        }

        // ── Bottom action bar ─────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.45f))
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Save
            ActionButton(
                icon    = Icons.Default.Download,
                label   = "Save",
                loading = isSaving,
                onClick = onSave
            )
            // Share
            ActionButton(
                icon  = Icons.Default.Share,
                label = "Share",
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type  = if (item.isVideo) "video/*" else "image/*"
                        putExtra(Intent.EXTRA_STREAM, item.uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share via"))
                }
            )
            // Info
            ActionButton(
                icon  = Icons.Default.Info,
                label = "Info",
                onClick = { showInfo = true }
            )
        }
    }

    // ── Info dialog ───────────────────────────────────────────────────────────
    if (showInfo) {
        AlertDialog(
            onDismissRequest = { showInfo = false },
            title   = { Text("File Info") },
            text    = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    InfoRow("Name", item.name)
                    InfoRow("Type", if (item.isVideo) "Video" else "Image")
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfo = false }) { Text("OK") }
            }
        )
    }
}

// ── Video player (ExoPlayer) ──────────────────────────────────────────────────

@Composable
private fun VideoPlayer(uri: android.net.Uri, modifier: Modifier) {
    val context = LocalContext.current
    val player  = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = true
        }
    }
    DisposableEffect(Unit) { onDispose { player.release() } }

    AndroidView(
        modifier = modifier,
        factory  = {
            PlayerView(it).apply {
                this.player  = player
                useController = true
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    )
}

// ── Small helpers ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    loading: Boolean = false,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (loading) {
            LoadingIndicator(
                modifier    = Modifier.size(24.dp).padding(bottom = 4.dp),
                color       = Color.White
            )
        } else {
            IconButton(onClick = onClick) {
                Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(26.dp))
            }
        }
        Text(label, color = Color.White, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}
