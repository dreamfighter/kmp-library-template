package id.dreamfighter.kmp.json.layout.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView

@Composable
actual fun AudioPlayer(
    modifier: Modifier,
    url: String,
    headers: Map<String, String>,
    autoPlay: Boolean,
    isLooping: Boolean
) {
    val context = LocalContext.current

    val exoPlayer = remember {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        if (headers.isNotEmpty()) {
            httpDataSourceFactory.setDefaultRequestProperties(headers)
        }
        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)

        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build()
    }

    LaunchedEffect(url) {
        val mediaItem = MediaItem.fromUri(url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        if (autoPlay) exoPlayer.play()
        if (isLooping) exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                // Configure for Audio: Hide video surface, show controls
                controllerShowTimeoutMs = 0 // Keep controls visible
                controllerHideOnTouch = false
                // Optional: Set a default artwork or just show the controller
            }
        },
        modifier = modifier
    )
}