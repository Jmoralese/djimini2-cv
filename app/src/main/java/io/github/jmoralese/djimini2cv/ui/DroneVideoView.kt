package io.github.jmoralese.djimini2cv.ui

import android.graphics.SurfaceTexture
import android.view.TextureView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.github.jmoralese.djimini2cv.drone.VideoFeed

/**
 * Vista Android clásica para el vídeo: el decodificador DJI V4 renderiza
 * sobre una SurfaceTexture, algo que Compose no ofrece de forma nativa.
 */
@Composable
fun DroneVideoView(feed: VideoFeed, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextureView(context).apply {
                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                        feed.attachSurface(surface, width, height)
                    }

                    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                        feed.onSurfaceSizeChanged(width, height)
                    }

                    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                        feed.detachSurface()
                        return true
                    }

                    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) = Unit
                }
            }
        },
        onRelease = { feed.detachSurface() },
    )
}
