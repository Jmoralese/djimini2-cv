package io.github.jmoralese.djimini2cv

import android.app.Application
import android.content.Context

/**
 * Composición explícita, sin framework de inyección: el gateway lo crea el
 * DroneRuntime de la variante compilada (sim o real).
 */
class DroneApp : Application() {

    lateinit var droneGateway: io.github.jmoralese.djimini2cv.drone.DroneGateway
        private set

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // En la variante real instala el cargador del SDK DJI; en sim no hace nada.
        DroneRuntime.installSdk(this)
    }

    override fun onCreate() {
        super.onCreate()
        droneGateway = DroneRuntime.createGateway(this)
    }
}
