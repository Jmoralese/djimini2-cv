package io.github.jmoralese.djimini2cv.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Tema oscuro fijo, como las apps de pilotaje: el vídeo es el protagonista.
private val DarkColors = darkColorScheme(
    primary = Color(0xFF4DD0E1),
    onPrimary = Color(0xFF00252B),
    secondary = Color(0xFF80CBC4),
    background = Color(0xFF060A0E),
    onBackground = Color(0xFFE8F4F8),
    surface = Color(0xFF101A26),
    onSurface = Color(0xFFE8F4F8),
    error = Color(0xFFFF8A80),
)

@Composable
fun Dji2Theme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColors, content = content)
}
