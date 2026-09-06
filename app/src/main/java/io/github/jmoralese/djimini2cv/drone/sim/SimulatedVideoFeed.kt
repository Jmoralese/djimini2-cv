package io.github.jmoralese.djimini2cv.drone.sim

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.SurfaceTexture
import android.view.Surface
import io.github.jmoralese.djimini2cv.drone.VideoFeed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * Dibuja un patrón animado sobre la superficie para verificar el camino
 * UI -> VideoFeed sin dron. No pretende parecer vídeo real: la variante
 * simulada se identifica de forma visible.
 */
class SimulatedVideoFeed : VideoFeed {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var drawJob: Job? = null
    private var surface: Surface? = null

    override fun attachSurface(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        detachSurface()
        val target = Surface(surfaceTexture)
        surface = target
        drawJob = scope.launch {
            var frame = 0L
            while (isActive) {
                val drawn = drawFrame(target, frame)
                if (!drawn) break
                frame++
                delay(FRAME_INTERVAL_MILLIS)
            }
        }
    }

    override fun onSurfaceSizeChanged(width: Int, height: Int) {
        // El tamaño se lee del canvas en cada frame; nada que reconfigurar.
    }

    override fun detachSurface() {
        drawJob?.cancel()
        drawJob = null
        surface?.release()
        surface = null
    }

    private fun drawFrame(target: Surface, frame: Long): Boolean {
        val canvas: Canvas = try {
            target.lockCanvas(null)
        } catch (_: IllegalStateException) {
            return false
        } catch (_: IllegalArgumentException) {
            return false
        }
        try {
            render(canvas, frame)
        } finally {
            try {
                target.unlockCanvasAndPost(canvas)
            } catch (_: IllegalStateException) {
                return false
            }
        }
        return true
    }

    private fun render(canvas: Canvas, frame: Long) {
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        canvas.drawColor(Color.rgb(16, 26, 38))

        // Rejilla fija de referencia.
        gridPaint.strokeWidth = 1f
        var x = 0f
        while (x <= w) {
            canvas.drawLine(x, 0f, x, h, gridPaint)
            x += w / 8f
        }
        var y = 0f
        while (y <= h) {
            canvas.drawLine(0f, y, w, y, gridPaint)
            y += h / 6f
        }

        // Objetivo móvil (Lissajous) como contenido cambiante verificable.
        val t = frame * FRAME_INTERVAL_MILLIS / 1000f
        val cx = w / 2f + w * 0.3f * sin(1.1f * t)
        val cy = h / 2f + h * 0.3f * cos(0.7f * t)
        canvas.drawCircle(cx, cy, minOf(w, h) * 0.06f, targetPaint)
        canvas.drawLine(cx - 40f, cy, cx + 40f, cy, crossPaint)
        canvas.drawLine(cx, cy - 40f, cx, cy + 40f, crossPaint)

        textPaint.textSize = h * 0.05f
        canvas.drawText("SIMULACIÓN · frame $frame", w * 0.04f, h * 0.95f, textPaint)
    }

    private val gridPaint = Paint().apply {
        color = Color.argb(60, 120, 180, 200)
        style = Paint.Style.STROKE
    }
    private val targetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(200, 77, 208, 225)
        style = Paint.Style.FILL
    }
    private val crossPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeWidth = 2f
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(220, 232, 244, 248)
    }

    private companion object {
        const val FRAME_INTERVAL_MILLIS = 66L // ~15 fps, suficiente para el patrón
    }
}
