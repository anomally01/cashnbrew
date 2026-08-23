package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState

private val EspressoDarkColorScheme = darkColorScheme(
    primary = EspressoCaramelPrimary,
    onPrimary = EspressoOnPrimary,
    primaryContainer = EspressoCaramelPrimaryContainer,
    onPrimaryContainer = EspressoOnPrimaryContainer,
    secondary = EspressoSecondaryWarm,
    onSecondary = EspressoOnSecondary,
    secondaryContainer = EspressoSecondaryContainer,
    onSecondaryContainer = EspressoOnSecondaryContainer,
    tertiary = EspressoTertiaryGreen,
    onTertiary = EspressoOnTertiary,
    tertiaryContainer = EspressoTertiaryContainerGreen,
    onTertiaryContainer = EspressoOnTertiaryContainer,
    background = EspressoSurfaceDark,
    onBackground = EspressoOnSurfaceWarm,
    surface = EspressoSurfaceDark,
    onSurface = EspressoOnSurfaceWarm,
    surfaceVariant = EspressoSurfaceContainerHighest,
    onSurfaceVariant = EspressoOnSurfaceVariant,
    outline = EspressoOutlineWarm,
    outlineVariant = EspressoOutlineVariant,
    error = EspressoErrorRed,
    onError = EspressoOnError,
    errorContainer = EspressoErrorContainer,
)

private val CreamLightColorScheme = lightColorScheme(
    primary = CreamCaramelPrimary,
    onPrimary = CreamOnPrimary,
    primaryContainer = CreamCaramelPrimaryContainer,
    onPrimaryContainer = CreamOnPrimaryContainer,
    secondary = CreamSecondaryWarm,
    onSecondary = CreamOnSecondary,
    secondaryContainer = CreamSecondaryContainer,
    onSecondaryContainer = CreamOnSecondaryContainer,
    tertiary = CreamTertiaryGreen,
    onTertiary = CreamOnTertiary,
    tertiaryContainer = CreamTertiaryContainerGreen,
    onTertiaryContainer = CreamOnTertiaryContainer,
    background = CreamSurfaceDark,
    onBackground = CreamOnSurfaceWarm,
    surface = CreamSurfaceDark,
    onSurface = CreamOnSurfaceWarm,
    surfaceVariant = CreamSurfaceContainerHighest,
    onSurfaceVariant = CreamOnSurfaceVariant,
    outline = CreamOutlineWarm,
    outlineVariant = CreamOutlineVariant,
    error = CreamErrorRed,
    onError = CreamOnError,
    errorContainer = CreamErrorContainer,
)

@Composable
fun CashAndBrewTheme(
    themeMode: AppThemeMode = ThemeManager.themeMode.collectAsState().value,
    content: @Composable () -> Unit
) {
    val isEspresso = themeMode == AppThemeMode.ESPRESSO
    val colorScheme = if (isEspresso) EspressoDarkColorScheme else CreamLightColorScheme
    val appThemeColors = if (isEspresso) EspressoColors else CreamColors

    CompositionLocalProvider(LocalAppColors provides appThemeColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Backward compatibility helper
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    CashAndBrewTheme(content = content)
}
