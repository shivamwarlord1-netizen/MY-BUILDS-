package com.example.ui.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Custom vector renderer for the official KinetiQ emblem:
 * Features a smooth breathing kinetic halo, gently sweeping gradient arc ring,
 * and a completely static, crisp modern 'K' geometry.
 */
@Composable
fun KinetiQLogo(
  modifier: Modifier = Modifier,
  size: Dp = 38.dp,
  animatePulse: Boolean = true,
  ringGradient: List<Color> = listOf(
    MaterialTheme.colorScheme.secondary,
    MaterialTheme.colorScheme.primary,
    MaterialTheme.colorScheme.tertiary
  )
) {
  val colorScheme = MaterialTheme.colorScheme
  val seamlessGradient = remember(ringGradient) {
    if (ringGradient.size >= 2) ringGradient + ringGradient.first() else ringGradient
  }

  val infiniteTransition = rememberInfiniteTransition(label = "kinetiq_logo_motion")

  val sweepProgress by if (animatePulse) {
    infiniteTransition.animateFloat(
      initialValue = 0f,
      targetValue = 1f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 7000, easing = EaseInOutSine),
        repeatMode = RepeatMode.Reverse
      ),
      label = "logo_sweep_progress"
    )
  } else {
    androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0.5f) }
  }

  val breathProgress by if (animatePulse) {
    infiniteTransition.animateFloat(
      initialValue = 0f,
      targetValue = 1f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 6500, easing = EaseInOutSine),
        repeatMode = RepeatMode.Reverse
      ),
      label = "logo_breath_progress"
    )
  } else {
    androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0.5f) }
  }

  val primaryAccent = colorScheme.primary
  val stemColor = colorScheme.onBackground
  val trackColor = colorScheme.onBackground.copy(alpha = 0.08f)

  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size)) {
      val w = this.size.width
      val scale = w / 200f
      val strokeW = 12f * scale
      val center = Offset(w / 2f, this.size.height / 2f)
      val radius = 56f * scale

      // 1. Subtle Ambient Breathing Halo Ring
      val haloRadius = radius * (1.0f + 0.055f * breathProgress)
      val haloAlpha = 0.07f + (0.14f * breathProgress)
      drawCircle(
        color = primaryAccent.copy(alpha = haloAlpha),
        radius = haloRadius,
        center = center,
        style = Stroke(width = strokeW * (1.35f + 0.25f * breathProgress))
      )

      // 2. Background Track Circle
      drawCircle(
        color = trackColor,
        radius = radius,
        center = center,
        style = Stroke(width = strokeW)
      )

      // 3. Synchronized Kinetic Gradient Arc Sweep
      val sweepRotation = -16f + (32f * sweepProgress)
      val dynamicSweep = 256f + (28f * breathProgress)
      val dynamicStart = 142f - (14f * breathProgress)

      rotate(degrees = sweepRotation, pivot = center) {
        val gradientBrush = Brush.sweepGradient(
          colors = seamlessGradient,
          center = center
        )

        drawArc(
          brush = gradientBrush,
          startAngle = dynamicStart,
          sweepAngle = dynamicSweep,
          useCenter = false,
          topLeft = Offset(center.x - radius, center.y - radius),
          size = Size(radius * 2, radius * 2),
          style = Stroke(width = strokeW, cap = StrokeCap.Round)
        )
      }

      // 4. Completely Static Stylized Letter K
      // Vertical Stem: M83 73 V127
      drawLine(
        color = stemColor,
        start = Offset(83f * scale, 73f * scale),
        end = Offset(83f * scale, 127f * scale),
        strokeWidth = strokeW,
        cap = StrokeCap.Round
      )

      // Upper Diagonal Branch: M83 103 L116 73
      drawLine(
        color = stemColor,
        start = Offset(83f * scale, 103f * scale),
        end = Offset(116f * scale, 73f * scale),
        strokeWidth = strokeW,
        cap = StrokeCap.Round
      )

      // Lower Diagonal Branch: M98 89 L118 127
      drawLine(
        color = primaryAccent,
        start = Offset(98f * scale, 89f * scale),
        end = Offset(118f * scale, 127f * scale),
        strokeWidth = strokeW,
        cap = StrokeCap.Round
      )
    }
  }
}
