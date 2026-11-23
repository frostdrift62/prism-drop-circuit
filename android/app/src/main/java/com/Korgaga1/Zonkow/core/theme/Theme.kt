package com.Korgaga1.Zonkow.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PrismColorScheme = darkColorScheme(
    primary = AccentViolet,
    onPrimary = TextPrimary,
    primaryContainer = SurfaceElevated,
    onPrimaryContainer = TextPrimary,
    secondary = AccentCyan,
    onSecondary = BaseVoid,
    secondaryContainer = PanelWell,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentMagenta,
    onTertiary = TextPrimary,
    background = BaseVoid,
    onBackground = TextPrimary,
    surface = SurfaceCircuit,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted,
    outlineVariant = InertLane,
    error = AccentMagenta,
    onError = TextPrimary
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PrismColorScheme,
        typography = PrismTypography,
        content = content
    )
}
