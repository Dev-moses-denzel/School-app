package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class ColorBlindMode {
    NONE, // Standard Palette
    DEUTERANOPIA, // Red-Green Color Blind Friendly (Cobalt Blue / Warm Gold / Deep Purple)
    PROTANOPIA, // Cyan Blue / Warm Gold / Deep Plum
    HIGH_CONTRAST // Ultra High Contrast Black & Yellow
}

private val DarkColorScheme = darkColorScheme(
    primary = BlueLight,
    onPrimary = Color.White,
    primaryContainer = Navy800,
    onPrimaryContainer = BlueContainer,
    secondary = GoldAccent,
    onSecondary = Color.Black,
    secondaryContainer = GoldAccent,
    background = Navy900,
    onBackground = Color.White,
    surface = Navy800,
    onSurface = Color.White,
    surfaceVariant = Navy900,
    onSurfaceVariant = SlateBorder
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = BlueContainer,
    onPrimaryContainer = BluePrimaryDark,
    secondary = GoldAccent,
    onSecondary = Color.White,
    secondaryContainer = GoldContainer,
    background = SlateSurface,
    onBackground = SlateTextDark,
    surface = Color.White,
    onSurface = SlateTextDark,
    surfaceVariant = SlateSurface,
    onSurfaceVariant = SlateTextMuted
)

private val HighContrastColorScheme = darkColorScheme(
    primary = HighContrastYellow,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF27272A),
    onPrimaryContainer = HighContrastYellow,
    secondary = Color(0xFF38BDF8),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0284C7),
    background = HighContrastBackground,
    onBackground = Color.White,
    surface = HighContrastSurface,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF27272A),
    onSurfaceVariant = Color.White
)

@Composable
fun getPassColor(colorBlindMode: ColorBlindMode = ColorBlindMode.NONE): Color {
    return when (colorBlindMode) {
        ColorBlindMode.NONE -> EmeraldPass
        ColorBlindMode.DEUTERANOPIA -> DeuteranopiaPass
        ColorBlindMode.PROTANOPIA -> ProtanopiaPass
        ColorBlindMode.HIGH_CONTRAST -> HighContrastYellow
    }
}

@Composable
fun getPassContainerColor(colorBlindMode: ColorBlindMode = ColorBlindMode.NONE): Color {
    return when (colorBlindMode) {
        ColorBlindMode.NONE -> EmeraldContainer
        ColorBlindMode.DEUTERANOPIA -> DeuteranopiaPassContainer
        ColorBlindMode.PROTANOPIA -> ProtanopiaPassContainer
        ColorBlindMode.HIGH_CONTRAST -> Color(0xFF27272A)
    }
}

@Composable
fun getFailColor(colorBlindMode: ColorBlindMode = ColorBlindMode.NONE): Color {
    return when (colorBlindMode) {
        ColorBlindMode.NONE -> CrimsonFail
        ColorBlindMode.DEUTERANOPIA -> DeuteranopiaFail
        ColorBlindMode.PROTANOPIA -> ProtanopiaFail
        ColorBlindMode.HIGH_CONTRAST -> Color(0xFFEF4444)
    }
}

@Composable
fun getFailContainerColor(colorBlindMode: ColorBlindMode = ColorBlindMode.NONE): Color {
    return when (colorBlindMode) {
        ColorBlindMode.NONE -> CrimsonContainer
        ColorBlindMode.DEUTERANOPIA -> DeuteranopiaFailContainer
        ColorBlindMode.PROTANOPIA -> ProtanopiaFailContainer
        ColorBlindMode.HIGH_CONTRAST -> Color(0xFF450A0A)
    }
}

@Composable
fun ResultHubTheme(
    themeMode: ThemeMode = ThemeMode.LIGHT,
    colorBlindMode: ColorBlindMode = ColorBlindMode.NONE,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = when {
        colorBlindMode == ColorBlindMode.HIGH_CONTRAST -> HighContrastColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun ResultHubTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    ResultHubTheme(
        themeMode = if (darkTheme) ThemeMode.DARK else ThemeMode.LIGHT,
        colorBlindMode = ColorBlindMode.NONE,
        dynamicColor = dynamicColor,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    ResultHubTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

