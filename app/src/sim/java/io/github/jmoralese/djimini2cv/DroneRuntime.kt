package io.github.jmoralese.djimini2cv

import android.app.Application
import io.github.jmoralese.djimini2cv.drone.DroneGateway
import io.github.jmoralese.djimini2cv.drone.sim.SimulatedDroneGateway
import io.github.jmoralese.djimini2cv.drone.sim.SimulatedVideoFeed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/** Variante simulada: sin bibliotecas DJI ni permisos adicionales (D-003). */
object DroneRuntime {

    val runtimePermissions: List<String> = emptyList()

    fun installSdk(application: Application) {
        // Nada que instalar en simulación.
    }

    fun createGateway(application: Application): DroneGateway =
        SimulatedDroneGateway(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
            videoFeed = SimulatedVideoFeed(),
        )
}
