package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.exercise.AnatomyType
import com.example.ui.theme.KinetiqBlack
import com.example.ui.theme.KinetiqBorderSubtle
import com.example.ui.theme.KinetiqObsidianSurface
import com.example.ui.theme.KinetiqTextMuted
import com.example.ui.theme.KinetiqTextSubtle
import com.example.ui.theme.KinetiqTextWhite

/**
 * Visual anatomical muscle targeting diagram:
 * Renders an athletic anatomical silhouette and highlights target muscle heads
 * in a high-contrast glowing neon aesthetic.
 */
@Composable
fun MuscleAnatomyDiagram(
  anatomyType: AnatomyType,
  modifier: Modifier = Modifier,
  height: Dp = 190.dp,
  highlightColor: Color = MaterialTheme.colorScheme.primary
) {
  val infiniteTransition = rememberInfiniteTransition(label = "muscle_glow")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.55f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .height(height)
      .border(0.8.dp, KinetiqBorderSubtle, RoundedCornerShape(18.dp)),
    shape = RoundedCornerShape(18.dp),
    color = KinetiqObsidianSurface
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Anatomy Silhouette Canvas
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(width = 110.dp, height = 160.dp)
      ) {
        Canvas(modifier = Modifier.size(width = 100.dp, height = 150.dp)) {
          val w = size.width
          val h = size.height
          val baseColor = Color.White.copy(alpha = 0.12f)
          val activeColor = highlightColor.copy(alpha = pulseAlpha)

          // 1. Head & Neck
          drawCircle(color = baseColor, radius = 9.dp.toPx(), center = Offset(w * 0.5f, h * 0.11f))
          drawLine(color = baseColor, start = Offset(w * 0.5f, h * 0.16f), end = Offset(w * 0.5f, h * 0.20f), strokeWidth = 5.dp.toPx())

          // 2. Torso Outline (Ribcage to Pelvis)
          drawRoundRect(
            color = baseColor,
            topLeft = Offset(w * 0.32f, h * 0.20f),
            size = Size(w * 0.36f, h * 0.32f),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
          )

          // 3. Arms Outline
          drawLine(color = baseColor, start = Offset(w * 0.32f, h * 0.22f), end = Offset(w * 0.20f, h * 0.40f), strokeWidth = 7.dp.toPx(), cap = StrokeCap.Round)
          drawLine(color = baseColor, start = Offset(w * 0.20f, h * 0.40f), end = Offset(w * 0.16f, h * 0.56f), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)

          drawLine(color = baseColor, start = Offset(w * 0.68f, h * 0.22f), end = Offset(w * 0.80f, h * 0.40f), strokeWidth = 7.dp.toPx(), cap = StrokeCap.Round)
          drawLine(color = baseColor, start = Offset(w * 0.80f, h * 0.40f), end = Offset(w * 0.84f, h * 0.56f), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)

          // 4. Legs Outline
          drawLine(color = baseColor, start = Offset(w * 0.40f, h * 0.52f), end = Offset(w * 0.38f, h * 0.74f), strokeWidth = 10.dp.toPx(), cap = StrokeCap.Round)
          drawLine(color = baseColor, start = Offset(w * 0.38f, h * 0.74f), end = Offset(w * 0.36f, h * 0.95f), strokeWidth = 7.dp.toPx(), cap = StrokeCap.Round)

          drawLine(color = baseColor, start = Offset(w * 0.60f, h * 0.52f), end = Offset(w * 0.62f, h * 0.74f), strokeWidth = 10.dp.toPx(), cap = StrokeCap.Round)
          drawLine(color = baseColor, start = Offset(w * 0.62f, h * 0.74f), end = Offset(w * 0.64f, h * 0.95f), strokeWidth = 7.dp.toPx(), cap = StrokeCap.Round)

          // 5. TARGET MUSCLE HIGHLIGHT OVERLAYS
          when (anatomyType) {
            AnatomyType.SHOULDERS -> {
              // Left & Right Deltoid Caps
              drawCircle(color = activeColor, radius = 7.dp.toPx(), center = Offset(w * 0.28f, h * 0.22f))
              drawCircle(color = activeColor, radius = 7.dp.toPx(), center = Offset(w * 0.72f, h * 0.22f))
            }
            AnatomyType.CHEST -> {
              // Pectoral Plates
              drawRoundRect(
                color = activeColor,
                topLeft = Offset(w * 0.34f, h * 0.22f),
                size = Size(w * 0.14f, h * 0.10f),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
              )
              drawRoundRect(
                color = activeColor,
                topLeft = Offset(w * 0.52f, h * 0.22f),
                size = Size(w * 0.14f, h * 0.10f),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
              )
            }
            AnatomyType.BACK -> {
              // Latissimus Dorsi & Traps (V-Shape Wings)
              drawRoundRect(
                color = activeColor,
                topLeft = Offset(w * 0.30f, h * 0.21f),
                size = Size(w * 0.40f, h * 0.18f),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
              )
            }
            AnatomyType.BICEPS -> {
              // Front Upper Arm Biceps
              drawLine(color = activeColor, start = Offset(w * 0.25f, h * 0.26f), end = Offset(w * 0.21f, h * 0.38f), strokeWidth = 8.dp.toPx(), cap = StrokeCap.Round)
              drawLine(color = activeColor, start = Offset(w * 0.75f, h * 0.26f), end = Offset(w * 0.79f, h * 0.38f), strokeWidth = 8.dp.toPx(), cap = StrokeCap.Round)
            }
            AnatomyType.TRICEPS -> {
              // Posterior Arm Triceps
              drawLine(color = activeColor, start = Offset(w * 0.24f, h * 0.24f), end = Offset(w * 0.20f, h * 0.39f), strokeWidth = 9.dp.toPx(), cap = StrokeCap.Round)
              drawLine(color = activeColor, start = Offset(w * 0.76f, h * 0.24f), end = Offset(w * 0.80f, h * 0.39f), strokeWidth = 9.dp.toPx(), cap = StrokeCap.Round)
            }
            AnatomyType.QUADS -> {
              // Anterior Thigh Quadriceps
              drawLine(color = activeColor, start = Offset(w * 0.40f, h * 0.54f), end = Offset(w * 0.38f, h * 0.72f), strokeWidth = 11.dp.toPx(), cap = StrokeCap.Round)
              drawLine(color = activeColor, start = Offset(w * 0.60f, h * 0.54f), end = Offset(w * 0.62f, h * 0.72f), strokeWidth = 11.dp.toPx(), cap = StrokeCap.Round)
            }
            AnatomyType.HAMSTRINGS -> {
              // Posterior Thigh Hamstrings
              drawLine(color = activeColor, start = Offset(w * 0.40f, h * 0.54f), end = Offset(w * 0.38f, h * 0.72f), strokeWidth = 10.dp.toPx(), cap = StrokeCap.Round)
              drawLine(color = activeColor, start = Offset(w * 0.60f, h * 0.54f), end = Offset(w * 0.62f, h * 0.72f), strokeWidth = 10.dp.toPx(), cap = StrokeCap.Round)
            }
            AnatomyType.CALVES -> {
              // Lower Leg Calves
              drawLine(color = activeColor, start = Offset(w * 0.38f, h * 0.76f), end = Offset(w * 0.36f, h * 0.90f), strokeWidth = 8.dp.toPx(), cap = StrokeCap.Round)
              drawLine(color = activeColor, start = Offset(w * 0.62f, h * 0.76f), end = Offset(w * 0.64f, h * 0.90f), strokeWidth = 8.dp.toPx(), cap = StrokeCap.Round)
            }
            AnatomyType.CORE -> {
              // Abdominal 6-Pack Grid & Obliques
              drawRoundRect(
                color = activeColor,
                topLeft = Offset(w * 0.38f, h * 0.31f),
                size = Size(w * 0.24f, h * 0.16f),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
              )
            }
          }
        }
      }

      // Anatomy Legend & Target Description
      Column(
        modifier = Modifier
          .weight(1f)
          .padding(start = 12.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .background(highlightColor, CircleShape)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "TARGET ACTIVATION",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp,
              letterSpacing = 1.sp
            ),
            color = highlightColor
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = getAnatomyLabel(anatomyType),
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = KinetiqTextWhite
          )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = getAnatomyDescription(anatomyType),
          style = MaterialTheme.typography.bodySmall,
          color = KinetiqTextMuted,
          lineHeight = 16.sp
        )
      }
    }
  }
}

private fun getAnatomyLabel(type: AnatomyType): String = when (type) {
  AnatomyType.SHOULDERS -> "Deltoid Complex"
  AnatomyType.CHEST -> "Pectoralis Major"
  AnatomyType.BACK -> "Latissimus & Traps"
  AnatomyType.BICEPS -> "Biceps Brachii"
  AnatomyType.TRICEPS -> "Triceps Brachii"
  AnatomyType.QUADS -> "Quadriceps Femoris"
  AnatomyType.HAMSTRINGS -> "Hamstring Complex"
  AnatomyType.CALVES -> "Gastrocnemius & Soleus"
  AnatomyType.CORE -> "Rectus Abdominis & Obliques"
}

private fun getAnatomyDescription(type: AnatomyType): String = when (type) {
  AnatomyType.SHOULDERS -> "Anterior, lateral, and posterior deltoid heads for 3D shoulder shape."
  AnatomyType.CHEST -> "Clavicular (upper) and sternal (mid/lower) horizontal pressing fibers."
  AnatomyType.BACK -> "Vertical pulldown and horizontal rowing posterior back thickness."
  AnatomyType.BICEPS -> "Long head peak and short head elbow flexion musculature."
  AnatomyType.TRICEPS -> "Horseshoe lateral, long, and medial elbow extension heads."
  AnatomyType.QUADS -> "Vastus lateralis, medialis, and rectus femoris knee extensors."
  AnatomyType.HAMSTRINGS -> "Biceps femoris and semitendinosus hip extension drivers."
  AnatomyType.CALVES -> "Plantarflexion explosive ankle stability and calf definition."
  AnatomyType.CORE -> "Spinal flexion and anti-rotational intra-abdominal pressure wall."
}
