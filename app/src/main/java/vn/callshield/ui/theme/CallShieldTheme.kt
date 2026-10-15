package vn.callshield.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF4361EE),
    secondary = Color(0xFF7209B7),
    tertiary = Color(0xFF4CC9A6),
    background = Color(0xFFF5F7FF),
    surface = Color.White
)

@Composable
fun CallShieldTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
