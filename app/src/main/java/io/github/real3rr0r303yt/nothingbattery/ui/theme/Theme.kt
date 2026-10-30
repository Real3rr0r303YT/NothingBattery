package io.github.real3rr0r303yt.nothingbattery.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object NothingColors {
    val Black = Color(0xFF000000)
    val White = Color(0xFFFFFFFF)
    val Red = Color(0xFFD71921)
    val Surface = Color(0xFF0D0D0D)
    val Outline = Color(0xFF262626)
    val Muted = Color(0xFF8A8A8A)
    val DotOff = Color(0xFF1C1C1C)
}

object NothingType {
    /** Uppercase, letter-spaced monospace labels — the Nothing OS "system" voice. */
    val Label = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        letterSpacing = 1.6.sp,
    )
    val Value = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 26.sp,
        letterSpacing = (-0.5).sp,
    )
    val Unit = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        letterSpacing = 1.sp,
    )
    val Body = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    )
}

private val colors = darkColorScheme(
    primary = NothingColors.Red,
    onPrimary = NothingColors.White,
    secondary = NothingColors.White,
    onSecondary = NothingColors.Black,
    background = NothingColors.Black,
    onBackground = NothingColors.White,
    surface = NothingColors.Black,
    onSurface = NothingColors.White,
    surfaceVariant = NothingColors.Surface,
    onSurfaceVariant = NothingColors.Muted,
    outline = NothingColors.Outline,
    error = NothingColors.Red,
)

@Composable
fun NothingBatteryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colors,
        typography = Typography(bodyMedium = NothingType.Body, labelSmall = NothingType.Label),
        content = content,
    )
}
