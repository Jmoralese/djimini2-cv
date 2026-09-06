package io.github.jmoralese.djimini2cv.drone

/**
 * Estado de conexión con tipos propios, sin clases DJI.
 * Distingue registro del SDK de la conexión física al producto.
 */
sealed interface DroneConnectionState {
    /** Sin sesión iniciada; también tras una desconexión pedida por el usuario. */
    data object Disconnected : DroneConnectionState

    /** La variante real no tiene App Key configurada; no se intenta registrar. */
    data object MissingAppKey : DroneConnectionState

    /** Registro del SDK en curso (requiere Internet la primera vez). */
    data object Registering : DroneConnectionState

    data class RegistrationFailed(val reason: String) : DroneConnectionState

    /** SDK listo; sin enlace efectivo con mando/aeronave todavía. */
    data object WaitingForDrone : DroneConnectionState

    data class Connected(val modelName: String) : DroneConnectionState
}
