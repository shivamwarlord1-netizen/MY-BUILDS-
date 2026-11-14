package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Immutable
data class KinetiqSemanticColors(
  val background: Color,
  val surface1: Color,
  val surface2: Color,
  val surface3: Color,
  val divider: Color,
  val textPrimary: Color,
  val textSecondary: Color,
  val steps: Color,
  val water: Color,
  val sleep: Color,
  val habits: Color,
  val isLight: Boolean
)

private val DarkSemanticColors = KinetiqSemanticColors(
  background = KinetiqBlack,
  surface1 = KinetiqObsidianSurface,
  surface2 = KinetiqCardSurface,
  surface3 = KinetiqElevatedCard,
  divider = KinetiqDividerDark,
  textPrimary = KinetiqTextWhite,
  textSecondary = KinetiqTextMuted,
  steps = PillarStepsDark,
  water = PillarWaterDark,
  sleep = PillarSleepDark,
  habits = PillarHabitsDark,
  isLight = false
)

private val LightSemanticColors = KinetiqSemanticColors(
  background = KinetiqLightBackground,
  surface1 = KinetiqLightSurface,
  surface2 = KinetiqLightSurfaceVariant,
  surface3 = KinetiqLightSurfaceElevated,
  divider = KinetiqLightDivider,
  textPrimary = KinetiqLightText,
  textSecondary = KinetiqLightTextMuted,
  steps = PillarStepsLight,
  water = PillarWaterLight,
  sleep = PillarSleepLight,
  habits = PillarHabitsLight,
  isLight = true
)

private val SimpleSemanticColors = KinetiqSemanticColors(
  background = SimpleBackground,
  surface1 = SimpleSurface1,
  surface2 = SimpleSurface2,
  surface3 = SimpleSurface3,
  divider = SimpleDivider,
  textPrimary = SimpleTextPrimary,
  textSecondary = SimpleTextSecondary,
  steps = PillarStepsSimple,
  water = PillarWaterSimple,
  sleep = PillarSleepSimple,
  habits = PillarHabitsSimple,
  isLight = true
)

val LocalKinetiqColors = staticCompositionLocalOf { DarkSemanticColors }

val MaterialTheme.kinetiqColors: KinetiqSemanticColors
  @Composable
  get() = LocalKinetiqColors.current

// Obsidian Purple Dark Color Scheme
private val ObsidianPurpleColorScheme = darkColorScheme(
  primary = KinetiqPurpleNeon,
  onPrimary = KinetiqBlack,
  primaryContainer = KinetiqPurpleDeep,
  onPrimaryContainer = KinetiqPurpleLavender,
  secondary = KinetiqPurplePrimary,
  onSecondary = Color.White,
  secondaryContainer = KinetiqElevatedCard,
  onSecondaryContainer = KinetiqPurpleLavender,
  tertiary = PillarHabitsDark,
  onTertiary = KinetiqBlack,
  tertiaryContainer = RadiantAmberTrack,
  onTertiaryContainer = PillarHabitsDark,
  background = KinetiqBlack,
  onBackground = KinetiqTextWhite,
  surface = KinetiqObsidianSurface,
  onSurface = KinetiqTextWhite,
  surfaceVariant = KinetiqCardSurface,
  onSurfaceVariant = KinetiqTextMuted,
  surfaceContainerLowest = KinetiqBlack,
  surfaceContainerLow = KinetiqObsidianSurface,
  surfaceContainer = KinetiqCardSurface,
  surfaceContainerHigh = KinetiqElevatedCard,
  surfaceContainerHighest = KinetiqElevatedCard,
  outline = KinetiqDividerDark,
  outlineVariant = KinetiqDividerDark
)

// Ruby Velvet Red Color Scheme (only brand accent changes; surfaces match Obsidian calm tone)
private val RubyVelvetColorScheme = darkColorScheme(
  primary = RubyNeon,
  onPrimary = KinetiqBlack,
  primaryContainer = RubyDeep,
  onPrimaryContainer = RubyRose,
  secondary = RubyPrimary,
  onSecondary = Color.White,
  secondaryContainer = RubyElevatedCard,
  onSecondaryContainer = RubyRose,
  tertiary = PillarHabitsDark,
  onTertiary = KinetiqBlack,
  tertiaryContainer = RadiantAmberTrack,
  onTertiaryContainer = PillarHabitsDark,
  background = RubyDarkBackground,
  onBackground = KinetiqTextWhite,
  surface = RubyObsidianSurface,
  onSurface = KinetiqTextWhite,
  surfaceVariant = RubyCardSurface,
  onSurfaceVariant = KinetiqTextMuted,
  surfaceContainerLowest = RubyDarkBackground,
  surfaceContainerLow = RubyObsidianSurface,
  surfaceContainer = RubyCardSurface,
  surfaceContainerHigh = RubyElevatedCard,
  surfaceContainerHighest = RubyElevatedCard,
  outline = RubyBorderSubtle,
  outlineVariant = RubyBorderSubtle
)

// Calm Twilight Light Color Scheme
private val TwilightLightColorScheme = lightColorScheme(
  primary = KinetiqLightPrimary,
  onPrimary = KinetiqLightOnPrimary,
  primaryContainer = Color(0xFFEADBFC),
  onPrimaryContainer = Color(0xFF2E0854),
  secondary = KinetiqPurplePrimary,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFEFE8FA),
  onSecondaryContainer = Color(0xFF38106A),
  tertiary = PillarHabitsLight,
  onTertiary = Color.White,
  background = KinetiqLightBackground,
  onBackground = KinetiqLightText,
  surface = KinetiqLightSurface,
  onSurface = KinetiqLightText,
  surfaceVariant = KinetiqLightSurfaceVariant,
  onSurfaceVariant = KinetiqLightTextMuted,
  surfaceContainerLowest = KinetiqLightBackground,
  surfaceContainerLow = KinetiqLightSurface,
  surfaceContainer = KinetiqLightSurfaceVariant,
  surfaceContainerHigh = KinetiqLightSurfaceElevated,
  surfaceContainerHighest = KinetiqLightSurfaceElevated,
  outline = KinetiqLightDivider,
  outlineVariant = KinetiqLightDivider
)

// Simple Minimal White & Grey Color Scheme
private val SimpleWhiteGreyColorScheme = lightColorScheme(
  primary = SimplePrimary,
  onPrimary = SimpleOnPrimary,
  primaryContainer = SimplePrimaryContainer,
  onPrimaryContainer = SimpleOnPrimaryContainer,
  secondary = SimpleSecondary,
  onSecondary = SimpleOnPrimary,
  secondaryContainer = SimpleSecondaryContainer,
  onSecondaryContainer = SimpleOnSecondaryContainer,
  tertiary = SimpleTertiary,
  onTertiary = SimpleOnPrimary,
  tertiaryContainer = SimpleSurface2,
  onTertiaryContainer = SimpleTextPrimary,
  background = SimpleBackground,
  onBackground = SimpleTextPrimary,
  surface = SimpleSurface1,
  onSurface = SimpleTextPrimary,
  surfaceVariant = SimpleSurface2,
  onSurfaceVariant = SimpleTextSecondary,
  surfaceContainerLowest = SimpleBackground,
  surfaceContainerLow = SimpleSurface1,
  surfaceContainer = SimpleSurface2,
  surfaceContainerHigh = SimpleSurface3,
  surfaceContainerHighest = SimpleSurface3,
  outline = SimpleOutline,
  outlineVariant = SimpleDivider
)

@Composable
fun AuraTheme(
  themeMode: String = "dark",
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val isSimple = themeMode.equals("simple", ignoreCase = true)
  val isRuby = !isSimple && (themeMode == "ruby" || themeMode == "red")
  val isLight = isSimple || themeMode == "light" || (!darkTheme && !isRuby)

  val colorScheme = when {
    isSimple -> SimpleWhiteGreyColorScheme
    isRuby -> RubyVelvetColorScheme
    isLight -> TwilightLightColorScheme
    else -> ObsidianPurpleColorScheme
  }

  val semanticColors = when {
    isSimple -> SimpleSemanticColors
    isLight -> LightSemanticColors
    else -> DarkSemanticColors
  }

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = isLight
        controller.isAppearanceLightNavigationBars = isLight
      }
    }
  }

  CompositionLocalProvider(LocalKinetiqColors provides semanticColors) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}
