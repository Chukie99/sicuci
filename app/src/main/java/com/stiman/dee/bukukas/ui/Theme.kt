package com.stiman.dee.bukukas.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = TextOnPrimary,
    secondary = Accent,
    onSecondary = TextOnAccent,
    secondaryContainer = AccentLight,
    onSecondaryContainer = TextPrimary,
    tertiary = Success,
    onTertiary = TextOnPrimary,
    background = Surface,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCardAlt,
    onSurfaceVariant = TextSecondary,
    error = Danger,
    onError = TextOnPrimary,
    outline = Border,
    outlineVariant = Divider
)

@Composable
fun BukuKasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        content = content
    )
}
