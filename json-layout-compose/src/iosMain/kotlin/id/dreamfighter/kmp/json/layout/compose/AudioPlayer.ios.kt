package id.dreamfighter.kmp.json.layout.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVURLAsset
import platform.AVFoundation.play
import platform.AVKit.AVPlayerViewController
import platform.Foundation.NSDictionary
import platform.Foundation.NSURL

@Composable
actual fun AudioPlayer(
    modifier: Modifier,
    url: String,
    headers: Map<String, String>,
    autoPlay: Boolean,
    isLooping: Boolean
) {
    val player = remember { AVPlayer() }
    val playerViewController = remember { AVPlayerViewController() }

    LaunchedEffect(url) {
        val nsUrl = NSURL.URLWithString(url) ?: return@LaunchedEffect

        val assetOptions = if (headers.isNotEmpty()) {
            @Suppress("UNCHECKED_CAST")
            mapOf("AVURLAssetHTTPHeaderFieldsKey" to (headers as NSDictionary))
        } else null

        val asset = AVURLAsset.URLAssetWithURL(nsUrl, assetOptions as? NSDictionary)
        val item = AVPlayerItem(asset)

        player.replaceCurrentItemWithPlayerItem(item)
        if (autoPlay) player.play()

        // Note: Looping in AVPlayer requires listening to AVPlayerItemDidPlayToEndTimeNotification
        // which is complex in Compose. Simple play is implemented here.
    }

    DisposableEffect(Unit) {
        onDispose { player.pause() }
    }

    UIKitView(
        factory = {
            playerViewController.player = player
            playerViewController.showsPlaybackControls = true
            playerViewController.view
        },
        modifier = modifier
    )
}