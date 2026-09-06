package io.github.jmoralese.djimini2cv.drone.sim

import io.github.jmoralese.djimini2cv.drone.DroneConnectionState
import io.github.jmoralese.djimini2cv.drone.DroneGateway
import io.github.jmoralese.djimini2cv.drone.VideoFeed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Doble del dron para desarrollo y tests: reproduce la secuencia observable
 * registro -> espera -> conexión sin cargar bibliotecas DJI (D-003).
 */
class SimulatedDroneGateway(
    private val scope: CoroutineScope,
    override val videoFeed: VideoFeed,
    private val stepDelayMillis: Long = 600L,
) : DroneGateway {

    override val simulated: Boolean = true

    private val mutableState = MutableStateFlow<DroneConnectionState>(DroneConnectionState.Disconnected)
    override val connectionState = mutableState.asStateFlow()

    private var connectJob: Job? = null

    override fun connect() {
        if (connectJob?.isActive == true || mutableState.value is DroneConnectionState.Connected) return
        connectJob = scope.launch {
            mutableState.value = DroneConnectionState.Registering
            delay(stepDelayMillis)
            mutableState.value = DroneConnectionState.WaitingForDrone
            delay(stepDelayMillis)
            mutableState.value = DroneConnectionState.Connected(SIMULATED_MODEL)
        }
    }

    override fun disconnect() {
        // Cancelar primero: un intento en curso no debe publicar estados antiguos.
        connectJob?.cancel()
        connectJob = null
        mutableState.value = DroneConnectionState.Disconnected
    }

    companion object {
        const val SIMULATED_MODEL = "Mini 2 (simulado)"
    }
}
