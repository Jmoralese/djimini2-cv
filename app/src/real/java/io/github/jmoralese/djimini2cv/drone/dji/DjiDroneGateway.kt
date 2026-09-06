package io.github.jmoralese.djimini2cv.drone.dji

import android.app.Application
import android.content.pm.PackageManager
import dji.common.error.DJIError
import dji.common.error.DJISDKError
import dji.sdk.base.BaseComponent
import dji.sdk.base.BaseProduct
import dji.sdk.sdkmanager.DJISDKInitEvent
import dji.sdk.sdkmanager.DJISDKManager
import io.github.jmoralese.djimini2cv.drone.DroneConnectionState
import io.github.jmoralese.djimini2cv.drone.DroneGateway
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Adaptador DJI V4: concentra registro del SDK, conexión al producto y
 * traducción a tipos propios. Referencia: portal Mobile SDK V4 y muestra
 * oficial (docs/CONTRATO_ARQUITECTURA.md, docs/INTEGRACION_DJI.md).
 */
class DjiDroneGateway(private val application: Application) : DroneGateway {

    override val simulated: Boolean = false

    override val videoFeed = DjiVideoFeed(application)

    private val mutableState = MutableStateFlow<DroneConnectionState>(DroneConnectionState.Disconnected)
    override val connectionState = mutableState.asStateFlow()

    override fun connect() {
        if (manifestAppKey().isBlank()) {
            mutableState.value = DroneConnectionState.MissingAppKey
            return
        }
        when (mutableState.value) {
            DroneConnectionState.Registering,
            DroneConnectionState.WaitingForDrone,
            is DroneConnectionState.Connected,
            -> return

            else -> Unit
        }
        // Solo se llega aquí desde Disconnected/MissingAppKey/RegistrationFailed:
        // el estado propio evita registros duplicados dentro del proceso.
        mutableState.value = DroneConnectionState.Registering
        DJISDKManager.getInstance().registerApp(application, sdkCallback)
    }

    override fun disconnect() {
        // El enlace físico lo gobiernan USB y SDK; no se finge una desconexión.
        // La parada de sesiones de visión/control llegará con esas funciones.
    }

    // Los callbacks del SDK llegan en hilos propios; StateFlow es seguro para esto.
    private val sdkCallback = object : DJISDKManager.SDKManagerCallback {

        override fun onRegister(error: DJIError?) {
            if (error == DJISDKError.REGISTRATION_SUCCESS) {
                mutableState.value = DroneConnectionState.WaitingForDrone
                DJISDKManager.getInstance().startConnectionToProduct()
            } else {
                mutableState.value = DroneConnectionState.RegistrationFailed(
                    error?.description ?: "Error desconocido",
                )
            }
        }

        override fun onProductConnect(product: BaseProduct?) = publishProduct(product)

        override fun onProductChanged(product: BaseProduct?) = publishProduct(product)

        override fun onProductDisconnect() {
            mutableState.value = DroneConnectionState.WaitingForDrone
        }

        override fun onComponentChange(
            key: BaseProduct.ComponentKey?,
            oldComponent: BaseComponent?,
            newComponent: BaseComponent?,
        ) {
            publishProduct(DJISDKManager.getInstance().product)
        }

        override fun onInitProcess(event: DJISDKInitEvent?, totalProcess: Int) = Unit

        override fun onDatabaseDownloadProgress(current: Long, total: Long) = Unit
    }

    private fun publishProduct(product: BaseProduct?) {
        mutableState.value = if (product != null && product.isConnected) {
            DroneConnectionState.Connected(product.model?.displayName ?: "Producto DJI")
        } else {
            // Mando enlazado sin aeronave encendida también pasa por aquí.
            DroneConnectionState.WaitingForDrone
        }
    }

    private fun manifestAppKey(): String = try {
        val info = application.packageManager.getApplicationInfo(
            application.packageName,
            PackageManager.GET_META_DATA,
        )
        info.metaData?.getString("com.dji.sdk.API_KEY").orEmpty()
    } catch (_: PackageManager.NameNotFoundException) {
        ""
    }
}
