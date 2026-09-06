package io.github.jmoralese.djimini2cv.drone

import kotlinx.coroutines.flow.StateFlow

/**
 * Único punto de acceso al dron para UI y lógica de aplicación.
 * Implementaciones: simulada (sin DJI) y adaptador DJI V4 en la variante real.
 * No replica la API DJI: expone solo lo que la app necesita.
 */
interface DroneGateway {
    /** Indicador visible de simulación exigido por el contrato del dron. */
    val simulated: Boolean

    val connectionState: StateFlow<DroneConnectionState>

    val videoFeed: VideoFeed

    /** Inicia registro y conexión. Idempotente mientras hay un intento activo. */
    fun connect()

    /**
     * Termina la sesión iniciada por el usuario. En la variante real el enlace
     * físico lo gobierna el USB/SDK; la implementación no finge desconexiones.
     */
    fun disconnect()
}
