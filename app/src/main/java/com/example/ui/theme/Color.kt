package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ==========================================
// 1. ESPRESSO THEME PALETTE (Default Dark)
// ==========================================
val EspressoSurfaceDark = Color(0xFF0C0908)
val EspressoSurfaceContainerLowest = Color(0xFF080605)
val EspressoSurfaceContainerLow = Color(0xFF14100E)
val EspressoSurfaceContainer = Color(0xFF1A120B)
val EspressoSurfaceContainerHigh = Color(0xFF241B13)
val EspressoSurfaceContainerHighest = Color(0xFF2D241C)
val EspressoSurfaceBright = Color(0xFF382D24)

val EspressoCaramelPrimary = Color(0xFFD4A373)
val EspressoCaramelPrimaryContainer = Color(0xFFA07850)
val EspressoCaramelPrimaryLight = Color(0xFFE8C5A0)
val EspressoOnPrimary = Color(0xFF1A120B)
val EspressoOnPrimaryContainer = Color(0xFFF5F2ED)
val EspressoPrimaryFixed = Color(0xFFF0D5BA)

val EspressoSecondaryWarm = Color(0xFFA68E74)
val EspressoSecondaryContainer = Color(0xFF2D241C)
val EspressoOnSecondary = Color(0xFF0C0908)
val EspressoOnSecondaryContainer = Color(0xFFD4A373)

val EspressoOnSurfaceWarm = Color(0xFFF5F2ED)
val EspressoOnSurfaceVariant = Color(0xFFA68E74)
val EspressoOutlineWarm = Color(0xFFA68E74)
val EspressoOutlineVariant = Color(0xFF2D241C)

val EspressoTertiaryGreen = Color(0xFF88C999)
val EspressoTertiaryContainerGreen = Color(0xFF1C3825)
val EspressoOnTertiary = Color(0xFF0C1F12)
val EspressoOnTertiaryContainer = Color(0xFFA8E0B7)

val EspressoErrorRed = Color(0xFFE57373)
val EspressoErrorContainer = Color(0xFF441818)
val EspressoOnError = Color(0xFF2C0B0B)

// ==========================================
// 2. CREAM THEME PALETTE (Light Mode)
// ==========================================
val CreamSurfaceDark = Color(0xFFFBF8F3) // Soft Milk Foam / Warm Canvas
val CreamSurfaceContainerLowest = Color(0xFFFFFFFF)
val CreamSurfaceContainerLow = Color(0xFFF6F1EA)
val CreamSurfaceContainer = Color(0xFFFFFFFF) // Crisp container cards
val CreamSurfaceContainerHigh = Color(0xFFEFE7DB)
val CreamSurfaceContainerHighest = Color(0xFFE7DEC0)
val CreamSurfaceBright = Color(0xFFFFFFFF)

val CreamCaramelPrimary = Color(0xFF9E6230) // Rich Roasted Caramel Amber
val CreamCaramelPrimaryContainer = Color(0xFFEAD2BE)
val CreamCaramelPrimaryLight = Color(0xFFBA7D4A)
val CreamOnPrimary = Color(0xFFFFFFFF)
val CreamOnPrimaryContainer = Color(0xFF2A1507)
val CreamPrimaryFixed = Color(0xFFF5E4D4)

val CreamSecondaryWarm = Color(0xFF7A6451) // Warm Mocha
val CreamSecondaryContainer = Color(0xFFEDE4D9)
val CreamOnSecondary = Color(0xFFFFFFFF)
val CreamOnSecondaryContainer = Color(0xFF2A1507)

val CreamOnSurfaceWarm = Color(0xFF1C130D) // Deep Roasted Espresso Text
val CreamOnSurfaceVariant = Color(0xFF6E5B4C) // Muted Hazelnut
val CreamOutlineWarm = Color(0xFF9E8A78)
val CreamOutlineVariant = Color(0xFFE2D6C6) // Soft Latte Border

val CreamTertiaryGreen = Color(0xFF2E7D32)
val CreamTertiaryContainerGreen = Color(0xFFD6EED8)
val CreamOnTertiary = Color(0xFFFFFFFF)
val CreamOnTertiaryContainer = Color(0xFF0A3310)

val CreamErrorRed = Color(0xFFD32F2F)
val CreamErrorContainer = Color(0xFFFFEBEE)
val CreamOnError = Color(0xFFFFFFFF)

// ==========================================
// Dynamic Theme Colors Data Container
// ==========================================
data class AppThemeColors(
    val surfaceDark: Color,
    val surfaceContainerLowest: Color,
    val surfaceContainerLow: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerHighest: Color,
    val surfaceBright: Color,
    val caramelPrimary: Color,
    val caramelPrimaryContainer: Color,
    val caramelPrimaryLight: Color,
    val onPrimary: Color,
    val onPrimaryContainer: Color,
    val primaryFixed: Color,
    val secondaryWarm: Color,
    val secondaryContainer: Color,
    val onSecondary: Color,
    val onSecondaryContainer: Color,
    val onSurfaceWarm: Color,
    val onSurfaceVariant: Color,
    val outlineWarm: Color,
    val outlineVariant: Color,
    val tertiaryGreen: Color,
    val tertiaryContainerGreen: Color,
    val onTertiary: Color,
    val onTertiaryContainer: Color,
    val errorRed: Color,
    val errorContainer: Color,
    val onError: Color
)

val EspressoColors = AppThemeColors(
    surfaceDark = EspressoSurfaceDark,
    surfaceContainerLowest = EspressoSurfaceContainerLowest,
    surfaceContainerLow = EspressoSurfaceContainerLow,
    surfaceContainer = EspressoSurfaceContainer,
    surfaceContainerHigh = EspressoSurfaceContainerHigh,
    surfaceContainerHighest = EspressoSurfaceContainerHighest,
    surfaceBright = EspressoSurfaceBright,
    caramelPrimary = EspressoCaramelPrimary,
    caramelPrimaryContainer = EspressoCaramelPrimaryContainer,
    caramelPrimaryLight = EspressoCaramelPrimaryLight,
    onPrimary = EspressoOnPrimary,
    onPrimaryContainer = EspressoOnPrimaryContainer,
    primaryFixed = EspressoPrimaryFixed,
    secondaryWarm = EspressoSecondaryWarm,
    secondaryContainer = EspressoSecondaryContainer,
    onSecondary = EspressoOnSecondary,
    onSecondaryContainer = EspressoOnSecondaryContainer,
    onSurfaceWarm = EspressoOnSurfaceWarm,
    onSurfaceVariant = EspressoOnSurfaceVariant,
    outlineWarm = EspressoOutlineWarm,
    outlineVariant = EspressoOutlineVariant,
    tertiaryGreen = EspressoTertiaryGreen,
    tertiaryContainerGreen = EspressoTertiaryContainerGreen,
    onTertiary = EspressoOnTertiary,
    onTertiaryContainer = EspressoOnTertiaryContainer,
    errorRed = EspressoErrorRed,
    errorContainer = EspressoErrorContainer,
    onError = EspressoOnError
)

val CreamColors = AppThemeColors(
    surfaceDark = CreamSurfaceDark,
    surfaceContainerLowest = CreamSurfaceContainerLowest,
    surfaceContainerLow = CreamSurfaceContainerLow,
    surfaceContainer = CreamSurfaceContainer,
    surfaceContainerHigh = CreamSurfaceContainerHigh,
    surfaceContainerHighest = CreamSurfaceContainerHighest,
    surfaceBright = CreamSurfaceBright,
    caramelPrimary = CreamCaramelPrimary,
    caramelPrimaryContainer = CreamCaramelPrimaryContainer,
    caramelPrimaryLight = CreamCaramelPrimaryLight,
    onPrimary = CreamOnPrimary,
    onPrimaryContainer = CreamOnPrimaryContainer,
    primaryFixed = CreamPrimaryFixed,
    secondaryWarm = CreamSecondaryWarm,
    secondaryContainer = CreamSecondaryContainer,
    onSecondary = CreamOnSecondary,
    onSecondaryContainer = CreamOnSecondaryContainer,
    onSurfaceWarm = CreamOnSurfaceWarm,
    onSurfaceVariant = CreamOnSurfaceVariant,
    outlineWarm = CreamOutlineWarm,
    outlineVariant = CreamOutlineVariant,
    tertiaryGreen = CreamTertiaryGreen,
    tertiaryContainerGreen = CreamTertiaryContainerGreen,
    onTertiary = CreamOnTertiary,
    onTertiaryContainer = CreamOnTertiaryContainer,
    errorRed = CreamErrorRed,
    errorContainer = CreamErrorContainer,
    onError = CreamOnError
)

val LocalAppColors = staticCompositionLocalOf { EspressoColors }

// ==========================================
// Reactive Color Accessors for Jetpack Compose
// ==========================================
val SurfaceDark: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.surfaceDark

val SurfaceContainerLowest: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.surfaceContainerLowest

val SurfaceContainerLow: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.surfaceContainerLow

val SurfaceContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.surfaceContainer

val SurfaceContainerHigh: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.surfaceContainerHigh

val SurfaceContainerHighest: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.surfaceContainerHighest

val SurfaceBright: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.surfaceBright

val CaramelPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.caramelPrimary

val CaramelPrimaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.caramelPrimaryContainer

val CaramelPrimaryLight: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.caramelPrimaryLight

val OnPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.onPrimary

val OnPrimaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.onPrimaryContainer

val PrimaryFixed: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.primaryFixed

val SecondaryWarm: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.secondaryWarm

val SecondaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.secondaryContainer

val OnSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.onSecondary

val OnSecondaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.onSecondaryContainer

val OnSurfaceWarm: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.onSurfaceWarm

val OnSurfaceVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.onSurfaceVariant

val OutlineWarm: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.outlineWarm

val OutlineVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.outlineVariant

val TertiaryGreen: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.tertiaryGreen

val TertiaryContainerGreen: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.tertiaryContainerGreen

val OnTertiary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.onTertiary

val OnTertiaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.onTertiaryContainer

val ErrorRed: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.errorRed

val ErrorContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.errorContainer

val OnError: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.onError
