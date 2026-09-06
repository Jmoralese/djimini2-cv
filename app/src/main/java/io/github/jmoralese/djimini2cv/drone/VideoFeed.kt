package io.github.jmoralese.djimini2cv.drone

import android.graphics.SurfaceTexture

/**
 * Destino de renderizado del vídeo del dron (o del patrón simulado).
 * Las implementaciones deben tolerar detach repetido; la UI puede llamarlo
 * desde el listener de la superficie y al desmontar la vista.
 *
 * Ampliación prevista (T-005/T-011): acceso a frames decodificados para
 * MediaPipe a través de un FrameSource, sin acoplar la UI al SDK.
 */
interface VideoFeed {
    fun attachSurface(surfaceTexture: SurfaceTexture, width: Int, height: Int)
    fun onSurfaceSizeChanged(width: Int, height: Int)
    fun detachSurface()
}
