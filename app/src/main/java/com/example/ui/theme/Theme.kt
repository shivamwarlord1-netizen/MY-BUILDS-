package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Premium Obsidian & Royal Purple Dark Color Scheme
private val ObsidianPurpleColorScheme = darkColorScheme(
  primary = KinetiqPurpleNeon,
  onPrimary = KinetiqBlack,
  primaryContainer = KinetiqPurpleDeep,
  onPrimaryContainer = KinetiqPurpleLavender,
  secondary = KinetiqPurplePrimary,
  onSecondary = Color.White,
  secondaryContainer = KinetiqElevatedCard,
  onSecondaryContainer = KinetiqPurpleLavender,
  tertiary = RadiantAmber,
  onTertiary = KinetiqBlack,
  tertiaryContainer = RadiantAmberTrack,
  onTertiaryContainer = RadiantAmber,
  background = KinetiqBlack,
  onBackground = KinetiqTextWhite,
  surface = KinetiqObsidianSurface,
  onSurface = KinetiqTextWhite,
  surfaceVariant = KinetiqCardSurface,
  onSurfaceVariant = KinetiqTextMuted,
  outline = KinetiqBorderSubtle,
  outlineVariant = KinetiqBorderGlow
)

// Premium Ruby Velvet Red Luxury Color Scheme
private val RubyVelvetColorScheme = darkColorScheme(
  primary = RubyNeon,
  onPrimary = RubyDarkBackground,
  primaryContainer = RubyDeep,
  onPrimaryContainer = RubyRose,
  secondary = RubyPrimary,
  onSecondary = Color.White,
  secondaryContainer = RubyElevatedCard,
  onSecondaryContainer = RubyRose,
  tertiary = RadiantAmber,
  onTertiary = RubyDarkBackground,
  tertiaryContainer = RadiantAmberTrack,
  onTertiaryContainer = RadiantAmber,
  background = RubyDarkBackground,
  onBackground = KinetiqTextWhite,
  surface = RubyObsidianSurface,
  onSurface = KinetiqTextWhite,
  surfaceVariant = RubyCardSurface,
  onSurfaceVariant = KinetiqTextMuted,
  outline = RubyBorderSubtle,
  outlineVariant = RubyBorderGlow
)

private val TwilightLightColorScheme = lightColorScheme(
  primary = KinetiqLightPrimary,
  onPrimary = KinetiqLightOnPrimary,
  primaryContainer = Color(0xFFEADBFC),
  onPrimaryContainer = Color(0xFF2E0854),
  secondary = KinetiqPurplePrimary,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFF1E9FA),
  onSecondaryContainer = Color(0xFF38106A),
  tertiary = Color(0xFFD97706),
  onTertiary = Color.White,
  background = KinetiqLightBackground,
  onBackground = KinetiqLightText,
  surface = KinetiqLightSurface,
  onSurface = KinetiqLightText,
  surfaceVariant = KinetiqLightSurfaceVariant,
  onSurfaceVariant = Color(0xFF4C3E5E),
  outline = Color(0xFFC7B8DA)
)

@Composable
fun AuraTheme(
  themeMode: String = "dark",
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    themeMode == "ruby" || themeMode == "red" -> RubyVelvetColorScheme
    themeMode == "light" || (!darkTheme && themeMode != "ruby" && themeMode != "red") -> TwilightLightColorScheme
    else -> ObsidianPurpleColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
