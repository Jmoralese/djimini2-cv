package io.github.jmoralese.djimini2cv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.jmoralese.djimini2cv.drone.DroneConnectionState
import io.github.jmoralese.djimini2cv.drone.DroneGateway
import io.github.jmoralese.djimini2cv.drone.VideoFeed
import kotlinx.coroutines.flow.StateFlow

/**
 * Estado de pantalla sobre el gateway. Crecerá con telemetría y visión;
 * la UI no accede a clases DJI en ningún caso.
 */
class DroneViewModel(private val gateway: DroneGateway) : ViewModel() {

    val simulated: Boolean get() = gateway.simulated
    val videoFeed: VideoFeed get() = gateway.videoFeed
    val connectionState: StateFlow<DroneConnectionState> = gateway.connectionState

    fun connect() = gateway.connect()

    fun disconnect() = gateway.disconnect()

    companion object {
        fun factory(gateway: DroneGateway): ViewModelProvider.Factory = viewModelFactory {
            initializer { DroneViewModel(gateway) }
        }
    }
}
