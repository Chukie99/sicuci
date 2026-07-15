package com.stiman.dee.bukukas.ui

import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════════════════
// PRIMARY PALETTE
// ══════════════════════════════════════════════════════════════

// Primary — deep navy for headers and key elements
val Primary = Color(0xFF1B2A4A)
val PrimaryLight = Color(0xFF2C3E6B)
val PrimaryDark = Color(0xFF0F1B33)

// Accent — warm amber/orange for CTAs and highlights
val Accent = Color(0xFFF0932B)
val AccentLight = Color(0xFFF5B041)
val AccentDark = Color(0xFFD68910)

// ══════════════════════════════════════════════════════════════
// SURFACE & BACKGROUND
// ══════════════════════════════════════════════════════════════

// Background — clean light gray
val Surface = Color(0xFFF5F6FA)
val SurfaceCard = Color(0xFFFFFFFF)
val SurfaceCardAlt = Color(0xFFEEF0F5)

// ══════════════════════════════════════════════════════════════
// STATUS COLORS
// ══════════════════════════════════════════════════════════════

// Status — queue workflow
val StatusWaiting = Color(0xFFF39C12)   // Orange/amber — waiting
val StatusWashing = Color(0xFF3498DB)   // Blue — in progress
val StatusDone = Color(0xFF27AE60)      // Green — completed

// Success / Income — calm green
val Success = Color(0xFF27AE60)
val SuccessLight = Color(0xFF2ECC71)

// Danger / Expense — muted red
val Danger = Color(0xFFE74C3C)
val DangerLight = Color(0xFFEC7063)

// Warning
val Warning = Color(0xFFF39C12)

// Info
val Info = Color(0xFF3498DB)

// ══════════════════════════════════════════════════════════════
// TEXT HIERARCHY
// ══════════════════════════════════════════════════════════════

val TextPrimary = Color(0xFF1B2A4A)
val TextSecondary = Color(0xFF6B7B8D)
val TextMuted = Color(0xFF9DA8B5)
val TextOnPrimary = Color(0xFFFFFFFF)
val TextOnAccent = Color(0xFFFFFFFF)

// ══════════════════════════════════════════════════════════════
// BORDERS & DIVIDERS
// ══════════════════════════════════════════════════════════════

val Border = Color(0xFFE0E4EA)
val Divider = Color(0xFFECEEF2)

// ══════════════════════════════════════════════════════════════
// ALIASES (backward compatibility)
// ══════════════════════════════════════════════════════════════

val DarkBackground = Surface
val DarkSurface = SurfaceCard
val DarkCard = SurfaceCard
val DarkCardVariant = SurfaceCardAlt

val IncomeGreen = Success
val IncomeGreenLight = SuccessLight
val ExpenseRed = Danger
val ExpenseRedLight = DangerLight

val AccentWhite = TextOnPrimary

val Slate900 = Primary
val Slate800 = SurfaceCardAlt
val Slate700 = Border
val Slate600 = Border
val Slate500 = TextMuted
val Slate400 = TextMuted

val BlueAccent = Info
val Gold = Warning

val IncomeGreenDark = Success
val ExpenseRedDark = Danger

val NeonGreen = SuccessLight
val NeonGreenDark = Success
val CoralRed = DangerLight
val CoralRedDark = Danger
