package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DaylightStudioColorScheme = lightColorScheme(
    primary = StudioPrimary,
    onPrimary = Color.White,
    primaryContainer = StudioPrimaryContainer,
    onPrimaryContainer = StudioOnPrimaryContainer,
    secondary = StudioSecondary,
    onSecondary = Color.White,
    secondaryContainer = StudioSecondaryContainer,
    onSecondaryContainer = StudioOnSecondaryContainer,
    tertiary = StudioTertiary,
    onTertiary = Color.White,
    tertiaryContainer = StudioTertiaryContainer,
    onTertiaryContainer = StudioOnTertiaryContainer,
    background = StudioBackground,
    onBackground = StudioTextPrimary,
    surface = StudioSurface,
    onSurface = StudioTextPrimary,
    surfaceVariant = StudioSurfaceVariant,
    onSurfaceVariant = StudioTextSecondary,
    outline = StudioBorder,
    error = StudioError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    // Enforced light theme for everyday usage convenience as requested
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DaylightStudioColorScheme,
        typography = Typography,
        content = content
    )
}
