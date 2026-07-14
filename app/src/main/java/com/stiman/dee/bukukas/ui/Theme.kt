package com.stiman.dee.bukukas.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Core palette
val DarkBackground = Color(0xFF0F172A)
val DarkSurface = Color(0xFF1E293B)
val DarkCard = Color(0xFF1E293B)
val DarkCardVariant = Color(0xFF334155)

val IncomeGreen = Color(0xFF10B981)
val IncomeGreenLight = Color(0xFF34D399)
val ExpenseRed = Color(0xFFEF4444)
val ExpenseRedLight = Color(0xFFF87171)

val AccentWhite = Color(0xFFF1F5F9)
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

private val DarkColorScheme = darkColorScheme(
    primary = IncomeGreen,
    onPrimary = Color.Black,
    secondary = AccentWhite,
    onSecondary = Color.Black,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkCardVariant,
    error = ExpenseRed,
    onError = Color.White
)

@Composable
fun BukuKasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
