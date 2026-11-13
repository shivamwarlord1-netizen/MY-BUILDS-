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

val KinetiqBrandFontFamily = FontFamily(
  Font(
    resId = R.font.plus_jakarta_sans,
    weight = FontWeight.Bold,
    variationSettings = FontVariation.Settings(FontVariation.weight(700))
  ),
  Font(
    resId = R.font.plus_jakarta_sans,
    weight = FontWeight.SemiBold,
    variationSettings = FontVariation.Settings(FontVariation.weight(600))
  ),
  Font(
    resId = R.font.plus_jakarta_sans,
    weight = FontWeight.Normal,
    variationSettings = FontVariation.Settings(FontVariation.weight(400))
  )
)

val KinetiqBrandTitleStyle = TextStyle(
  fontFamily = KinetiqBrandFontFamily,
  fontWeight = FontWeight.Bold,
  fontSize = 22.sp,
  lineHeight = 28.sp,
  letterSpacing = (-0.2).sp
)

// Set of Material typography styles to start with
val Typography =
  Typography(
    bodyLarge =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
      )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
  )
