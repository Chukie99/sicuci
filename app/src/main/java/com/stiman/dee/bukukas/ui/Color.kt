package com.stiman.dee.bukukas.ui

import androidx.compose.ui.graphics.Color

// Primary palette — deep indigo-navy, professional & trustworthy
val Primary = Color(0xFF1A2744)
val PrimaryLight = Color(0xFF243656)
val PrimaryDark = Color(0xFF0F1A2E)

// Surface colors — warm off-white, not sterile
val Surface = Color(0xFFF7F8FA)
val SurfaceCard = Color(0xFFFFFFFF)
val SurfaceCardAlt = Color(0xFFF0F2F5)

// Accent — warm amber, for actions and CTAs
val Accent = Color(0xFFE8913A)
val AccentLight = Color(0xFFF0A85C)
val AccentDark = Color(0xFFD07A28)

// Success — teal-green, calm not neon
val Success = Color(0xFF2D9C8A)
val SuccessLight = Color(0xFF3DB8A8)
val SuccessDark = Color(0xFF1E7A6A)

// Danger — muted coral, not screaming red
val Danger = Color(0xFFD94F4F)
val DangerLight = Color(0xFFE87070)
val DangerDark = Color(0xFFB83A3A)

// Warning
val Warning = Color(0xFFE5A93B)

// Info
val Info = Color(0xFF4A7FBF)

// Text hierarchy
val TextPrimary = Color(0xFF1A2744)
val TextSecondary = Color(0xFF5A6B82)
val TextMuted = Color(0xFF8E9BB0)
val TextOnPrimary = Color(0xFFFFFFFF)
val TextOnAccent = Color(0xFFFFFFFF)

// Borders & dividers
val Border = Color(0xFFE2E6EC)
val Divider = Color(0xFFEEF0F4)

// Status colors (for queue)
val StatusWaiting = Color(0xFFE5A93B)
val StatusWashing = Color(0xFF4A7FBF)
val StatusDone = Color(0xFF2D9C8A)

// Legacy aliases (for backward compatibility)
val DarkBackground = Surface
val DarkSurface = SurfaceCard
val DarkCard = SurfaceCard
val DarkCardVariant = SurfaceCardAlt

val IncomeGreen = Success
val IncomeGreenLight = SuccessLight
val ExpenseRed = Danger
val ExpenseRedLight = DangerLight

val AccentWhite = TextOnPrimary

// More legacy aliases
val Slate900 = Primary
val Slate800 = SurfaceCardAlt
val Slate700 = Border
val Slate600 = Border
val Slate500 = TextMuted
val Slate400 = TextMuted

val BlueAccent = Info
val Gold = Warning

val IncomeGreenDark = SuccessDark
val ExpenseRedDark = DangerDark

val NeonGreen = SuccessLight
val NeonGreenDark = Success
val CoralRed = DangerLight
val CoralRedDark = Danger
