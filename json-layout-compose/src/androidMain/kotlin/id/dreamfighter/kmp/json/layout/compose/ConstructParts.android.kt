package id.dreamfighter.kmp.json.layout.compose

import android.content.Context
import android.media.MediaMetadata
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.FileDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import java.io.File

@OptIn(UnstableApi::class)
@Composable
actual fun VideoPlayer(uris: List<String>, headers:List<Map<String,String>>, listener: (Int,String?) -> Unit) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
            .apply {

                val defaultDataSourceFactory = DefaultHttpDataSource.Factory()
                //val defaultDataSourceFactory = DefaultDataSource.Factory(context)

                // val progressiveMediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)

                uris.forEachIndexed { index, it ->
                    val uri = if(it.startsWith("http")){
                        Uri.parse(it)
                    }else{
                        //Log.d("File","${File(it).exists()}")
                        Uri.fromFile(File(it))
                    }
                    defaultDataSourceFactory.setDefaultRequestProperties(headers[index])

                    if(headers[index]["volume"] != null){
                        volume = headers[index]["volume"]?.toFloat() ?: 0f
                    }else {
                        volume = 0f
                    }
                    //Log.d("lastPathSegment","${uri.lastPathSegment}")
                    val source = if(uri.lastPathSegment?.endsWith("m3u8")==true){
                        // Create a data source factory.
                        //val dataSourceFactory = DefaultHttpDataSource.Factory()

                        HlsMediaSource.Factory(defaultDataSourceFactory).createMediaSource(MediaItem.fromUri(uri))
                    }else if(uri.scheme?.startsWith("http")==true){
                        val cacheDataSourceFactory = CacheDataSource.Factory()
                        Log.d("VIDEO_URI","$uri")

                        cacheDataSourceFactory.setCache(SimpleCacheBuilder.build(context))
                        cacheDataSourceFactory.setUpstreamDataSourceFactory(defaultDataSourceFactory)
                        cacheDataSourceFactory.setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                        ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                            .createMediaSource(MediaItem.fromUri(uri))
                    }else{
                        ProgressiveMediaSource.Factory(FileDataSource.Factory()).createMediaSource(MediaItem.fromUri(uri))
                    }
                    //val source = DefaultMediaSourceFactory(cacheDataSourceFactory).createMediaSource(MediaItem.fromUri(uri))
                    addMediaSource(source)
                }
                prepare()
            }
    }

    exoPlayer.playWhenReady = true
    exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
    exoPlayer.repeatMode = Player.REPEAT_MODE_ALL



    DisposableEffect(
        AndroidView(factory = {
            PlayerView(context).apply {
                hideController()
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        })
    ) {
        val playerListener = object: Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                //listener(playbackState)
                listener(0,mediaItem?.localConfiguration?.uri?.lastPathSegment)
                //Log.d("MediaItem","${mediaItem?.localConfiguration?.uri}")
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                //Log.d("playbackState","$playbackState")
                listener(playbackState,null)
            }

            override fun onPlayerError(error: PlaybackException) {
                listener(99,error.errorCodeName)
            }
        }
        exoPlayer.addListener(playerListener)
        onDispose {
            exoPlayer.removeListener(playerListener)
            exoPlayer.release()
        }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class SimpleCacheBuilder private constructor() {
    companion object {

        @Volatile
        private var instance: SimpleCache? = null


        fun build(context:Context) =
            instance ?: synchronized(this) {
                val databaseProvider = StandaloneDatabaseProvider(context)
                val cacheDir = context.externalCacheDir
                //Log.d("CACHE_DIR","${cacheDir?.exists()} ${cacheDir?.absolutePath}")
                //if(cacheDir?.exists() !){
                //cacheDir?.mkdirs()
                //}
                //Log.d("CACHE_DIR","${it.absolutePath}")

                instance ?: SimpleCache(
                    cacheDir!!,
                    NoOpCacheEvictor(),
                    databaseProvider
                ).also { instance = it }
            }
    }
}