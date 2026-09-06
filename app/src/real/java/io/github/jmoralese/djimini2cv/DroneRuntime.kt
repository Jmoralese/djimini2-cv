package io.github.jmoralese.djimini2cv

import android.Manifest
import android.app.Application
import com.cySdkyc.clx.Helper
import io.github.jmoralese.djimini2cv.drone.DroneGateway
import io.github.jmoralese.djimini2cv.drone.dji.DjiDroneGateway

/** Variante real: carga el SDK DJI V4 y crea su adaptador. */
object DroneRuntime {

    // Ubicación: la base fly-safe del SDK la usa; su ausencia no bloquea el registro.
    val runtimePermissions: List<String> = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    fun installSdk(application: Application) {
        // Requisito del empaquetado SecNeo del MSDK V4 (en 4.18 la clase es
        // com.cySdkyc.clx.Helper); debe ejecutarse antes de tocar cualquier
        // clase DJI, desde attachBaseContext de la Application.
        Helper.install(application)
    }

    fun createGateway(application: Application): DroneGateway = DjiDroneGateway(application)
}
