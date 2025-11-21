package id.dreamfighter.kmp.json.layout.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.browser.document
import org.w3c.dom.HTMLAudioElement

@Composable
actual fun AudioPlayer(
    modifier: Modifier,
    url: String,
    headers: Map<String, String>,
    autoPlay: Boolean,
    isLooping: Boolean
) {
    // Note: HTML5 Audio does not support custom HTTP headers for the source URL
    // due to browser security specs. The 'headers' parameter will be ignored.

    val audioElement = remember {
        (document.createElement("audio") as HTMLAudioElement).apply {
            style.display = "none" // Hide visual player
        }
    }

    LaunchedEffect(url, autoPlay, isLooping) {
        audioElement.src = url
        audioElement.loop = isLooping
        if (autoPlay) {
            audioElement.play()
        }
    }

    DisposableEffect(Unit) {
        document.body?.appendChild(audioElement)
        onDispose {
            audioElement.pause()
            audioElement.remove()
        }
    }
}