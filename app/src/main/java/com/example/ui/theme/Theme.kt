package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CaramelPrimary,
    onPrimary = OnPrimary,
    primaryContainer = CaramelPrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = SecondaryWarm,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = TertiaryGreen,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainerGreen,
    onTertiaryContainer = OnTertiaryContainer,
    background = SurfaceDark,
    onBackground = OnSurfaceWarm,
    surface = SurfaceDark,
    onSurface = OnSurfaceWarm,
    surfaceVariant = SurfaceContainerHighest,
    onSurfaceVariant = OnSurfaceVariant,
    outline = OutlineWarm,
    outlineVariant = OutlineVariant,
    error = ErrorRed,
    onError = OnError,
    errorContainer = ErrorContainer
)

@Composable
fun CashAndBrewTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We enforce the designated Espresso Velvet dark palette for the brand POS experience
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

// Keep backward compatibility if old theme name referenced
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    CashAndBrewTheme(content = content)
}
