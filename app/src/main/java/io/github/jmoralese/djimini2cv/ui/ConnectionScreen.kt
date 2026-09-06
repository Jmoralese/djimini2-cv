package io.github.jmoralese.djimini2cv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jmoralese.djimini2cv.R
import io.github.jmoralese.djimini2cv.drone.DroneConnectionState

/**
 * Pantalla única de esta primera versión: estado de conexión y vídeo.
 * Diseño oscuro a pantalla completa inspirado en referencias/dji-fly.
 */
@Composable
fun ConnectionScreen(viewModel: DroneViewModel) {
    val state by viewModel.connectionState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        VideoArea(viewModel, state)
        StatusBar(
            state = state,
            simulated = viewModel.simulated,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .safeDrawingPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
        ActionBar(
            state = state,
            simulated = viewModel.simulated,
            onConnect = viewModel::connect,
            onDisconnect = viewModel::disconnect,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .safeDrawingPadding()
                .padding(16.dp),
        )
    }
}

@Composable
private fun VideoArea(viewModel: DroneViewModel, state: DroneConnectionState) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (state is DroneConnectionState.Connected) {
            // La superficie solo existe con conexión: sin señal no se finge vídeo.
            DroneVideoView(
                feed = viewModel.videoFeed,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
            )
        } else {
            Text(
                text = stringResource(R.string.video_sin_senal),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            )
        }
    }
}

@Composable
private fun StatusBar(state: DroneConnectionState, simulated: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (simulated) {
            Chip(
                text = stringResource(R.string.badge_simulacion),
                container = Color(0xFF7A4A00),
                content = Color(0xFFFFE0B2),
            )
        }
        Chip(
            text = statusText(state),
            container = if (state.isError()) {
                MaterialTheme.colorScheme.error.copy(alpha = 0.25f)
            } else {
                MaterialTheme.colorScheme.surface
            },
            content = if (state.isError()) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
    }
}

@Composable
private fun Chip(text: String, container: Color, content: Color) {
    Surface(
        color = container,
        contentColor = content,
        shape = RoundedCornerShape(50),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun ActionBar(
    state: DroneConnectionState,
    simulated: Boolean,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        DroneConnectionState.Disconnected,
        DroneConnectionState.MissingAppKey,
        is DroneConnectionState.RegistrationFailed,
        -> {
            Button(onClick = onConnect, modifier = modifier) {
                Text(stringResource(R.string.accion_conectar))
            }
        }

        is DroneConnectionState.Connected -> {
            // En la variante real el enlace lo gobierna el USB; no se ofrece
            // un botón que fingiría desconectar el hardware.
            if (simulated) {
                OutlinedButton(onClick = onDisconnect, modifier = modifier) {
                    Text(stringResource(R.string.accion_desconectar))
                }
            }
        }

        DroneConnectionState.Registering,
        DroneConnectionState.WaitingForDrone,
        -> Unit // Proceso en curso visible en el chip de estado.
    }
}

private fun DroneConnectionState.isError(): Boolean =
    this is DroneConnectionState.RegistrationFailed || this is DroneConnectionState.MissingAppKey

@Composable
private fun statusText(state: DroneConnectionState): String = when (state) {
    DroneConnectionState.Disconnected -> stringResource(R.string.estado_desconectado)
    DroneConnectionState.MissingAppKey -> stringResource(R.string.error_falta_app_key)
    DroneConnectionState.Registering -> stringResource(R.string.estado_registrando)
    is DroneConnectionState.RegistrationFailed -> stringResource(R.string.error_registro, state.reason)
    DroneConnectionState.WaitingForDrone -> stringResource(R.string.estado_esperando_dron)
    is DroneConnectionState.Connected -> stringResource(R.string.estado_conectado, state.modelName)
}
