package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * Exhaustive set of all exercise IDs with dedicated vector character animations.
 */
val SUPPORTED_EXERCISE_ANIMATION_IDS: Set<String> = setOf(
  "overhead-dumbbell-press",
  "lateral-deltoid-raises",
  "rear-delt-face-pulls",
  "incline-dumbbell-press",
  "flat-barbell-bench-press",
  "cable-chest-fly",
  "lat-pulldown-neutral",
  "barbell-bent-over-row",
  "incline-dumbbell-curl",
  "triceps-rope-pushdown",
  "barbell-back-squat",
  "romanian-deadlift",
  "standing-calf-raises",
  "hanging-knee-raise",
  "cable-woodchopper"
)

/**
 * Clean Flat-Vector Athletic Exercise Demonstration Stage:
 * - Bright white studio backdrop with soft elliptical floor shadow beneath the athlete.
 * - Friendly illustrated character wearing a light blue tank top, navy athletic shorts,
 *   white crew socks, and white-and-blue sneakers.
 * - Pure vector aesthetic with zero muscle glow overlays (muscle highlights remain on Heatmap).
 * - User-controlled playback: paused in setup pose until explicitly animated.
 */
@Composable
fun AnimatedExerciseFigure(
  exerciseId: String,
  modifier: Modifier = Modifier,
  height: Dp = 180.dp,
  primaryColor: Color = MaterialTheme.colorScheme.primary,
  isPlaying: Boolean = false,
  staticPhase: Float = 0f,
  speedMultiplier: Float = 1.0f
) {
  val animProgress = remember(exerciseId) {
    Animatable(staticPhase.coerceIn(0f, 1f))
  }

  // Snap cleanly to requested static pose when paused (zero idle animation ticks)
  LaunchedEffect(isPlaying, staticPhase, exerciseId) {
    if (!isPlaying) {
      animProgress.snapTo(staticPhase.coerceIn(0f, 1f))
    }
  }

  // Run smooth biomechanical rep loop ONLY when explicitly playing
  LaunchedEffect(isPlaying, speedMultiplier, exerciseId) {
    if (isPlaying) {
      val durationMs = (1150f / speedMultiplier.coerceAtLeast(0.25f)).toInt().coerceAtLeast(350)
      while (isActive) {
        animProgress.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = durationMs, easing = FastOutSlowInEasing)
        )
        delay((240f / speedMultiplier.coerceAtLeast(0.25f)).toLong().coerceAtLeast(80L))
        animProgress.animateTo(
          targetValue = 0f,
          animationSpec = tween(durationMillis = durationMs, easing = FastOutSlowInEasing)
        )
        delay((140f / speedMultiplier.coerceAtLeast(0.25f)).toLong().coerceAtLeast(50L))
      }
    }
  }

  val activeProgress = animProgress.value
  val stageBorder = MaterialTheme.colorScheme.outline.copy(alpha = 0.72f)
  val pillBackground = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.96f)
  val pillTextColor = MaterialTheme.colorScheme.onSurface
  val mutedText = MaterialTheme.colorScheme.onSurfaceVariant

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .height(height)
      .clip(RoundedCornerShape(18.dp))
      .border(1.dp, stageBorder, RoundedCornerShape(18.dp)),
    color = Color.White
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.White),
      contentAlignment = Alignment.Center
    ) {
      // Flat-Vector Character Canvas on bright white studio backdrop
      Canvas(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 12.dp, vertical = 8.dp)
      ) {
        drawVectorExercise(
          exerciseId = exerciseId,
          progress = activeProgress
        )
      }

      // Bottom-right Movement Phase Indicator Pill
      val isPeak = activeProgress > 0.65f
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(8.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = pillBackground,
          border = BorderStroke(
            width = 0.8.dp,
            color = if (isPeak) primaryColor else stageBorder
          )
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .background(
                  color = if (isPeak) primaryColor else mutedText.copy(alpha = 0.6f),
                  shape = CircleShape
                )
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = when {
                isPeak -> "PEAK SQUEEZE"
                isPlaying -> "ECCENTRIC / SETUP"
                else -> "SETUP POSE"
              },
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 8.5.sp,
                letterSpacing = 0.7.sp
              ),
              color = if (isPeak) primaryColor else pillTextColor
            )
          }
        }
      }
    }
  }
}

/**
 * Renders the clean flat-vector athlete across all 15 ExerciseDatabase IDs.
 */
private fun DrawScope.drawVectorExercise(
  exerciseId: String,
  progress: Float
) {
  val w = size.width
  val h = size.height
  val u = minOf(h / 180f, w / 280f)
  val cx = w * 0.5f
  val floorY = h * 0.90f
  val p = progress.coerceIn(0f, 1f)

  when (exerciseId) {
    // =========================================================================
    // 1. SHOULDERS: OVERHEAD DUMBBELL SHOULDER PRESS (Seated upright bench)
    // =========================================================================
    "overhead-dumbbell-press" -> {
      drawFloorShadow(Offset(cx, floorY + 2f * u), 52f * u, 8.5f * u)

      // Upright bench frame & pad behind torso
      val backrestTop = Offset(cx, floorY - 122f * u)
      val backrestBottom = Offset(cx, floorY - 52f * u)
      drawLine(VectorAthletePalette.benchFrame, Offset(cx, floorY - 52f * u), Offset(cx, floorY), 4f * u, StrokeCap.Round)
      drawLine(VectorAthletePalette.dumbbellDark, Offset(cx - 12f * u, floorY), Offset(cx + 12f * u, floorY), 3f * u, StrokeCap.Round)
      drawLine(VectorAthletePalette.benchPad, backrestTop, backrestBottom, 16f * u, StrokeCap.Round)
      drawLine(VectorAthletePalette.benchFrame, Offset(cx - 18f * u, floorY - 52f * u), Offset(cx + 18f * u, floorY - 52f * u), 7f * u, StrokeCap.Round)

      val headCenter = Offset(cx, floorY - 126f * u)
      val neckTop = Offset(cx, floorY - 115f * u)
      val lShoulder = Offset(cx - 21f * u, floorY - 103f * u)
      val rShoulder = Offset(cx + 21f * u, floorY - 103f * u)
      val hipY = floorY - 54f * u

      val lHip = Offset(cx - 10f * u, hipY)
      val rHip = Offset(cx + 10f * u, hipY)
      val lKnee = Offset(cx - 22f * u, floorY - 38f * u)
      val rKnee = Offset(cx + 22f * u, floorY - 38f * u)
      val lAnkle = Offset(cx - 24f * u, floorY - 5f * u)
      val rAnkle = Offset(cx + 24f * u, floorY - 5f * u)

      drawVectorLeg(lHip, lKnee, lAnkle, u, facingDir = -1)
      drawVectorLeg(rHip, rKnee, rAnkle, u, facingDir = 1)
      drawVectorShortsFront(Offset(cx, hipY), 16.5f * u, lHip, lKnee, rHip, rKnee, u)

      drawVectorHead(headCenter, 10.5f * u, facingDir = 0)
      drawVectorFrontTankTorso(
        neckTop = neckTop,
        leftShoulder = lShoulder,
        rightShoulder = rShoulder,
        waistY = floorY - 68f * u,
        waistHalfWidth = 13.5f * u,
        hipY = hipY,
        hipHalfWidth = 16f * u
      )

      val lElbow = Offset(lerpFloat(cx - 33f * u, cx - 23f * u, p), lerpFloat(floorY - 94f * u, floorY - 128f * u, p))
      val rElbow = Offset(lerpFloat(cx + 33f * u, cx + 23f * u, p), lerpFloat(floorY - 94f * u, floorY - 128f * u, p))
      val lWrist = Offset(lerpFloat(cx - 30f * u, cx - 14f * u, p), lerpFloat(floorY - 121f * u, floorY - 155f * u, p))
      val rWrist = Offset(lerpFloat(cx + 30f * u, cx + 14f * u, p), lerpFloat(floorY - 121f * u, floorY - 155f * u, p))

      drawVectorDumbbell(lWrist, angleDeg = 0f, u)
      drawVectorDumbbell(rWrist, angleDeg = 0f, u)
      drawVectorArm(lShoulder, lElbow, lWrist, u)
      drawVectorArm(rShoulder, rElbow, rWrist, u)
    }

    // =========================================================================
    // 2. SHOULDERS: DUMBBELL LATERAL DELTOID RAISE
    // =========================================================================
    "lateral-deltoid-raises" -> {
      drawFloorShadow(Offset(cx, floorY + 2f * u), 48f * u, 8.5f * u)

      val headCenter = Offset(cx, floorY - 140f * u)
      val neckTop = Offset(cx, floorY - 128f * u)
      val lShoulder = Offset(cx - 21f * u, floorY - 115f * u)
      val rShoulder = Offset(cx + 21f * u, floorY - 115f * u)
      val hipY = floorY - 72f * u

      val lHip = Offset(cx - 9f * u, hipY)
      val rHip = Offset(cx + 9f * u, hipY)
      val lKnee = Offset(cx - 12f * u, floorY - 38f * u)
      val rKnee = Offset(cx + 12f * u, floorY - 38f * u)

      drawVectorLeg(lHip, lKnee, Offset(cx - 13f * u, floorY - 5f * u), u, -1)
      drawVectorLeg(rHip, rKnee, Offset(cx + 13f * u, floorY - 5f * u), u, 1)
      drawVectorShortsFront(Offset(cx, hipY), 16.5f * u, lHip, lKnee, rHip, rKnee, u)

      drawVectorHead(headCenter, 10.5f * u, facingDir = 0)
      drawVectorFrontTankTorso(
        neckTop = neckTop,
        leftShoulder = lShoulder,
        rightShoulder = rShoulder,
        waistY = floorY - 86f * u,
        waistHalfWidth = 13f * u,
        hipY = hipY,
        hipHalfWidth = 15.5f * u
      )

      val lElbow = Offset(lerpFloat(cx - 22f * u, cx - 44f * u, p), lerpFloat(floorY - 94f * u, floorY - 113f * u, p))
      val rElbow = Offset(lerpFloat(cx + 22f * u, cx + 44f * u, p), lerpFloat(floorY - 94f * u, floorY - 113f * u, p))
      val lWrist = Offset(lerpFloat(cx - 18f * u, cx - 68f * u, p), lerpFloat(floorY - 74f * u, floorY - 112f * u, p))
      val rWrist = Offset(lerpFloat(cx + 18f * u, cx + 68f * u, p), lerpFloat(floorY - 74f * u, floorY - 112f * u, p))

      drawVectorDumbbell(lWrist, angleDeg = lerpFloat(90f, 12f, p), u)
      drawVectorDumbbell(rWrist, angleDeg = lerpFloat(90f, -12f, p), u)
      drawVectorArm(lShoulder, lElbow, lWrist, u)
      drawVectorArm(rShoulder, rElbow, rWrist, u)
    }

    // =========================================================================
    // 3. SHOULDERS: CABLE FACE PULL & EXTERNAL ROTATION (3/4 Side View, 2 Legs!)
    // =========================================================================
    "rear-delt-face-pulls" -> {
      drawFloorShadow(Offset(cx + 16f * u, floorY + 2f * u), 56f * u, 8.5f * u)

      val towerX = cx - 96f * u
      val pulleyY = floorY - 118f * u

      val hip = Offset(cx + 22f * u, floorY - 72f * u)
      val shoulder = Offset(lerpFloat(cx + 18f * u, cx + 26f * u, p), floorY - 116f * u)
      val headCenter = Offset(shoulder.x - 1f * u, shoulder.y - 19f * u)

      val farHip = Offset(hip.x - 4f * u, hip.y)
      val farKnee = Offset(cx - 4f * u, floorY - 38f * u)
      val farAnkle = Offset(cx - 10f * u, floorY - 5f * u)

      val nearHip = Offset(hip.x + 2f * u, hip.y)
      val nearKnee = Offset(cx + 34f * u, floorY - 37f * u)
      val nearAnkle = Offset(cx + 44f * u, floorY - 5f * u)

      val farShoulder = Offset(shoulder.x - 4f * u, shoulder.y - 3f * u)
      val farElbow = Offset(lerpFloat(cx - 14f * u, cx + 38f * u, p), lerpFloat(pulleyY + 6f * u, shoulder.y - 8f * u, p))
      val farWrist = Offset(lerpFloat(cx - 38f * u, shoulder.x + 12f * u, p), lerpFloat(pulleyY + 2f * u, headCenter.y - 2f * u, p))

      val nearElbow = Offset(lerpFloat(cx - 10f * u, cx + 44f * u, p), lerpFloat(pulleyY + 10f * u, shoulder.y - 2f * u, p))
      val nearWrist = Offset(lerpFloat(cx - 34f * u, shoulder.x + 16f * u, p), lerpFloat(pulleyY + 5f * u, headCenter.y + 4f * u, p))

      val ropeJunction = lerpOffset(farWrist, nearWrist, 0.5f) + Offset(-12f * u, 2f * u)
      drawVectorCableColumn(towerX, floorY - 150f * u, floorY, pulleyY, listOf(ropeJunction), u)
      drawLine(VectorAthletePalette.dumbbellDark, ropeJunction, farWrist, 2.4f * u, StrokeCap.Round)
      drawLine(VectorAthletePalette.dumbbellDark, ropeJunction, nearWrist, 2.4f * u, StrokeCap.Round)

      drawVectorLeg(farHip, farKnee, farAnkle, u, facingDir = -1, isFarLeg = true)
      drawVectorShortsSide(farHip, farKnee, 9.8f * u, facingDir = -1, isFarSleeve = true)
      drawVectorArm(farShoulder, farElbow, farWrist, u, isFarArm = true)

      drawVectorHead(headCenter, 10f * u, facingDir = -1)
      drawVectorSideTankTorso(shoulder, hip, chestDepth = 15f * u, facingDir = -1)

      drawVectorLeg(nearHip, nearKnee, nearAnkle, u, facingDir = -1)
      drawVectorShortsSide(nearHip, nearKnee, 10.6f * u, facingDir = -1)

      drawVectorArm(shoulder, nearElbow, nearWrist, u)
      drawCircle(VectorAthletePalette.dumbbellDark, 3.6f * u, farWrist)
      drawCircle(VectorAthletePalette.dumbbellDark, 3.6f * u, nearWrist)
    }

    // =========================================================================
    // 4. CHEST: INCLINE DUMBBELL BENCH PRESS (30° Incline Bench)
    // =========================================================================
    "incline-dumbbell-press" -> {
      drawFloorShadow(Offset(cx, floorY + 2f * u), 72f * u, 8.5f * u)

      val seatStart = Offset(cx - 8f * u, floorY - 48f * u)
      val seatEnd = Offset(cx + 22f * u, floorY - 52f * u)
      val backStart = Offset(cx - 58f * u, floorY - 94f * u)
      val backEnd = Offset(cx - 6f * u, floorY - 50f * u)

      drawVectorBench(seatStart, seatEnd, floorY, u)
      drawLine(VectorAthletePalette.benchFrame, Offset(cx - 46f * u, floorY - 84f * u), Offset(cx - 46f * u, floorY), 3.6f * u, StrokeCap.Round)
      drawLine(VectorAthletePalette.dumbbellDark, Offset(cx - 54f * u, floorY), Offset(cx - 38f * u, floorY), 3f * u, StrokeCap.Round)
      drawLine(VectorAthletePalette.benchPad, backStart, backEnd, 8.5f * u, StrokeCap.Round)

      val hip = Offset(cx + 2f * u, floorY - 55f * u)
      val farHip = Offset(hip.x, hip.y - 2f * u)
      val shoulder = Offset(cx - 34f * u, floorY - 84f * u)
      val headCenter = Offset(shoulder.x - 14f * u, shoulder.y - 12f * u)

      val farKnee = Offset(cx + 34f * u, floorY - 46f * u)
      val farAnkle = Offset(cx + 37f * u, floorY - 5f * u)
      val nearKnee = Offset(cx + 42f * u, floorY - 43f * u)
      val nearAnkle = Offset(cx + 47f * u, floorY - 5f * u)

      val farElbow = Offset(lerpFloat(shoulder.x - 2f * u, cx - 28f * u, p), lerpFloat(shoulder.y + 16f * u, shoulder.y - 26f * u, p))
      val farWrist = Offset(lerpFloat(shoulder.x + 2f * u, cx - 22f * u, p), lerpFloat(shoulder.y - 16f * u, floorY - 144f * u, p))

      drawVectorLeg(farHip, farKnee, farAnkle, u, facingDir = 1, isFarLeg = true)
      drawVectorShortsSide(farHip, farKnee, 9.8f * u, facingDir = 1, isFarSleeve = true)
      drawVectorDumbbell(farWrist, angleDeg = 0f, u)
      drawVectorArm(Offset(shoulder.x + 2f * u, shoulder.y - 3f * u), farElbow, farWrist, u, isFarArm = true)

      drawVectorHead(headCenter, 9.8f * u, facingDir = 1)
      drawVectorSideTankTorso(shoulder, hip, chestDepth = 15.5f * u, facingDir = 1)

      drawVectorLeg(hip, nearKnee, nearAnkle, u, facingDir = 1)
      drawVectorShortsSide(hip, nearKnee, 10.6f * u, facingDir = 1)

      val nearElbow = Offset(lerpFloat(shoulder.x + 6f * u, cx - 20f * u, p), lerpFloat(shoulder.y + 18f * u, shoulder.y - 22f * u, p))
      val nearWrist = Offset(lerpFloat(shoulder.x + 8f * u, cx - 16f * u, p), lerpFloat(shoulder.y - 14f * u, floorY - 140f * u, p))
      drawVectorDumbbell(nearWrist, angleDeg = 0f, u)
      drawVectorArm(shoulder, nearElbow, nearWrist, u)
    }

    // =========================================================================
    // 5. CHEST: BARBELL FLAT BENCH PRESS
    // =========================================================================
    "flat-barbell-bench-press" -> {
      drawFloorShadow(Offset(cx - 2f * u, floorY + 2f * u), 78f * u, 8.5f * u)

      val benchY = floorY - 46f * u
      drawVectorBench(Offset(cx - 72f * u, benchY), Offset(cx + 38f * u, benchY), floorY, u)

      drawLine(VectorAthletePalette.benchFrame, Offset(cx - 56f * u, floorY - 118f * u), Offset(cx - 56f * u, floorY), 4.5f * u, StrokeCap.Round)
      drawLine(VectorAthletePalette.dumbbellDark, Offset(cx - 66f * u, floorY), Offset(cx - 46f * u, floorY), 3.5f * u, StrokeCap.Round)

      val shoulder = Offset(cx - 32f * u, benchY - 9f * u)
      val hip = Offset(cx + 22f * u, benchY - 7f * u)
      val farHip = Offset(hip.x, hip.y - 2f * u)
      val headCenter = Offset(cx - 54f * u, benchY - 12f * u)

      val farKnee = Offset(cx + 50f * u, benchY - 1f * u)
      val farAnkle = Offset(cx + 54f * u, floorY - 5f * u)
      val nearKnee = Offset(cx + 58f * u, benchY + 2f * u)
      val nearAnkle = Offset(cx + 64f * u, floorY - 5f * u)

      drawVectorLeg(farHip, farKnee, farAnkle, u, facingDir = 1, isFarLeg = true)
      drawVectorShortsSide(farHip, farKnee, 9.8f * u, facingDir = 1, isFarSleeve = true)

      val barPos = Offset(lerpFloat(cx - 22f * u, cx - 26f * u, p), lerpFloat(benchY - 28f * u, benchY - 76f * u, p))
      val farElbow = Offset(cx - 28f * u, lerpFloat(benchY + 5f * u, benchY - 42f * u, p))
      drawVectorArm(Offset(shoulder.x + 2f * u, shoulder.y - 3f * u), farElbow, barPos, u, isFarArm = true)

      drawVectorHead(headCenter, 9.8f * u, facingDir = 1)
      drawVectorSideTankTorso(shoulder, hip, chestDepth = 15.5f * u, facingDir = 1)

      drawVectorLeg(hip, nearKnee, nearAnkle, u, facingDir = 1)
      drawVectorShortsSide(hip, nearKnee, 10.6f * u, facingDir = 1)

      val nearElbow = Offset(cx - 24f * u, lerpFloat(benchY + 8f * u, benchY - 40f * u, p))
      drawVectorArm(shoulder, nearElbow, barPos, u)
      drawVectorBarbellSidePlate(barPos, u)
    }

    // =========================================================================
    // 6. CHEST: STANDING MID-CABLE CHEST FLYE
    // =========================================================================
    "cable-chest-fly" -> {
      drawFloorShadow(Offset(cx, floorY + 2f * u), 56f * u, 8.5f * u)

      val pulleyY = floorY - 108f * u
      val hipY = floorY - 70f * u
      val headCenter = Offset(cx, floorY - 138f * u)
      val neckTop = Offset(cx, floorY - 126f * u)
      val lShoulder = Offset(cx - 22f * u, floorY - 113f * u)
      val rShoulder = Offset(cx + 22f * u, floorY - 113f * u)

      val lWrist = Offset(lerpFloat(cx - 64f * u, cx - 8f * u, p), lerpFloat(floorY - 104f * u, floorY - 96f * u, p))
      val rWrist = Offset(lerpFloat(cx + 64f * u, cx + 8f * u, p), lerpFloat(floorY - 104f * u, floorY - 96f * u, p))
      val lElbow = Offset(lerpFloat(cx - 44f * u, cx - 26f * u, p), lerpFloat(floorY - 110f * u, floorY - 101f * u, p))
      val rElbow = Offset(lerpFloat(cx + 44f * u, cx + 26f * u, p), lerpFloat(floorY - 110f * u, floorY - 101f * u, p))

      drawVectorCableColumn(cx - 108f * u, floorY - 150f * u, floorY, pulleyY, listOf(lWrist), u)
      drawVectorCableColumn(cx + 108f * u, floorY - 150f * u, floorY, pulleyY, listOf(rWrist), u)

      val lHip = Offset(cx - 8f * u, hipY)
      val rHip = Offset(cx + 10f * u, hipY)
      val lKnee = Offset(cx - 16f * u, floorY - 38f * u)
      val rKnee = Offset(cx + 16f * u, floorY - 36f * u)

      drawVectorLeg(lHip, lKnee, Offset(cx - 18f * u, floorY - 7f * u), u, -1, isFarLeg = true)
      drawVectorLeg(rHip, rKnee, Offset(cx + 18f * u, floorY - 5f * u), u, 1)
      drawVectorShortsFront(Offset(cx, hipY), 16.5f * u, lHip, lKnee, rHip, rKnee, u)

      drawVectorHead(headCenter, 10.2f * u, facingDir = 0)
      drawVectorFrontTankTorso(
        neckTop = neckTop,
        leftShoulder = lShoulder,
        rightShoulder = rShoulder,
        waistY = floorY - 84f * u,
        waistHalfWidth = 13f * u,
        hipY = hipY,
        hipHalfWidth = 15.5f * u
      )

      drawVectorArm(lShoulder, lElbow, lWrist, u)
      drawVectorArm(rShoulder, rElbow, rWrist, u)
      drawCircle(VectorAthletePalette.dumbbellDark, 3.6f * u, lWrist)
      drawCircle(VectorAthletePalette.dumbbellDark, 3.6f * u, rWrist)
    }

    // =========================================================================
    // 7. BACK: NEUTRAL-GRIP LAT PULLDOWN
    // =========================================================================
    "lat-pulldown-neutral" -> {
      drawFloorShadow(Offset(cx, floorY + 2f * u), 54f * u, 8.5f * u)

      val seatY = floorY - 46f * u
      drawLine(VectorAthletePalette.benchFrame, Offset(cx, floorY - 160f * u), Offset(cx, floorY), 4.5f * u, StrokeCap.Round)
      drawLine(VectorAthletePalette.steelBar, Offset(cx - 56f * u, floorY - 160f * u), Offset(cx + 56f * u, floorY - 160f * u), 4f * u, StrokeCap.Round)
      drawVectorBench(Offset(cx - 18f * u, seatY), Offset(cx + 18f * u, seatY), floorY, u)

      val barY = lerpFloat(floorY - 144f * u, floorY - 104f * u, p)
      drawCircle(VectorAthletePalette.dumbbellBevel, 4.5f * u, Offset(cx, floorY - 157f * u))
      drawCircle(VectorAthletePalette.steelLight, 2f * u, Offset(cx, floorY - 157f * u))
      drawLine(VectorAthletePalette.steelBar, Offset(cx, floorY - 157f * u), Offset(cx, barY), 2.2f * u, StrokeCap.Round)

      val hipY = seatY - 6f * u
      val neckTop = Offset(cx, floorY - 110f * u)
      val headCenter = Offset(cx, floorY - 122f * u)
      val lShoulder = Offset(cx - 22f * u, floorY - 98f * u)
      val rShoulder = Offset(cx + 22f * u, floorY - 98f * u)

      val lHip = Offset(cx - 12f * u, hipY)
      val rHip = Offset(cx + 12f * u, hipY)
      val lKnee = Offset(cx - 24f * u, seatY + 4f * u)
      val rKnee = Offset(cx + 24f * u, seatY + 4f * u)

      drawVectorLeg(lHip, lKnee, Offset(cx - 26f * u, floorY - 5f * u), u, -1)
      drawVectorLeg(rHip, rKnee, Offset(cx + 26f * u, floorY - 5f * u), u, 1)
      drawVectorShortsFront(Offset(cx, hipY), 16.5f * u, lHip, lKnee, rHip, rKnee, u)
      drawRoundRect(VectorAthletePalette.benchPad, Offset(cx - 32f * u, seatY - 7f * u), Size(14f * u, 8f * u), CornerRadius(4f * u, 4f * u))
      drawRoundRect(VectorAthletePalette.benchPad, Offset(cx + 18f * u, seatY - 7f * u), Size(14f * u, 8f * u), CornerRadius(4f * u, 4f * u))

      drawVectorBackTankTorso(
        neckTop = neckTop,
        leftShoulder = lShoulder,
        rightShoulder = rShoulder,
        waistY = floorY - 68f * u,
        waistHalfWidth = 13.5f * u,
        hipY = hipY,
        hipHalfWidth = 16f * u
      )

      val lWrist = Offset(cx - 22f * u, barY)
      val rWrist = Offset(cx + 22f * u, barY)
      val lElbow = Offset(lerpFloat(cx - 27f * u, cx - 32f * u, p), lerpFloat(floorY - 124f * u, floorY - 82f * u, p))
      val rElbow = Offset(lerpFloat(cx + 27f * u, cx + 32f * u, p), lerpFloat(floorY - 124f * u, floorY - 82f * u, p))

      drawVectorArm(lShoulder, lElbow, lWrist, u)
      drawVectorArm(rShoulder, rElbow, rWrist, u)

      drawLine(VectorAthletePalette.dumbbellDark, Offset(cx - 34f * u, barY), Offset(cx + 34f * u, barY), 3.6f * u, StrokeCap.Round)
      drawLine(VectorAthletePalette.benchFrame, lWrist, Offset(lWrist.x, barY + 5f * u), 3.2f * u, StrokeCap.Round)
      drawLine(VectorAthletePalette.benchFrame, rWrist, Offset(rWrist.x, barY + 5f * u), 3.2f * u, StrokeCap.Round)

      drawVectorHead(headCenter, 10.2f * u, facingDir = 0, isBackView = true)
    }

    // =========================================================================
    // 8. BACK: BARBELL BENT-OVER ROW
    // =========================================================================
    "barbell-bent-over-row" -> {
      drawFloorShadow(Offset(cx + 8f * u, floorY + 2f * u), 56f * u, 8.5f * u)

      val hip = Offset(cx + 26f * u, floorY - 72f * u)
      val farHip = Offset(hip.x - 4f * u, hip.y)
      val shoulder = Offset(cx - 18f * u, floorY - 104f * u)
      val headCenter = Offset(shoulder.x - 14f * u, shoulder.y - 10f * u)

      val farKnee = Offset(cx + 12f * u, floorY - 38f * u)
      val farAnkle = Offset(cx + 18f * u, floorY - 5f * u)
      val nearKnee = Offset(cx + 18f * u, floorY - 36f * u)
      val nearAnkle = Offset(cx + 26f * u, floorY - 5f * u)

      val barPos = Offset(cx - 14f * u, lerpFloat(floorY - 36f * u, floorY - 68f * u, p))
      val farElbow = Offset(lerpFloat(cx - 18f * u, cx + 6f * u, p), lerpFloat(floorY - 68f * u, floorY - 114f * u, p))
      val nearElbow = Offset(lerpFloat(cx - 15f * u, cx + 10f * u, p), lerpFloat(floorY - 66f * u, floorY - 110f * u, p))

      drawVectorLeg(farHip, farKnee, farAnkle, u, facingDir = -1, isFarLeg = true)
      drawVectorShortsSide(farHip, farKnee, 9.8f * u, facingDir = -1, isFarSleeve = true)
      drawVectorArm(Offset(shoulder.x - 2f * u, shoulder.y - 2f * u), farElbow, barPos, u, isFarArm = true)

      drawVectorHead(headCenter, 9.8f * u, facingDir = -1)
      drawVectorSideTankTorso(shoulder, hip, chestDepth = 15.5f * u, facingDir = -1)

      drawVectorLeg(hip, nearKnee, nearAnkle, u, facingDir = -1)
      drawVectorShortsSide(hip, nearKnee, 10.6f * u, facingDir = -1)

      drawVectorArm(shoulder, nearElbow, barPos, u)
      drawVectorBarbellSidePlate(barPos, u)
    }

    // =========================================================================
    // 9. ARMS: INCLINE DUMBBELL BICEP CURL
    // =========================================================================
    "incline-dumbbell-curl" -> {
      drawFloorShadow(Offset(cx + 2f * u, floorY + 2f * u), 64f * u, 8.5f * u)

      val seatStart = Offset(cx - 12f * u, floorY - 46f * u)
      val seatEnd = Offset(cx + 18f * u, floorY - 48f * u)
      val backStart = Offset(cx - 38f * u, floorY - 110f * u)
      val backEnd = Offset(cx - 10f * u, floorY - 47f * u)

      drawVectorBench(seatStart, seatEnd, floorY, u)
      drawLine(VectorAthletePalette.benchFrame, Offset(cx - 30f * u, floorY - 94f * u), Offset(cx - 30f * u, floorY), 3.6f * u, StrokeCap.Round)
      drawLine(VectorAthletePalette.dumbbellDark, Offset(cx - 38f * u, floorY), Offset(cx - 22f * u, floorY), 3f * u, StrokeCap.Round)
      drawLine(VectorAthletePalette.benchPad, backStart, backEnd, 8.5f * u, StrokeCap.Round)

      val hip = Offset(cx - 4f * u, floorY - 52f * u)
      val farHip = Offset(hip.x, hip.y - 2f * u)
      val shoulder = Offset(cx - 20f * u, floorY - 94f * u)
      val headCenter = Offset(shoulder.x - 4f * u, shoulder.y - 18f * u)

      val farKnee = Offset(cx + 26f * u, floorY - 44f * u)
      val farAnkle = Offset(cx + 28f * u, floorY - 5f * u)
      val nearKnee = Offset(cx + 34f * u, floorY - 41f * u)
      val nearAnkle = Offset(cx + 38f * u, floorY - 5f * u)

      val farShoulder = Offset(shoulder.x + 2f * u, shoulder.y - 2f * u)
      val farElbow = Offset(farShoulder.x + 2f * u, farShoulder.y + 30f * u)
      val farWrist = Offset(lerpFloat(farElbow.x + 3f * u, farElbow.x + 20f * u, p), lerpFloat(farElbow.y + 28f * u, farShoulder.y + 10f * u, p))

      drawVectorLeg(farHip, farKnee, farAnkle, u, facingDir = 1, isFarLeg = true)
      drawVectorShortsSide(farHip, farKnee, 9.8f * u, facingDir = 1, isFarSleeve = true)
      drawVectorDumbbell(farWrist, angleDeg = lerpFloat(0f, -65f, p), u)
      drawVectorArm(farShoulder, farElbow, farWrist, u, isFarArm = true)

      drawVectorHead(headCenter, 9.8f * u, facingDir = 1)
      drawVectorSideTankTorso(shoulder, hip, chestDepth = 14.5f * u, facingDir = 1)

      drawVectorLeg(hip, nearKnee, nearAnkle, u, facingDir = 1)
      drawVectorShortsSide(hip, nearKnee, 10.6f * u, facingDir = 1)

      val nearElbow = Offset(shoulder.x + 1f * u, shoulder.y + 32f * u)
      val nearWrist = Offset(lerpFloat(nearElbow.x + 2f * u, nearElbow.x + 21f * u, p), lerpFloat(nearElbow.y + 28f * u, shoulder.y + 11f * u, p))
      drawVectorDumbbell(nearWrist, angleDeg = lerpFloat(0f, -65f, p), u)
      drawVectorArm(shoulder, nearElbow, nearWrist, u)
    }

    // =========================================================================
    // 10. ARMS: CABLE TRICEPS ROPE PUSHDOWN
    // =========================================================================
    "triceps-rope-pushdown" -> {
      drawFloorShadow(Offset(cx - 24f * u, floorY + 2f * u), 50f * u, 8.5f * u)

      val towerX = cx + 82f * u
      val pulleyY = floorY - 148f * u

      val hip = Offset(cx - 24f * u, floorY - 72f * u)
      val shoulder = Offset(cx - 18f * u, floorY - 116f * u)
      val headCenter = Offset(shoulder.x + 2f * u, shoulder.y - 19f * u)

      val farHip = Offset(hip.x - 2f * u, hip.y)
      val farKnee = Offset(cx - 10f * u, floorY - 37f * u)
      val farAnkle = Offset(cx - 14f * u, floorY - 5f * u)
      val nearKnee = Offset(cx - 34f * u, floorY - 37f * u)
      val nearAnkle = Offset(cx - 42f * u, floorY - 5f * u)

      val nearElbow = Offset(shoulder.x + 3f * u, shoulder.y + 28f * u)
      val nearWrist = Offset(lerpFloat(nearElbow.x + 26f * u, nearElbow.x + 8f * u, p), lerpFloat(nearElbow.y - 2f * u, nearElbow.y + 28f * u, p))
      val farElbow = Offset(shoulder.x + 5f * u, shoulder.y + 26f * u)
      val farWrist = Offset(nearWrist.x + 3f * u, nearWrist.y - 3f * u)

      val ropeTop = nearWrist + Offset(12f * u, -16f * u)
      drawVectorCableColumn(towerX, floorY - 156f * u, floorY, pulleyY, listOf(ropeTop), u)
      drawLine(VectorAthletePalette.dumbbellDark, ropeTop, nearWrist, 2.5f * u, StrokeCap.Round)
      drawLine(VectorAthletePalette.dumbbellDark, ropeTop, farWrist, 2.5f * u, StrokeCap.Round)

      drawVectorLeg(farHip, farKnee, farAnkle, u, facingDir = 1, isFarLeg = true)
      drawVectorShortsSide(farHip, farKnee, 9.8f * u, facingDir = 1, isFarSleeve = true)
      drawVectorArm(Offset(shoulder.x + 2f * u, shoulder.y - 2f * u), farElbow, farWrist, u, isFarArm = true)

      drawVectorHead(headCenter, 10f * u, facingDir = 1)
      drawVectorSideTankTorso(shoulder, hip, chestDepth = 15f * u, facingDir = 1)

      drawVectorLeg(hip, nearKnee, nearAnkle, u, facingDir = 1)
      drawVectorShortsSide(hip, nearKnee, 10.6f * u, facingDir = 1)

      drawVectorArm(shoulder, nearElbow, nearWrist, u)
      drawCircle(VectorAthletePalette.dumbbellDark, 3.6f * u, nearWrist)
    }

    // =========================================================================
    // 11. LEGS: BARBELL BACK SQUAT
    // =========================================================================
    "barbell-back-squat" -> {
      drawFloorShadow(Offset(cx, floorY + 2f * u), 56f * u, 8.5f * u)

      val hipY = lerpFloat(floorY - 72f * u, floorY - 46f * u, p)
      val shoulderY = lerpFloat(floorY - 116f * u, floorY - 86f * u, p)
      val headCenter = Offset(cx, shoulderY - 22f * u)
      val neckTop = Offset(cx, shoulderY - 11f * u)
      val lShoulder = Offset(cx - 21f * u, shoulderY)
      val rShoulder = Offset(cx + 21f * u, shoulderY)

      val lHip = Offset(cx - 10f * u, hipY)
      val rHip = Offset(cx + 10f * u, hipY)
      val lKnee = Offset(lerpFloat(cx - 17f * u, cx - 32f * u, p), lerpFloat(floorY - 38f * u, floorY - 42f * u, p))
      val rKnee = Offset(lerpFloat(cx + 17f * u, cx + 32f * u, p), lerpFloat(floorY - 38f * u, floorY - 42f * u, p))
      val lAnkle = Offset(cx - 22f * u, floorY - 5f * u)
      val rAnkle = Offset(cx + 22f * u, floorY - 5f * u)

      val barY = shoulderY - 4f * u
      drawVectorBarbellFront(Offset(cx - 56f * u, barY), Offset(cx + 56f * u, barY), u)

      drawVectorLeg(lHip, lKnee, lAnkle, u, -1)
      drawVectorLeg(rHip, rKnee, rAnkle, u, 1)
      drawVectorShortsFront(Offset(cx, hipY), 17f * u, lHip, lKnee, rHip, rKnee, u)

      drawVectorHead(headCenter, 10.2f * u, facingDir = 0)
      drawVectorFrontTankTorso(
        neckTop = neckTop,
        leftShoulder = lShoulder,
        rightShoulder = rShoulder,
        waistY = lerpFloat(shoulderY + 29f * u, shoulderY + 26f * u, p),
        waistHalfWidth = 13.5f * u,
        hipY = hipY,
        hipHalfWidth = 16f * u
      )

      val lElbow = Offset(cx - 35f * u, shoulderY + 12f * u)
      val rElbow = Offset(cx + 35f * u, shoulderY + 12f * u)
      val lWrist = Offset(cx - 33f * u, barY)
      val rWrist = Offset(cx + 33f * u, barY)

      drawVectorArm(lShoulder, lElbow, lWrist, u)
      drawVectorArm(rShoulder, rElbow, rWrist, u)
    }

    // =========================================================================
    // 12. LEGS: DUMBBELL ROMANIAN DEADLIFT (RDL)
    // =========================================================================
    "romanian-deadlift" -> {
      drawFloorShadow(Offset(cx + 6f * u, floorY + 2f * u), 50f * u, 8.5f * u)

      val hip = Offset(lerpFloat(cx + 4f * u, cx + 28f * u, p), lerpFloat(floorY - 72f * u, floorY - 68f * u, p))
      val farHip = Offset(hip.x - 4f * u, hip.y)
      val shoulder = Offset(lerpFloat(cx + 2f * u, cx - 28f * u, p), lerpFloat(floorY - 116f * u, floorY - 76f * u, p))
      val headCenter = Offset(shoulder.x - 5f * u - p * 9f * u, shoulder.y - 18f * u + p * 8f * u)

      val farKnee = Offset(cx + 4f * u + p * 7f * u, floorY - 38f * u)
      val farAnkle = Offset(cx + 6f * u, floorY - 5f * u)
      val nearKnee = Offset(cx + 8f * u + p * 8f * u, floorY - 36f * u)
      val nearAnkle = Offset(cx + 12f * u, floorY - 5f * u)

      val farHand = Offset(lerpFloat(cx - 2f * u, cx - 22f * u, p), lerpFloat(floorY - 64f * u, floorY - 30f * u, p))
      val nearHand = Offset(lerpFloat(cx + 4f * u, cx - 16f * u, p), lerpFloat(floorY - 62f * u, floorY - 28f * u, p))

      drawVectorLeg(farHip, farKnee, farAnkle, u, facingDir = -1, isFarLeg = true)
      drawVectorShortsSide(farHip, farKnee, 9.8f * u, facingDir = -1, isFarSleeve = true)
      drawVectorDumbbell(farHand, angleDeg = 0f, u)
      drawVectorArm(Offset(shoulder.x + 2f * u, shoulder.y - 2f * u), lerpOffset(shoulder, farHand, 0.48f), farHand, u, isFarArm = true)

      drawVectorHead(headCenter, 9.8f * u, facingDir = -1)
      drawVectorSideTankTorso(shoulder, hip, chestDepth = 15f * u, facingDir = -1)

      drawVectorLeg(hip, nearKnee, nearAnkle, u, facingDir = -1)
      drawVectorShortsSide(hip, nearKnee, 10.6f * u, facingDir = -1)

      drawVectorDumbbell(nearHand, angleDeg = 0f, u)
      drawVectorArm(shoulder, lerpOffset(shoulder, nearHand, 0.48f), nearHand, u)
    }

    // =========================================================================
    // 13. LEGS: ELEVATED STANDING CALF RAISE
    // =========================================================================
    "standing-calf-raises" -> {
      drawFloorShadow(Offset(cx - 4f * u, floorY + 2f * u), 54f * u, 8.5f * u)

      val stepHeight = 12f * u
      val stepTop = floorY - stepHeight
      drawRoundRect(
        color = VectorAthletePalette.dumbbellDark,
        topLeft = Offset(cx - 22f * u, stepTop),
        size = Size(56f * u, stepHeight),
        cornerRadius = CornerRadius(3f * u, 3f * u)
      )
      drawLine(VectorAthletePalette.benchFrame, Offset(cx - 48f * u, floorY - 102f * u), Offset(cx - 48f * u, floorY), 4f * u, StrokeCap.Round)
      drawLine(VectorAthletePalette.steelBar, Offset(cx - 56f * u, floorY - 102f * u), Offset(cx - 34f * u, floorY - 102f * u), 3.5f * u, StrokeCap.Round)

      val rise = lerpFloat(-4f * u, 13f * u, p)
      val shoeTilt = lerpFloat(14f, -26f, p)

      val hip = Offset(cx + 4f * u, stepTop - 70f * u - rise)
      val farHip = Offset(hip.x - 3f * u, hip.y)
      val shoulder = Offset(cx + 4f * u, stepTop - 114f * u - rise)
      val headCenter = Offset(cx + 4f * u, stepTop - 133f * u - rise)

      val farKnee = Offset(cx + 2f * u, stepTop - 36f * u - rise * 0.9f)
      val farAnkle = Offset(cx + 2f * u, stepTop - 7f * u - rise)
      val nearKnee = Offset(cx + 6f * u, stepTop - 35f * u - rise * 0.9f)
      val nearAnkle = Offset(cx + 6f * u, stepTop - 6f * u - rise)

      val farElbow = Offset(cx - 18f * u, shoulder.y + 18f * u)
      val farWrist = Offset(cx - 38f * u, floorY - 102f * u)

      drawVectorLeg(farHip, farKnee, farAnkle, u, facingDir = -1, isFarLeg = true, shoeAngleDeg = shoeTilt)
      drawVectorShortsSide(farHip, farKnee, 9.8f * u, facingDir = -1, isFarSleeve = true)
      drawVectorArm(Offset(shoulder.x - 2f * u, shoulder.y - 2f * u), farElbow, farWrist, u, isFarArm = true)

      drawVectorHead(headCenter, 9.8f * u, facingDir = -1)
      drawVectorSideTankTorso(shoulder, hip, chestDepth = 15f * u, facingDir = -1)

      drawVectorLeg(hip, nearKnee, nearAnkle, u, facingDir = -1, shoeAngleDeg = shoeTilt)
      drawVectorShortsSide(hip, nearKnee, 10.6f * u, facingDir = -1)

      val nearElbow = Offset(shoulder.x + 5f * u, shoulder.y + 24f * u)
      val nearWrist = Offset(shoulder.x + 6f * u, shoulder.y + 48f * u)
      drawVectorDumbbell(nearWrist, angleDeg = 0f, u)
      drawVectorArm(shoulder, nearElbow, nearWrist, u)
    }

    // =========================================================================
    // 14. CORE: HANGING KNEE / LEG RAISE
    // =========================================================================
    "hanging-knee-raise" -> {
      drawFloorShadow(Offset(cx, floorY + 2f * u), 56f * u, 8.5f * u)

      val barY = floorY - 154f * u
      drawLine(VectorAthletePalette.benchFrame, Offset(cx - 48f * u, barY), Offset(cx - 48f * u, floorY), 4f * u, StrokeCap.Round)
      drawLine(VectorAthletePalette.benchFrame, Offset(cx + 48f * u, barY), Offset(cx + 48f * u, floorY), 4f * u, StrokeCap.Round)
      drawLine(VectorAthletePalette.dumbbellDark, Offset(cx - 52f * u, barY), Offset(cx + 52f * u, barY), 4f * u, StrokeCap.Round)

      val lShoulder = Offset(cx - 19f * u, floorY - 120f * u)
      val rShoulder = Offset(cx + 19f * u, floorY - 120f * u)
      val neckTop = Offset(cx, floorY - 128f * u)
      val headCenter = Offset(cx, floorY - 137f * u)
      val hipY = lerpFloat(floorY - 76f * u, floorY - 79f * u, p)

      drawVectorArm(lShoulder, Offset(cx - 21f * u, floorY - 138f * u), Offset(cx - 21f * u, barY), u)
      drawVectorArm(rShoulder, Offset(cx + 21f * u, floorY - 138f * u), Offset(cx + 21f * u, barY), u)

      val lHip = Offset(cx - 9f * u, hipY)
      val rHip = Offset(cx + 9f * u, hipY)
      val lKnee = Offset(lerpFloat(cx - 10f * u, cx - 12f * u, p), lerpFloat(floorY - 43f * u, floorY - 82f * u, p))
      val rKnee = Offset(lerpFloat(cx + 10f * u, cx + 12f * u, p), lerpFloat(floorY - 43f * u, floorY - 82f * u, p))
      val lAnkle = Offset(lerpFloat(cx - 11f * u, cx - 12f * u, p), lerpFloat(floorY - 12f * u, floorY - 52f * u, p))
      val rAnkle = Offset(lerpFloat(cx + 11f * u, cx + 12f * u, p), lerpFloat(floorY - 12f * u, floorY - 52f * u, p))

      drawVectorHead(headCenter, 9.8f * u, facingDir = 0)
      drawVectorFrontTankTorso(
        neckTop = neckTop,
        leftShoulder = lShoulder,
        rightShoulder = rShoulder,
        waistY = floorY - 90f * u,
        waistHalfWidth = 13f * u,
        hipY = hipY,
        hipHalfWidth = 15.5f * u
      )
      drawVectorLeg(lHip, lKnee, lAnkle, u, -1)
      drawVectorLeg(rHip, rKnee, rAnkle, u, 1)
      drawVectorShortsFront(Offset(cx, hipY), 16.5f * u, lHip, lKnee, rHip, rKnee, u)
    }

    // =========================================================================
    // 15. CORE: STANDING CABLE ROTATIONAL WOODCHOPPER
    // =========================================================================
    "cable-woodchopper" -> {
      drawFloorShadow(Offset(cx, floorY + 2f * u), 56f * u, 8.5f * u)

      val towerX = cx - 96f * u
      val pulleyY = floorY - 116f * u
      val hipY = floorY - 70f * u
      val headCenter = Offset(cx, floorY - 138f * u)
      val neckTop = Offset(cx, floorY - 126f * u)

      val torsoShift = lerpFloat(-4f * u, 6f * u, p)
      val lShoulder = Offset(cx - 19f * u + torsoShift, floorY - 113f * u)
      val rShoulder = Offset(cx + 19f * u + torsoShift, floorY - 113f * u)

      val lHip = Offset(cx - 10f * u, hipY)
      val rHip = Offset(cx + 10f * u, hipY)
      val lKnee = Offset(lerpFloat(cx - 24f * u, cx - 18f * u, p), floorY - 37f * u)
      val lAnkle = Offset(cx - 26f * u, floorY - 5f * u)
      val rKnee = Offset(cx + 24f * u, floorY - 37f * u)
      val rAnkle = Offset(cx + 26f * u, floorY - 5f * u)

      val handle = Offset(lerpFloat(cx - 46f * u, cx + 44f * u, p), lerpFloat(floorY - 112f * u, floorY - 76f * u, p))
      val lElbow = lerpOffset(lShoulder, handle, 0.50f) + Offset(0f, 3f * u)
      val rElbow = lerpOffset(rShoulder, handle, 0.50f) + Offset(0f, 3f * u)

      drawVectorCableColumn(towerX, floorY - 150f * u, floorY, pulleyY, listOf(handle), u)

      drawVectorLeg(lHip, lKnee, lAnkle, u, -1, shoeAngleDeg = p * -16f)
      drawVectorLeg(rHip, rKnee, rAnkle, u, 1)
      drawVectorShortsFront(Offset(cx, hipY), 17f * u, lHip, lKnee, rHip, rKnee, u)

      drawVectorHead(headCenter, 10.2f * u, facingDir = if (p > 0.5f) 1 else -1)
      drawVectorFrontTankTorso(
        neckTop = neckTop,
        leftShoulder = lShoulder,
        rightShoulder = rShoulder,
        waistY = floorY - 84f * u,
        waistHalfWidth = 13f * u,
        hipY = hipY,
        hipHalfWidth = 15.5f * u
      )

      drawVectorArm(lShoulder, lElbow, handle, u)
      drawVectorArm(rShoulder, rElbow, handle, u)
      drawCircle(VectorAthletePalette.dumbbellDark, 4.0f * u, handle)
    }

    // Fallback
    else -> {
      drawFloorShadow(Offset(cx, floorY + 2f * u), 48f * u, 8.5f * u)
      val hipY = floorY - 70f * u
      val headCenter = Offset(cx, floorY - 138f * u)
      val neckTop = Offset(cx, floorY - 126f * u)
      val lShoulder = Offset(cx - 21f * u, floorY - 113f * u)
      val rShoulder = Offset(cx + 21f * u, floorY - 113f * u)
      val lHip = Offset(cx - 10f * u, hipY)
      val rHip = Offset(cx + 10f * u, hipY)
      val lKnee = Offset(cx - 12f * u, floorY - 38f * u)
      val rKnee = Offset(cx + 12f * u, floorY - 38f * u)
      drawVectorLeg(lHip, lKnee, Offset(cx - 13f * u, floorY - 5f * u), u, -1)
      drawVectorLeg(rHip, rKnee, Offset(cx + 13f * u, floorY - 5f * u), u, 1)
      drawVectorShortsFront(Offset(cx, hipY), 16.5f * u, lHip, lKnee, rHip, rKnee, u)
      drawVectorHead(headCenter, 10.2f * u, facingDir = 0)
      drawVectorFrontTankTorso(neckTop, lShoulder, rShoulder, floorY - 84f * u, 13f * u, hipY, 15.5f * u)
      drawVectorArm(lShoulder, Offset(cx - 24f * u, floorY - 92f * u), Offset(cx - 22f * u, floorY - 70f * u), u)
      drawVectorArm(rShoulder, Offset(cx + 24f * u, floorY - 92f * u), Offset(cx + 22f * u, floorY - 70f * u), u)
    }
  }
}
