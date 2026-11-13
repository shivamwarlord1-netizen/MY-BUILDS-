package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.exercise.AnatomyType
import com.example.data.exercise.Exercise

/**
 * Interactive Anatomy Heatmap Overlay Component:
 * - Keyed by exercise.id so Front/Back toggle state never bleeds across LazyColumn cards.
 * - Theme-aware M3 colors across Obsidian Purple, Ruby Velvet Red, and Twilight Light.
 * - Sculpted cubic-Bezier human anatomical silhouette highlighting Primary vs Secondary muscles.
 */
@Composable
fun InteractiveAnatomyHeatmap(
  exercise: Exercise,
  modifier: Modifier = Modifier,
  height: Dp = 180.dp,
  primaryColor: Color = MaterialTheme.colorScheme.primary,
  secondaryColor: Color = MaterialTheme.colorScheme.tertiary
) {
  var isBackView by remember(exercise.id) {
    mutableStateOf(
      exercise.anatomyDiagramType == AnatomyType.BACK ||
        exercise.anatomyDiagramType == AnatomyType.HAMSTRINGS ||
        exercise.anatomyDiagramType == AnatomyType.TRICEPS ||
        exercise.anatomyDiagramType == AnatomyType.CALVES ||
        exercise.id == "rear-delt-face-pulls"
    )
  }

  val stageSurface = MaterialTheme.colorScheme.surface
  val pillSurface = MaterialTheme.colorScheme.background.copy(alpha = 0.85f)
  val borderSubtle = MaterialTheme.colorScheme.outline.copy(alpha = 0.65f)
  val onSurface = MaterialTheme.colorScheme.onSurface
  val onSurfaceMuted = MaterialTheme.colorScheme.onSurfaceVariant
  val silhouetteBase = onSurface.copy(alpha = 0.14f)
  val silhouetteStroke = onSurface.copy(alpha = 0.24f)

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .height(height)
      .clip(RoundedCornerShape(18.dp))
      .border(0.9.dp, borderSubtle, RoundedCornerShape(18.dp)),
    color = stageSurface
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Sculpted Anatomical Silhouette Heatmap Canvas
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .width(104.dp)
          .height(height - 20.dp)
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          drawHumanHeatmapSilhouette(
            exerciseId = exercise.id,
            anatomyType = exercise.anatomyDiagramType,
            isBackView = isBackView,
            baseColor = silhouetteBase,
            outlineColor = silhouetteStroke,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor
          )
        }
      }

      Spacer(modifier = Modifier.width(10.dp))

      // 2. Muscle Breakdown Legend & Anterior/Posterior Toggle
      Column(
        modifier = Modifier
          .weight(1f)
          .padding(vertical = 2.dp),
        verticalArrangement = Arrangement.Center
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "MUSCLE ACTIVATION",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp,
              letterSpacing = 0.8.sp
            ),
            color = primaryColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
          )

          Spacer(modifier = Modifier.width(6.dp))

          // Front / Back View Switcher Pill
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = pillSurface,
            border = BorderStroke(0.8.dp, borderSubtle),
            modifier = Modifier
              .heightIn(min = 30.dp)
              .clip(RoundedCornerShape(8.dp))
              .clickable { isBackView = !isBackView }
              .testTag("heatmap_view_toggle_${exercise.id}")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Layers,
                contentDescription = if (isBackView) "Switch to Anterior view" else "Switch to Posterior view",
                tint = primaryColor,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isBackView) "POSTERIOR" else "ANTERIOR",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 8.5.sp
                ),
                color = onSurface
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Primary Target Card
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = primaryColor.copy(alpha = 0.13f),
          border = BorderStroke(0.7.dp, primaryColor.copy(alpha = 0.40f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .background(primaryColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                text = "PRIMARY MOVER",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 8.sp,
                  letterSpacing = 0.6.sp
                ),
                color = primaryColor
              )
              Text(
                text = exercise.primaryMuscle,
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                ),
                color = onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }

        if (exercise.secondaryMuscles.isNotEmpty()) {
          Spacer(modifier = Modifier.height(5.dp))

          // Secondary Stabilizers Card
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = secondaryColor.copy(alpha = 0.11f),
            border = BorderStroke(0.7.dp, secondaryColor.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .background(secondaryColor, CircleShape)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text(
                  text = "SYNERGIST STABILIZERS",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp,
                    letterSpacing = 0.6.sp
                  ),
                  color = secondaryColor
                )
                Text(
                  text = exercise.secondaryMuscles.take(2).joinToString(", "),
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                  color = onSurfaceMuted,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Renders an organic human anatomical silhouette with anterior/posterior viewpoint
 * muscle highlights for Primary (vibrant primaryColor) and Secondary (warm tertiary accent).
 */
private fun DrawScope.drawHumanHeatmapSilhouette(
  exerciseId: String,
  anatomyType: AnatomyType,
  isBackView: Boolean,
  baseColor: Color,
  outlineColor: Color,
  primaryColor: Color,
  secondaryColor: Color
) {
  val w = size.width
  val h = size.height
  val cx = w * 0.5f
  val u = minOf(h / 160f, w / 100f)

  // 1. Head & Jaw contour
  val headY = h * 0.12f
  val headR = 9.5f * u
  drawCircle(baseColor, headR, Offset(cx, headY))
  drawCircle(outlineColor, headR, Offset(cx, headY), style = Stroke(1.1f * u))

  // 2. Sculpted Neck + Trapezius + V-Taper Torso
  val torsoPath = Path().apply {
    moveTo(cx - 4.5f * u, headY + headR * 0.85f)
    lineTo(cx + 4.5f * u, headY + headR * 0.85f)
    cubicTo(
      cx + 6f * u, h * 0.21f,
      cx + 15f * u, h * 0.21f,
      cx + 23f * u, h * 0.24f
    )
    cubicTo(
      cx + 22f * u, h * 0.34f,
      cx + 15f * u, h * 0.42f,
      cx + 13.5f * u, h * 0.48f
    )
    cubicTo(
      cx + 16f * u, h * 0.52f,
      cx + 17f * u, h * 0.55f,
      cx + 16f * u, h * 0.57f
    )
    lineTo(cx - 16f * u, h * 0.57f)
    cubicTo(
      cx - 17f * u, h * 0.55f,
      cx - 16f * u, h * 0.52f,
      cx - 13.5f * u, h * 0.48f
    )
    cubicTo(
      cx - 15f * u, h * 0.42f,
      cx - 22f * u, h * 0.34f,
      cx - 23f * u, h * 0.24f
    )
    cubicTo(
      cx - 15f * u, h * 0.21f,
      cx - 6f * u, h * 0.21f,
      cx - 4.5f * u, headY + headR * 0.85f
    )
    close()
  }
  drawPath(torsoPath, baseColor)
  drawPath(torsoPath, outlineColor, style = Stroke(1.1f * u))

  // 3. Sculpted Left & Right Arms
  fun drawSilhouetteArm(sign: Float) {
    val shoulder = Offset(cx + sign * 22f * u, h * 0.25f)
    val elbow = Offset(cx + sign * 30f * u, h * 0.41f)
    val wrist = Offset(cx + sign * 34f * u, h * 0.56f)
    drawLine(baseColor, shoulder, elbow, 8.2f * u, StrokeCap.Round)
    drawLine(baseColor, elbow, wrist, 6.2f * u, StrokeCap.Round)
    drawCircle(baseColor, 3.2f * u, wrist)
  }
  drawSilhouetteArm(-1f)
  drawSilhouetteArm(1f)

  // 4. Sculpted Left & Right Legs (Thigh -> Knee -> Calf -> Foot)
  fun drawSilhouetteLeg(sign: Float) {
    val hip = Offset(cx + sign * 9.5f * u, h * 0.56f)
    val knee = Offset(cx + sign * 11.5f * u, h * 0.74f)
    val ankle = Offset(cx + sign * 12.5f * u, h * 0.92f)
    drawLine(baseColor, hip, knee, 11.5f * u, StrokeCap.Round)
    drawLine(baseColor, knee, ankle, 7.6f * u, StrokeCap.Round)
    drawRoundRect(
      color = baseColor,
      topLeft = Offset(ankle.x - 4.5f * u, ankle.y - 1f * u),
      size = Size(9f * u, 4.5f * u),
      cornerRadius = CornerRadius(2f * u, 2f * u)
    )
  }
  drawSilhouetteLeg(-1f)
  drawSilhouetteLeg(1f)

  // Subtle center anatomical midline
  drawLine(
    color = outlineColor.copy(alpha = 0.5f),
    start = Offset(cx, h * 0.23f),
    end = Offset(cx, h * 0.54f),
    strokeWidth = 0.9f * u,
    cap = StrokeCap.Round
  )

  // Helper to paint soft glowing muscle regions
  fun glowCircle(center: Offset, radius: Float, color: Color) {
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(color.copy(alpha = 0.92f), color.copy(alpha = 0.35f), Color.Transparent),
        center = center,
        radius = radius * 1.35f
      ),
      radius = radius * 1.35f,
      center = center
    )
    drawCircle(color.copy(alpha = 0.78f), radius * 0.75f, center)
  }

  fun glowCapsule(start: Offset, end: Offset, width: Float, color: Color) {
    drawLine(color.copy(alpha = 0.35f), start, end, width * 1.45f, StrokeCap.Round)
    drawLine(color.copy(alpha = 0.88f), start, end, width, StrokeCap.Round)
  }

  // 5. MUSCLE HEATMAP OVERLAYS (Anterior vs Posterior accurate mapping)
  when (anatomyType) {
    AnatomyType.SHOULDERS -> {
      val isRearDelt = exerciseId == "rear-delt-face-pulls"
      if (isRearDelt && isBackView) {
        glowCircle(Offset(cx - 21f * u, h * 0.25f), 6.2f * u, primaryColor)
        glowCircle(Offset(cx + 21f * u, h * 0.25f), 6.2f * u, primaryColor)
        // Rhomboids / mid traps secondary
        glowCapsule(Offset(cx - 10f * u, h * 0.25f), Offset(cx + 10f * u, h * 0.32f), 5.5f * u, secondaryColor)
        glowCapsule(Offset(cx + 10f * u, h * 0.25f), Offset(cx - 10f * u, h * 0.32f), 5.5f * u, secondaryColor)
      } else {
        glowCircle(Offset(cx - 21f * u, h * 0.25f), 6.4f * u, primaryColor)
        glowCircle(Offset(cx + 21f * u, h * 0.25f), 6.4f * u, primaryColor)
        // Upper traps + triceps secondary
        glowCapsule(Offset(cx - 12f * u, h * 0.21f), Offset(cx + 12f * u, h * 0.21f), 4.5f * u, secondaryColor)
        if (isBackView) {
          glowCapsule(Offset(cx - 24f * u, h * 0.29f), Offset(cx - 29f * u, h * 0.39f), 5.2f * u, secondaryColor)
          glowCapsule(Offset(cx + 24f * u, h * 0.29f), Offset(cx + 29f * u, h * 0.39f), 5.2f * u, secondaryColor)
        }
      }
    }

    AnatomyType.CHEST -> {
      if (!isBackView) {
        // Sculpted Pectoral Major Plates
        drawRoundRect(primaryColor.copy(alpha = 0.85f), Offset(cx - 17.5f * u, h * 0.24f), Size(15f * u, h * 0.105f), CornerRadius(5f * u, 5f * u))
        drawRoundRect(primaryColor.copy(alpha = 0.85f), Offset(cx + 2.5f * u, h * 0.24f), Size(15f * u, h * 0.105f), CornerRadius(5f * u, 5f * u))
        // Anterior deltoids secondary
        glowCircle(Offset(cx - 21f * u, h * 0.25f), 4.8f * u, secondaryColor)
        glowCircle(Offset(cx + 21f * u, h * 0.25f), 4.8f * u, secondaryColor)
      } else {
        // Posterior stabilizers: Triceps
        glowCapsule(Offset(cx - 24f * u, h * 0.29f), Offset(cx - 29f * u, h * 0.39f), 5.8f * u, secondaryColor)
        glowCapsule(Offset(cx + 24f * u, h * 0.29f), Offset(cx + 29f * u, h * 0.39f), 5.8f * u, secondaryColor)
      }
    }

    AnatomyType.BACK -> {
      if (isBackView) {
        // Latissimus Dorsi V-wings
        glowCapsule(Offset(cx - 17f * u, h * 0.28f), Offset(cx - 6f * u, h * 0.43f), 8f * u, primaryColor)
        glowCapsule(Offset(cx + 17f * u, h * 0.28f), Offset(cx + 6f * u, h * 0.43f), 8f * u, primaryColor)
        // Rear delts & traps secondary
        glowCircle(Offset(cx - 21f * u, h * 0.25f), 4.8f * u, secondaryColor)
        glowCircle(Offset(cx + 21f * u, h * 0.25f), 4.8f * u, secondaryColor)
        glowCapsule(Offset(cx - 9f * u, h * 0.22f), Offset(cx + 9f * u, h * 0.22f), 5f * u, secondaryColor)
      } else {
        // Anterior view: biceps synergists
        glowCapsule(Offset(cx - 24f * u, h * 0.29f), Offset(cx - 29f * u, h * 0.39f), 5.6f * u, secondaryColor)
        glowCapsule(Offset(cx + 24f * u, h * 0.29f), Offset(cx + 29f * u, h * 0.39f), 5.6f * u, secondaryColor)
      }
    }

    AnatomyType.BICEPS -> {
      if (!isBackView) {
        glowCapsule(Offset(cx - 24f * u, h * 0.29f), Offset(cx - 29f * u, h * 0.39f), 6.8f * u, primaryColor)
        glowCapsule(Offset(cx + 24f * u, h * 0.29f), Offset(cx + 29f * u, h * 0.39f), 6.8f * u, primaryColor)
        glowCapsule(Offset(cx - 30.5f * u, h * 0.43f), Offset(cx - 33f * u, h * 0.52f), 4.5f * u, secondaryColor)
        glowCapsule(Offset(cx + 30.5f * u, h * 0.43f), Offset(cx + 33f * u, h * 0.52f), 4.5f * u, secondaryColor)
      } else {
        glowCircle(Offset(cx - 21f * u, h * 0.25f), 4.5f * u, secondaryColor)
        glowCircle(Offset(cx + 21f * u, h * 0.25f), 4.5f * u, secondaryColor)
      }
    }

    AnatomyType.TRICEPS -> {
      if (isBackView) {
        glowCapsule(Offset(cx - 24f * u, h * 0.28f), Offset(cx - 29f * u, h * 0.39f), 7f * u, primaryColor)
        glowCapsule(Offset(cx + 24f * u, h * 0.28f), Offset(cx + 29f * u, h * 0.39f), 7f * u, primaryColor)
      } else {
        glowCircle(Offset(cx - 21f * u, h * 0.25f), 4.5f * u, secondaryColor)
        glowCircle(Offset(cx + 21f * u, h * 0.25f), 4.5f * u, secondaryColor)
      }
    }

    AnatomyType.QUADS -> {
      if (!isBackView) {
        glowCapsule(Offset(cx - 9.5f * u, h * 0.58f), Offset(cx - 11.2f * u, h * 0.72f), 8.5f * u, primaryColor)
        glowCapsule(Offset(cx + 9.5f * u, h * 0.58f), Offset(cx + 11.2f * u, h * 0.72f), 8.5f * u, primaryColor)
        // Core bracing secondary
        drawRoundRect(secondaryColor.copy(alpha = 0.72f), Offset(cx - 9f * u, h * 0.37f), Size(18f * u, h * 0.11f), CornerRadius(4f * u, 4f * u))
      } else {
        // Glutes & Hamstrings
        glowCircle(Offset(cx - 8.5f * u, h * 0.53f), 7f * u, primaryColor)
        glowCircle(Offset(cx + 8.5f * u, h * 0.53f), 7f * u, primaryColor)
        glowCapsule(Offset(cx - 10f * u, h * 0.59f), Offset(cx - 11.2f * u, h * 0.72f), 7.5f * u, secondaryColor)
        glowCapsule(Offset(cx + 10f * u, h * 0.59f), Offset(cx + 11.2f * u, h * 0.72f), 7.5f * u, secondaryColor)
      }
    }

    AnatomyType.HAMSTRINGS -> {
      if (isBackView) {
        glowCapsule(Offset(cx - 10f * u, h * 0.59f), Offset(cx - 11.2f * u, h * 0.72f), 8.5f * u, primaryColor)
        glowCapsule(Offset(cx + 10f * u, h * 0.59f), Offset(cx + 11.2f * u, h * 0.72f), 8.5f * u, primaryColor)
        glowCircle(Offset(cx - 8.5f * u, h * 0.53f), 6.5f * u, primaryColor)
        glowCircle(Offset(cx + 8.5f * u, h * 0.53f), 6.5f * u, primaryColor)
        glowCapsule(Offset(cx, h * 0.34f), Offset(cx, h * 0.48f), 7f * u, secondaryColor)
      } else {
        glowCapsule(Offset(cx, h * 0.36f), Offset(cx, h * 0.48f), 6f * u, secondaryColor)
      }
    }

    AnatomyType.CALVES -> {
      glowCapsule(Offset(cx - 11.8f * u, h * 0.76f), Offset(cx - 12.5f * u, h * 0.87f), 6.8f * u, primaryColor)
      glowCapsule(Offset(cx + 11.8f * u, h * 0.76f), Offset(cx + 12.5f * u, h * 0.87f), 6.8f * u, primaryColor)
      glowCircle(Offset(cx - 12.5f * u, h * 0.92f), 3.6f * u, secondaryColor)
      glowCircle(Offset(cx + 12.5f * u, h * 0.92f), 3.6f * u, secondaryColor)
    }

    AnatomyType.CORE -> {
      if (!isBackView) {
        drawRoundRect(primaryColor.copy(alpha = 0.85f), Offset(cx - 9.5f * u, h * 0.35f), Size(19f * u, h * 0.14f), CornerRadius(4.5f * u, 4.5f * u))
        // Obliques / hip flexors secondary
        glowCapsule(Offset(cx - 13f * u, h * 0.39f), Offset(cx - 11f * u, h * 0.52f), 4.5f * u, secondaryColor)
        glowCapsule(Offset(cx + 13f * u, h * 0.39f), Offset(cx + 11f * u, h * 0.52f), 4.5f * u, secondaryColor)
      } else {
        glowCapsule(Offset(cx, h * 0.36f), Offset(cx, h * 0.48f), 7.5f * u, secondaryColor)
      }
    }
  }
}
