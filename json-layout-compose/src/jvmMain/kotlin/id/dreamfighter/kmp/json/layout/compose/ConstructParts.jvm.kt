package id.dreamfighter.kmp.json.layout.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import chaintech.videoplayer.host.MediaPlayerError
import chaintech.videoplayer.host.MediaPlayerEvent
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.model.VideoPlayerConfig
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import java.io.File
import java.net.URI

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun VideoPlayer(
    modifier: Modifier,
    uris: List<String>,
    headers: List<Map<String, String>>,
    listener: (Int, String?) -> Unit
) {
    val mediaUri = uris.firstOrNull() ?: return
    println(mediaUri)
    val playerHost = remember { MediaPlayerHost(mediaUrl = mediaUri,isLooping = true, isPaused = false) }
    val playerConfig = remember { VideoPlayerConfig(showControls = false) }
    playerHost.onError = { error ->
        when(error) {
            is MediaPlayerError.VlcNotFound -> { println("Error: VLC library not found. Please ensure VLC is installed.") }
            is MediaPlayerError.InitializationError -> { println("Initialization Error: ${error.details}") }
            is MediaPlayerError.PlaybackError -> { println("Playback Error: ${error.details}") }
            is MediaPlayerError.ResourceError -> { println("Resource Error: ${error.details}") }
        }
    }
    playerHost.onEvent = { event ->
        when (event) {
            is MediaPlayerEvent.MuteChange -> { println("Mute status changed: ${event.isMuted}") }
            is MediaPlayerEvent.PauseChange -> { println("Pause status changed: ${event.isPaused}") }
            is MediaPlayerEvent.BufferChange -> { println("Buffering status: ${event.isBuffering}") }
            is MediaPlayerEvent.CurrentTimeChange -> { println("Current playback time: ${event.currentTime}s") }
            is MediaPlayerEvent.TotalTimeChange -> { println("Video duration updated: ${event.totalTime}s") }
            is MediaPlayerEvent.FullScreenChange -> { println("FullScreen status changed: ${event.isFullScreen}") }
            MediaPlayerEvent.MediaEnd -> { println("Video playback ended") }
            else -> { println("Video playback unknown") }
        }
    }

    VideoPlayerComposable(
        modifier = modifier,
        playerHost = playerHost,
        playerConfig = playerConfig
    )
}