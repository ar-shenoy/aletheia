package com.realityfilter.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Calm, high-signal, low-drama palette
private val DarkColors = darkColorScheme(
    primary = Color(0xFF7C9CBF),
    onPrimary = Color(0xFF0A0F14),
    secondary = Color(0xFF9BB0C4),
    background = Color(0xFF0B0E11),
    surface = Color(0xFF141A20),
    onBackground = Color(0xFFE6EDF3),
    onSurface = Color(0xFFE6EDF3),
    error = Color(0xFFCF6679)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF3A5F7A),
    onPrimary = Color.White,
    secondary = Color(0xFF5A7A94),
    background = Color(0xFFF7F9FB),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF0B0E11),
    onSurface = Color(0xFF0B0E11)
)

@Composable
fun AletheiaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
