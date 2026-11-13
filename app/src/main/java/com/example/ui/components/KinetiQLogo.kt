package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.KinetiqPurpleLavender
import com.example.ui.theme.KinetiqPurpleNeon
import com.example.ui.theme.KinetiqPurplePrimary

/**
 * Custom vector renderer for the official KinetiQ emblem:
 * Features the kinetic activity arc ring and stylized modern 'K' geometry.
 */
@Composable
fun KinetiQLogo(
  modifier: Modifier = Modifier,
  size: Dp = 38.dp,
  animatePulse: Boolean = true,
  ringGradient: List<Color> = listOf(KinetiqPurpleLavender, KinetiqPurpleNeon, KinetiqPurplePrimary)
) {
  val infiniteTransition = rememberInfiniteTransition(label = "kinetiq_logo_rotation")
  val pulseAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = if (animatePulse) 360f else 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 12000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "logo_angle"
  )

  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size)) {
      val w = this.size.width
      val h = this.size.height
      val scale = w / 200f
      val strokeW = 12f * scale
      val center = Offset(w / 2f, h / 2f)
      val radius = 56f * scale

      // 1. Background Track Circle
      drawCircle(
        color = Color.White.copy(alpha = 0.08f),
        radius = radius,
        center = center,
        style = Stroke(width = strokeW)
      )

      // 2. Kinetic Gradient Arc Ring
      val gradientBrush = Brush.sweepGradient(
        colors = ringGradient,
        center = center
      )

      drawArc(
        brush = gradientBrush,
        startAngle = 135f + (pulseAngle * 0.15f),
        sweepAngle = 270f,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2),
        style = Stroke(width = strokeW, cap = StrokeCap.Round)
      )

      // 3. Stylized Letter K
      // Vertical Stem: M83 73 V127
      drawLine(
        color = Color(0xFFFAF7FD),
        start = Offset(83f * scale, 73f * scale),
        end = Offset(83f * scale, 127f * scale),
        strokeWidth = strokeW,
        cap = StrokeCap.Round
      )

      // Upper Diagonal Branch: M83 103 L116 73
      drawLine(
        color = Color(0xFFFAF7FD),
        start = Offset(83f * scale, 103f * scale),
        end = Offset(116f * scale, 73f * scale),
        strokeWidth = strokeW,
        cap = StrokeCap.Round
      )

      // Lower Diagonal Branch: M98 89 L118 127
      drawLine(
        color = KinetiqPurpleNeon,
        start = Offset(98f * scale, 89f * scale),
        end = Offset(118f * scale, 127f * scale),
        strokeWidth = strokeW,
        cap = StrokeCap.Round
      )
    }
  }
}
