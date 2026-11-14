@file:OptIn(ExperimentalTextApi::class)

package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val BricolageGrotesqueFontFamily = FontFamily(
  Font(
    resId = R.font.bricolage_grotesque,
    weight = FontWeight.Bold,
    variationSettings = FontVariation.Settings(FontVariation.weight(700))
  ),
  Font(
    resId = R.font.bricolage_grotesque,
    weight = FontWeight.SemiBold,
    variationSettings = FontVariation.Settings(FontVariation.weight(600))
  ),
  Font(
    resId = R.font.bricolage_grotesque,
    weight = FontWeight.Medium,
    variationSettings = FontVariation.Settings(FontVariation.weight(500))
  ),
  Font(
    resId = R.font.bricolage_grotesque,
    weight = FontWeight.Normal,
    variationSettings = FontVariation.Settings(FontVariation.weight(400))
  )
)

val FigtreeFontFamily = FontFamily(
  Font(
    resId = R.font.figtree,
    weight = FontWeight.Bold,
    variationSettings = FontVariation.Settings(FontVariation.weight(700))
  ),
  Font(
    resId = R.font.figtree,
    weight = FontWeight.SemiBold,
    variationSettings = FontVariation.Settings(FontVariation.weight(600))
  ),
  Font(
    resId = R.font.figtree,
    weight = FontWeight.Medium,
    variationSettings = FontVariation.Settings(FontVariation.weight(500))
  ),
  Font(
    resId = R.font.figtree,
    weight = FontWeight.Normal,
    variationSettings = FontVariation.Settings(FontVariation.weight(400))
  ),
  Font(
    resId = R.font.figtree,
    weight = FontWeight.Light,
    variationSettings = FontVariation.Settings(FontVariation.weight(300))
  )
)

// Preserved brand typography symbols referenced across screens/components
val KinetiqBrandFontFamily = BricolageGrotesqueFontFamily

val KinetiqBrandTitleStyle = TextStyle(
  fontFamily = BricolageGrotesqueFontFamily,
  fontWeight = FontWeight.SemiBold,
  fontSize = 22.sp,
  lineHeight = 28.sp,
  letterSpacing = (-0.2).sp
)

// Dedicated Numeric & Hero Type Tokens
val KinetiqHeroScoreStyle = TextStyle(
  fontFamily = FigtreeFontFamily,
  fontWeight = FontWeight.Light,
  fontSize = 72.sp,
  lineHeight = 76.sp,
  letterSpacing = (-2.0).sp,
  fontFeatureSettings = "tnum"
)

val KinetiqValueTextStyle = TextStyle(
  fontFamily = FigtreeFontFamily,
  fontWeight = FontWeight.Medium,
  fontSize = 18.sp,
  lineHeight = 24.sp,
  fontFeatureSettings = "tnum"
)

val Typography = Typography(
  displayLarge = KinetiqHeroScoreStyle,
  headlineLarge = TextStyle(
    fontFamily = BricolageGrotesqueFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 28.sp,
    lineHeight = 34.sp,
    letterSpacing = (-0.4).sp
  ),
  headlineSmall = TextStyle(
    fontFamily = BricolageGrotesqueFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 22.sp,
    lineHeight = 28.sp,
    letterSpacing = (-0.2).sp
  ),
  titleLarge = TextStyle(
    fontFamily = BricolageGrotesqueFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 22.sp,
    lineHeight = 28.sp,
    letterSpacing = (-0.2).sp
  ),
  titleMedium = TextStyle(
    fontFamily = BricolageGrotesqueFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 24.sp,
    letterSpacing = (-0.1).sp
  ),
  titleSmall = TextStyle(
    fontFamily = FigtreeFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    fontFeatureSettings = "tnum"
  ),
  bodyLarge = TextStyle(
    fontFamily = FigtreeFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    fontFeatureSettings = "tnum"
  ),
  bodyMedium = TextStyle(
    fontFamily = FigtreeFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    fontFeatureSettings = "tnum"
  ),
  bodySmall = TextStyle(
    fontFamily = FigtreeFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    fontFeatureSettings = "tnum"
  ),
  labelLarge = TextStyle(
    fontFamily = FigtreeFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    fontFeatureSettings = "tnum"
  ),
  labelMedium = TextStyle(
    fontFamily = FigtreeFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    fontFeatureSettings = "tnum"
  ),
  labelSmall = TextStyle(
    fontFamily = FigtreeFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    fontFeatureSettings = "tnum"
  )
)
