package id.dreamfighter.kmp.json.layout.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import chaintech.videoplayer.host.MediaPlayerError
import chaintech.videoplayer.host.MediaPlayerEvent
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.model.VideoPlayerConfig
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import uk.co.caprica.vlcj.factory.MediaPlayerFactory

// You might need to wrap this in a Box or use a custom UI
// because vlcj doesn't provide a built-in Audio UI control set for Compose.
// This plays the audio, but you might need to build Play/Pause buttons in Compose.
@Composable
actual fun AudioPlayer(
    modifier: Modifier,
    url: String,
    headers: Map<String, String>,
    autoPlay: Boolean,
    isLooping: Boolean
) {
    println(url)
    val playerHost = remember { MediaPlayerHost(isLooping = false, isPaused = false) }
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

    LaunchedEffect(Unit){
        playerHost.loadUrl(url)
        //playerHost.toggleMuteUnmute()
    }

    VideoPlayerComposable(
        modifier = modifier,
        playerHost = playerHost,
        playerConfig = playerConfig
    )
}