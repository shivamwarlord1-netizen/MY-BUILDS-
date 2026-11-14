package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// KinetiQ Calm Obsidian Surface Hierarchy (tinted near-black, tonal layering without harsh borders)
val KinetiqBlack = Color(0xFF0D0A14)            // Background (#0D0A14, never #000)
val KinetiqObsidianSurface = Color(0xFF151120)  // Surface-1
val KinetiqCardSurface = Color(0xFF1D1829)      // Surface-2
val KinetiqElevatedCard = Color(0xFF272136)     // Surface-3
val KinetiqDividerDark = Color(0x14FFFFFF)      // White 8% divider
val KinetiqBorderSubtle = Color(0x14FFFFFF)     // Subtle 8% hairline for legacy callers
val KinetiqBorderGlow = Color(0x28FFFFFF)

// Core Purple Brand Tones (<10% screen usage: primary action, selected nav, hero highlight)
val KinetiqPurplePrimary = Color(0xFF9348EB)
val KinetiqPurpleNeon = Color(0xFFB06CFF)
val KinetiqPurpleLavender = Color(0xFFDCC4FF)
val KinetiqPurpleDeep = Color(0xFF391C5E)

// Ruby Velvet Theme (only the brand accent shifts; surfaces remain calm tonal obsidian)
val RubyDarkBackground = Color(0xFF0D0A14)
val RubyObsidianSurface = Color(0xFF151120)
val RubyCardSurface = Color(0xFF1D1829)
val RubyElevatedCard = Color(0xFF272136)
val RubyBorderSubtle = Color(0x14FFFFFF)
val RubyBorderGlow = Color(0x28FFFFFF)

val RubyPrimary = Color(0xFFE11D48)
val RubyNeon = Color(0xFFFF4774)
val RubyRose = Color(0xFFFDA4AF)
val RubyDeep = Color(0xFF5C1129)

// High-Contrast Accessible Typography Colors (>= 4.5:1 on all dark surfaces)
val KinetiqTextWhite = Color(0xFFF1ECF8)
val KinetiqTextMuted = Color(0xFFA9A1BA)
val KinetiqTextSubtle = Color(0xFF837B96)

// Light Palette (Calm Warm Alabaster & Twilight Ink)
val KinetiqLightBackground = Color(0xFFF7F5FB)
val KinetiqLightSurface = Color(0xFFFFFFFF)
val KinetiqLightSurfaceVariant = Color(0xFFF0ECF7)
val KinetiqLightSurfaceElevated = Color(0xFFE5DFEF)
val KinetiqLightDivider = Color(0x140D0A14)
val KinetiqLightPrimary = Color(0xFF7B2CBF)
val KinetiqLightOnPrimary = Color(0xFFFFFFFF)
val KinetiqLightText = Color(0xFF1A1526)
val KinetiqLightTextMuted = Color(0xFF554D66)

// Simple Palette (Minimal Pure White & Neutral Grey across the entire app)
val SimpleBackground = Color(0xFFF5F5F6)
val SimpleSurface1 = Color(0xFFFFFFFF)
val SimpleSurface2 = Color(0xFFEEEEF0)
val SimpleSurface3 = Color(0xFFE4E4E7)
val SimpleDivider = Color(0x1818181B)
val SimpleOutline = Color(0xFFD4D4D8)
val SimplePrimary = Color(0xFF27272A)
val SimpleOnPrimary = Color(0xFFFFFFFF)
val SimplePrimaryContainer = Color(0xFFE4E4E7)
val SimpleOnPrimaryContainer = Color(0xFF18181B)
val SimpleSecondary = Color(0xFF52525B)
val SimpleSecondaryContainer = Color(0xFFECECEF)
val SimpleOnSecondaryContainer = Color(0xFF27272A)
val SimpleTertiary = Color(0xFF71717A)
val SimpleTextPrimary = Color(0xFF141416)
val SimpleTextSecondary = Color(0xFF5E5E66)

// Dedicated 4-Pillar Colors (Dark/Ruby)
val PillarStepsDark = Color(0xFF6EE7A8)
val PillarWaterDark = Color(0xFF5CC8FF)
val PillarSleepDark = Color(0xFF8FA2FF)
val PillarHabitsDark = Color(0xFFFFC857)

// Contrast-Adjusted 4-Pillar Colors (Light surfaces >= 4.5:1)
val PillarStepsLight = Color(0xFF0E8348)
val PillarWaterLight = Color(0xFF0272B0)
val PillarSleepLight = Color(0xFF4338CA)
val PillarHabitsLight = Color(0xFFAD5200)

// Monochrome 4-Pillar Colors (Simple White & Grey theme >= 4.5:1)
val PillarStepsSimple = Color(0xFF18181B)
val PillarWaterSimple = Color(0xFF52525B)
val PillarSleepSimple = Color(0xFF71717A)
val PillarHabitsSimple = Color(0xFF3F3F46)

// Legacy Pillar Tokens preserved for Insights/CommonComponents compatibility
val KineticViolet = PillarStepsDark
val KineticVioletTrack = Color(0xFF162C24)
val BioCyan = PillarWaterDark
val BioCyanTrack = Color(0xFF132635)
val DuskIndigo = PillarSleepDark
val DuskIndigoTrack = Color(0xFF1D213B)
val RadiantAmber = PillarHabitsDark
val RadiantAmberTrack = Color(0xFF332815)

val ActivityGreen = PillarStepsDark
val HydrationBlue = PillarWaterDark
val SleepPurple = PillarSleepDark
val HabitAmber = PillarHabitsDark
