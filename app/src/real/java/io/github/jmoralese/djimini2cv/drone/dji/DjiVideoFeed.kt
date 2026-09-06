package io.github.jmoralese.djimini2cv.drone.dji

import android.content.Context
import android.graphics.SurfaceTexture
import dji.sdk.camera.VideoFeeder
import dji.sdk.codec.DJICodecManager
import io.github.jmoralese.djimini2cv.drone.VideoFeed

/**
 * Renderiza el vídeo primario del dron con el decodificador del SDK V4.
 * El listener se registra solo mientras hay superficie; los bytes H.264 se
 * entregan íntegros al decodificador (contrato de visión).
 *
 * Punto de ampliación T-005/T-011: DJICodecManager ofrece callbacks YUV para
 * alimentar MediaPipe; se validarán en banco antes de usarlos.
 */
class DjiVideoFeed(private val context: Context) : VideoFeed {

    private var codecManager: DJICodecManager? = null

    private val videoDataListener = VideoFeeder.VideoDataListener { videoBuffer, size ->
        codecManager?.sendDataToDecoder(videoBuffer, size)
    }

    override fun attachSurface(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        if (codecManager == null) {
            codecManager = DJICodecManager(context, surfaceTexture, width, height)
        }
        VideoFeeder.getInstance()?.primaryVideoFeed?.addVideoDataListener(videoDataListener)
    }

    override fun onSurfaceSizeChanged(width: Int, height: Int) {
        // El decodificador V4 gestiona el tamaño internamente; nada que hacer.
    }

    override fun detachSurface() {
        VideoFeeder.getInstance()?.primaryVideoFeed?.removeVideoDataListener(videoDataListener)
        codecManager?.cleanSurface()
        codecManager?.destroyCodec()
        codecManager = null
    }
}
