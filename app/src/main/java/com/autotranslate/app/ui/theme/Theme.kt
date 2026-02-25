package com.autotranslate.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

val DarkBackground = Color(0xFF0A0E14)
val DarkSurface = Color(0xFF111820)
val DarkCard = Color(0xFF1A2130)
val DarkCardElevated = Color(0xFF222C3D)

val AccentTeal = Color(0xFF00BFA6)
val AccentBlue = Color(0xFF2196F3)
val AccentCyan = Color(0xFF00E5FF)
val AccentRed = Color(0xFFFF5252)
val AccentYellow = Color(0xFFFFCA28)
val AccentGreen = Color(0xFF69F0AE)

val TextPrimary = Color(0xFFE8EDF4)
val TextSecondary = Color(0xFF8899AA)
val TextMuted = Color(0xFF4A5568)

private val DarkColorScheme = darkColorScheme(
    primary = AccentTeal,
    onPrimary = DarkBackground,
    secondary = AccentBlue,
    tertiary = AccentCyan,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkCard,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = AccentRed,
    outline = TextMuted,
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, color = TextPrimary),
    headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp, color = TextPrimary),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = TextPrimary),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, color = TextPrimary),
    bodyLarge = TextStyle(fontSize = 16.sp, color = TextSecondary),
    bodyMedium = TextStyle(fontSize = 14.sp, color = TextSecondary),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, color = TextSecondary),
)

@Composable
fun AutoTranslateTheme(content: @Composable () -> Unit) {
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DarkBackground.toArgb()
            window.navigationBarColor = DarkBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
