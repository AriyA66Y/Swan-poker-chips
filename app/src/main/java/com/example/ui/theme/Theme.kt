package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PokerColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color(0xFF1E1500),
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,
    secondary = ProfitGreen,
    onSecondary = Color(0xFF00391A),
    secondaryContainer = FeltCard,
    onSecondaryContainer = Color(0xFFA5F4C7),
    tertiary = BlueCall,
    onTertiary = Color(0xFF00344F),
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = FeltBorder,
    error = LossRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent poker casino atmosphere
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = PokerColorScheme,
        typography = Typography,
        content = content
    )
}

