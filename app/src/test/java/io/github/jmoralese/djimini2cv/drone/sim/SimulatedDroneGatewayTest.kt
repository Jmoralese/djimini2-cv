package io.github.jmoralese.djimini2cv.drone.sim

import android.graphics.SurfaceTexture
import io.github.jmoralese.djimini2cv.drone.DroneConnectionState
import io.github.jmoralese.djimini2cv.drone.VideoFeed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SimulatedDroneGatewayTest {

    private class FakeVideoFeed : VideoFeed {
        override fun attachSurface(surfaceTexture: SurfaceTexture, width: Int, height: Int) = Unit
        override fun onSurfaceSizeChanged(width: Int, height: Int) = Unit
        override fun detachSurface() = Unit
    }

    private val connected = DroneConnectionState.Connected(SimulatedDroneGateway.SIMULATED_MODEL)

    @Test
    fun `connect recorre registro, espera y conexion`() = runTest {
        val gateway = SimulatedDroneGateway(this, FakeVideoFeed(), stepDelayMillis = 100)
        val states = mutableListOf<DroneConnectionState>()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            gateway.connectionState.toList(states)
        }

        gateway.connect()
        advanceUntilIdle()
        collector.cancel()

        assertEquals(
            listOf(
                DroneConnectionState.Disconnected,
                DroneConnectionState.Registering,
                DroneConnectionState.WaitingForDrone,
                connected,
            ),
            states,
        )
    }

    @Test
    fun `connect repetido durante un intento activo no reinicia la secuencia`() = runTest {
        val gateway = SimulatedDroneGateway(this, FakeVideoFeed(), stepDelayMillis = 100)
        val states = mutableListOf<DroneConnectionState>()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            gateway.connectionState.toList(states)
        }

        gateway.connect()
        advanceTimeBy(150) // ya en WaitingForDrone
        gateway.connect()
        advanceUntilIdle()
        gateway.connect() // ya conectado: tampoco reinicia
        advanceUntilIdle()
        collector.cancel()

        assertEquals(1, states.count { it == DroneConnectionState.Registering })
        assertEquals(connected, gateway.connectionState.value)
    }

    @Test
    fun `disconnect durante el intento cancela y no publica estados antiguos`() = runTest {
        val gateway = SimulatedDroneGateway(this, FakeVideoFeed(), stepDelayMillis = 100)
        gateway.connect()
        advanceTimeBy(150) // intento a medias

        gateway.disconnect()
        val states = mutableListOf<DroneConnectionState>()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            gateway.connectionState.toList(states)
        }
        advanceUntilIdle()
        collector.cancel()

        assertEquals(DroneConnectionState.Disconnected, gateway.connectionState.value)
        assertFalse(states.any { it is DroneConnectionState.Connected })
    }

    @Test
    fun `tras desconectar se puede volver a conectar`() = runTest {
        val gateway = SimulatedDroneGateway(this, FakeVideoFeed(), stepDelayMillis = 100)
        gateway.connect()
        advanceUntilIdle()
        gateway.disconnect()

        gateway.connect()
        advanceUntilIdle()

        assertEquals(connected, gateway.connectionState.value)
    }
}
