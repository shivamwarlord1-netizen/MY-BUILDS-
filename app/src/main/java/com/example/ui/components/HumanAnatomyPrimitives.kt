package com.example.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Clean flat-vector character & equipment color palette matching the friendly studio illustration style:
 * - Warm tan skin with subtle depth shading on far-side limbs
 * - Light sky-blue sleeveless athletic tank top
 * - Relaxed navy slate athletic shorts
 * - Crisp white sneakers with blue sole trim & white crew socks
 */
internal object VectorAthletePalette {
  val skinLight = Color(0xFFFAC196)
  val skinBase = Color(0xFFF5AB77)
  val skinShadow = Color(0xFFDF8E59)

  // Slightly deeper tone for far-side limb separation in 3/4 & side angles
  val farSkinBase = Color(0xFFE3935D)
  val farSkinShadow = Color(0xFFC97843)

  val hairDark = Color(0xFF23252A)
  val hairHighlight = Color(0xFF363A43)
  val eyeDark = Color(0xFF1F2126)
  val mouthTone = Color(0xFFB56642)

  val tankBlue = Color(0xFF74A6DC)
  val tankFold = Color(0xFF5D8FC6)
  val tankOutline = Color(0xFF4E7EB4)

  val shortsNavy = Color(0xFF283D58)
  val shortsDark = Color(0xFF1C2C42)

  val sockWhite = Color(0xFFF4F6FA)
  val shoeWhite = Color(0xFFFFFFFF)
  val shoeShade = Color(0xFFDDE2EB)
  val shoeBlue = Color(0xFF3A6FA6)
  val shoeBlueDark = Color(0xFF26507D)

  val floorShadow = Color(0xFFE2E5EB)

  val dumbbellDark = Color(0xFF34383F)
  val dumbbellBevel = Color(0xFF525863)
  val steelBar = Color(0xFF9EA6B2)
  val steelLight = Color(0xFFCBD2DC)
  val benchPad = Color(0xFF2A3240)
  val benchFrame = Color(0xFF566070)
}

internal fun lerpFloat(start: Float, stop: Float, fraction: Float): Float {
  return start + (stop - start) * fraction
}

internal fun lerpOffset(start: Offset, stop: Offset, fraction: Float): Offset {
  return Offset(
    x = lerpFloat(start.x, stop.x, fraction),
    y = lerpFloat(start.y, stop.y, fraction)
  )
}

/**
 * Draws a soft neutral elliptical floor shadow directly beneath the athlete.
 */
internal fun DrawScope.drawFloorShadow(
  center: Offset,
  radiusX: Float,
  radiusY: Float
) {
  drawOval(
    color = VectorAthletePalette.floorShadow,
    topLeft = Offset(center.x - radiusX, center.y - radiusY),
    size = Size(radiusX * 2f, radiusY * 2f)
  )
}

/**
 * Draws an expressive friendly flat-vector human head with tousled dark hair, ears,
 * eyebrows, eyes, subtle nose, and calm smile across Front, Back, and Side viewpoints.
 */
internal fun DrawScope.drawVectorHead(
  center: Offset,
  radius: Float,
  facingDir: Int = 0,
  isBackView: Boolean = false
) {
  // Ears
  if (facingDir == 0) {
    drawCircle(VectorAthletePalette.skinShadow, radius * 0.23f, Offset(center.x - radius * 0.94f, center.y + radius * 0.08f))
    drawCircle(VectorAthletePalette.skinBase, radius * 0.16f, Offset(center.x - radius * 0.92f, center.y + radius * 0.08f))
    drawCircle(VectorAthletePalette.skinShadow, radius * 0.23f, Offset(center.x + radius * 0.94f, center.y + radius * 0.08f))
    drawCircle(VectorAthletePalette.skinBase, radius * 0.16f, Offset(center.x + radius * 0.92f, center.y + radius * 0.08f))
  } else {
    drawCircle(VectorAthletePalette.skinShadow, radius * 0.23f, Offset(center.x - facingDir * radius * 0.28f, center.y + radius * 0.10f))
  }

  // Smooth friendly head & jaw silhouette
  val headPath = Path().apply {
    if (facingDir == 0) {
      moveTo(center.x, center.y - radius * 1.02f)
      cubicTo(
        center.x + radius * 1.02f, center.y - radius * 1.02f,
        center.x + radius * 0.98f, center.y + radius * 0.30f,
        center.x + radius * 0.56f, center.y + radius * 0.88f
      )
      cubicTo(
        center.x + radius * 0.22f, center.y + radius * 1.18f,
        center.x - radius * 0.22f, center.y + radius * 1.18f,
        center.x - radius * 0.56f, center.y + radius * 0.88f
      )
      cubicTo(
        center.x - radius * 0.98f, center.y + radius * 0.30f,
        center.x - radius * 1.02f, center.y - radius * 1.02f,
        center.x, center.y - radius * 1.02f
      )
    } else {
      val d = facingDir.toFloat()
      moveTo(center.x - d * radius * 0.1f, center.y - radius * 1.02f)
      cubicTo(
        center.x + d * radius * 0.92f, center.y - radius * 0.95f,
        center.x + d * radius * 1.06f, center.y - radius * 0.08f,
        center.x + d * radius * 0.92f, center.y + radius * 0.38f
      )
      cubicTo(
        center.x + d * radius * 0.82f, center.y + radius * 0.94f,
        center.x + d * radius * 0.32f, center.y + radius * 1.14f,
        center.x - d * radius * 0.25f, center.y + radius * 0.88f
      )
      cubicTo(
        center.x - d * radius * 0.95f, center.y + radius * 0.55f,
        center.x - d * radius * 1.02f, center.y - radius * 0.72f,
        center.x - d * radius * 0.1f, center.y - radius * 1.02f
      )
    }
    close()
  }
  drawPath(headPath, VectorAthletePalette.skinBase)

  if (isBackView) {
    // Full posterior hair coverage
    val backHair = Path().apply {
      moveTo(center.x - radius * 0.96f, center.y + radius * 0.22f)
      cubicTo(
        center.x - radius * 1.08f, center.y - radius * 1.20f,
        center.x + radius * 1.08f, center.y - radius * 1.20f,
        center.x + radius * 0.96f, center.y + radius * 0.22f
      )
      cubicTo(
        center.x + radius * 0.62f, center.y + radius * 0.84f,
        center.x - radius * 0.62f, center.y + radius * 0.84f,
        center.x - radius * 0.96f, center.y + radius * 0.22f
      )
      close()
    }
    drawPath(backHair, VectorAthletePalette.hairDark)
  } else if (facingDir == 0) {
    // Tousled side-swept dark hair matching reference illustration
    val hairPath = Path().apply {
      moveTo(center.x - radius * 0.94f, center.y - radius * 0.12f)
      cubicTo(
        center.x - radius * 1.10f, center.y - radius * 0.95f,
        center.x - radius * 0.45f, center.y - radius * 1.28f,
        center.x + radius * 0.12f, center.y - radius * 1.22f
      )
      cubicTo(
        center.x + radius * 0.78f, center.y - radius * 1.18f,
        center.x + radius * 1.08f, center.y - radius * 0.72f,
        center.x + radius * 0.94f, center.y - radius * 0.12f
      )
      // Sideburn & fringe contour
      cubicTo(
        center.x + radius * 0.78f, center.y - radius * 0.56f,
        center.x + radius * 0.35f, center.y - radius * 0.55f,
        center.x + radius * 0.05f, center.y - radius * 0.68f
      )
      cubicTo(
        center.x - radius * 0.35f, center.y - radius * 0.55f,
        center.x - radius * 0.78f, center.y - radius * 0.56f,
        center.x - radius * 0.94f, center.y - radius * 0.12f
      )
      close()
    }
    drawPath(hairPath, VectorAthletePalette.hairDark)

    // Friendly eyebrows, expressive dark eyes, gentle nose, calm smile
    val eyeY = center.y + radius * 0.02f
    val eyeOffsetX = radius * 0.34f
    val browY = center.y - radius * 0.18f

    // Brows
    drawLine(
      color = VectorAthletePalette.hairDark,
      start = Offset(center.x - eyeOffsetX - radius * 0.15f, browY),
      end = Offset(center.x - eyeOffsetX + radius * 0.15f, browY - radius * 0.03f),
      strokeWidth = (radius * 0.09f).coerceAtLeast(1.1f),
      cap = StrokeCap.Round
    )
    drawLine(
      color = VectorAthletePalette.hairDark,
      start = Offset(center.x + eyeOffsetX - radius * 0.15f, browY - radius * 0.03f),
      end = Offset(center.x + eyeOffsetX + radius * 0.15f, browY),
      strokeWidth = (radius * 0.09f).coerceAtLeast(1.1f),
      cap = StrokeCap.Round
    )

    // Eyes
    drawCircle(VectorAthletePalette.eyeDark, radius * 0.095f, Offset(center.x - eyeOffsetX, eyeY))
    drawCircle(VectorAthletePalette.eyeDark, radius * 0.095f, Offset(center.x + eyeOffsetX, eyeY))

    // Nose hint
    drawLine(
      color = VectorAthletePalette.skinShadow,
      start = Offset(center.x, eyeY + radius * 0.06f),
      end = Offset(center.x + radius * 0.05f, eyeY + radius * 0.28f),
      strokeWidth = (radius * 0.08f).coerceAtLeast(1f),
      cap = StrokeCap.Round
    )

    // Calm smile
    val smilePath = Path().apply {
      moveTo(center.x - radius * 0.22f, center.y + radius * 0.50f)
      quadraticTo(
        center.x, center.y + radius * 0.66f,
        center.x + radius * 0.22f, center.y + radius * 0.50f
      )
    }
    drawPath(
      path = smilePath,
      color = VectorAthletePalette.mouthTone,
      style = Stroke(width = (radius * 0.09f).coerceAtLeast(1.1f), cap = StrokeCap.Round)
    )
  } else {
    // Side-profile tousled hair + eye + brow + smile
    val d = facingDir.toFloat()
    val sideHair = Path().apply {
      moveTo(center.x + d * radius * 0.72f, center.y - radius * 0.48f)
      cubicTo(
        center.x + d * radius * 0.62f, center.y - radius * 1.24f,
        center.x - d * radius * 1.05f, center.y - radius * 1.12f,
        center.x - d * radius * 0.88f, center.y + radius * 0.20f
      )
      lineTo(center.x - d * radius * 0.36f, center.y + radius * 0.14f)
      cubicTo(
        center.x - d * radius * 0.22f, center.y - radius * 0.42f,
        center.x + d * radius * 0.22f, center.y - radius * 0.48f,
        center.x + d * radius * 0.72f, center.y - radius * 0.48f
      )
      close()
    }
    drawPath(sideHair, VectorAthletePalette.hairDark)

    // Side profile brow & eye & mouth
    drawLine(
      color = VectorAthletePalette.hairDark,
      start = Offset(center.x + d * radius * 0.28f, center.y - radius * 0.16f),
      end = Offset(center.x + d * radius * 0.64f, center.y - radius * 0.14f),
      strokeWidth = (radius * 0.09f).coerceAtLeast(1f),
      cap = StrokeCap.Round
    )
    drawCircle(
      color = VectorAthletePalette.eyeDark,
      radius = radius * 0.09f,
      center = Offset(center.x + d * radius * 0.48f, center.y + radius * 0.03f)
    )
    drawLine(
      color = VectorAthletePalette.mouthTone,
      start = Offset(center.x + d * radius * 0.48f, center.y + radius * 0.54f),
      end = Offset(center.x + d * radius * 0.76f, center.y + radius * 0.50f),
      strokeWidth = (radius * 0.09f).coerceAtLeast(1f),
      cap = StrokeCap.Round
    )
  }
}

/**
 * Draws the front upper body wearing the light sky-blue sleeveless athletic tank top,
 * with natural tan neck and collar skin above the scoop neckline.
 */
internal fun DrawScope.drawVectorFrontTankTorso(
  neckTop: Offset,
  leftShoulder: Offset,
  rightShoulder: Offset,
  waistY: Float,
  waistHalfWidth: Float,
  hipY: Float,
  hipHalfWidth: Float
) {
  val cx = neckTop.x
  val shoulderSpan = (rightShoulder.x - leftShoulder.x).coerceAtLeast(10f)
  val neckHalfW = shoulderSpan * 0.17f
  val neckBaseY = (leftShoulder.y + rightShoulder.y) * 0.5f - shoulderSpan * 0.06f
  val strapOuterX = shoulderSpan * 0.36f
  val strapInnerX = shoulderSpan * 0.22f
  val chestMidY = (leftShoulder.y + rightShoulder.y) * 0.5f + shoulderSpan * 0.22f

  // 1. Warm tan neck + upper chest collar triangle
  val neckPath = Path().apply {
    moveTo(cx - neckHalfW, neckTop.y)
    lineTo(cx + neckHalfW, neckTop.y)
    cubicTo(
      cx + neckHalfW * 1.15f, neckBaseY,
      cx + strapOuterX, leftShoulder.y - shoulderSpan * 0.02f,
      cx + strapOuterX, leftShoulder.y + shoulderSpan * 0.04f
    )
    lineTo(cx + strapInnerX, chestMidY)
    lineTo(cx - strapInnerX, chestMidY)
    lineTo(cx - strapOuterX, leftShoulder.y + shoulderSpan * 0.04f)
    cubicTo(
      cx - strapOuterX, leftShoulder.y - shoulderSpan * 0.02f,
      cx - neckHalfW * 1.15f, neckBaseY,
      cx - neckHalfW, neckTop.y
    )
    close()
  }
  drawPath(neckPath, VectorAthletePalette.skinBase)

  // Subtle under-chin shadow
  drawOval(
    color = VectorAthletePalette.skinShadow.copy(alpha = 0.65f),
    topLeft = Offset(cx - neckHalfW * 0.85f, neckTop.y - neckHalfW * 0.2f),
    size = Size(neckHalfW * 1.7f, neckHalfW * 0.8f)
  )

  // 2. Light Sky-Blue Sleeveless Athletic Tank Top
  val armHoleY = (leftShoulder.y + rightShoulder.y) * 0.5f + shoulderSpan * 0.26f
  val tankBottomY = hipY + (hipY - waistY) * 0.16f
  val torsoWidth = (waistHalfWidth * 1.14f).coerceAtLeast(hipHalfWidth * 0.96f)

  val tankPath = Path().apply {
    // Left strap top
    moveTo(cx - strapOuterX, leftShoulder.y)
    lineTo(cx - strapInnerX, leftShoulder.y)
    // Smooth U-shaped scoop neckline
    cubicTo(
      cx - strapInnerX * 0.6f, chestMidY,
      cx + strapInnerX * 0.6f, chestMidY,
      cx + strapInnerX, rightShoulder.y
    )
    // Right strap top
    lineTo(cx + strapOuterX, rightShoulder.y)
    // Right armhole curve inward to underarm
    cubicTo(
      cx + strapOuterX + shoulderSpan * 0.02f, armHoleY * 0.5f + rightShoulder.y * 0.5f,
      cx + torsoWidth * 1.04f, armHoleY - shoulderSpan * 0.05f,
      cx + torsoWidth * 1.02f, armHoleY
    )
    // Relaxed athletic tank side down to hem
    cubicTo(
      cx + torsoWidth * 0.98f, lerpFloat(armHoleY, tankBottomY, 0.55f),
      cx + hipHalfWidth * 1.04f, tankBottomY - shoulderSpan * 0.05f,
      cx + hipHalfWidth * 1.04f, tankBottomY
    )
    // Gently curved hem
    quadraticTo(
      cx, tankBottomY + shoulderSpan * 0.05f,
      cx - hipHalfWidth * 1.04f, tankBottomY
    )
    // Left side up to left underarm
    cubicTo(
      cx - hipHalfWidth * 1.04f, tankBottomY - shoulderSpan * 0.05f,
      cx - torsoWidth * 0.98f, lerpFloat(armHoleY, tankBottomY, 0.55f),
      cx - torsoWidth * 1.02f, armHoleY
    )
    // Left armhole back to left strap
    cubicTo(
      cx - torsoWidth * 1.04f, armHoleY - shoulderSpan * 0.05f,
      cx - strapOuterX - shoulderSpan * 0.02f, armHoleY * 0.5f + leftShoulder.y * 0.5f,
      cx - strapOuterX, leftShoulder.y
    )
    close()
  }
  drawPath(tankPath, VectorAthletePalette.tankBlue)

  // Subtle scoop collar trim & lower hem fold
  drawPath(
    path = Path().apply {
      moveTo(cx - strapInnerX, leftShoulder.y + shoulderSpan * 0.02f)
      cubicTo(
        cx - strapInnerX * 0.6f, chestMidY,
        cx + strapInnerX * 0.6f, chestMidY,
        cx + strapInnerX, rightShoulder.y + shoulderSpan * 0.02f
      )
    },
    color = VectorAthletePalette.tankFold,
    style = Stroke(width = (shoulderSpan * 0.045f).coerceAtLeast(1.2f), cap = StrokeCap.Round)
  )
  drawLine(
    color = VectorAthletePalette.tankFold,
    start = Offset(cx - hipHalfWidth * 0.95f, tankBottomY - shoulderSpan * 0.03f),
    end = Offset(cx + hipHalfWidth * 0.95f, tankBottomY - shoulderSpan * 0.03f),
    strokeWidth = (shoulderSpan * 0.035f).coerceAtLeast(1f),
    cap = StrokeCap.Round
  )
}

/**
 * Draws the posterior (back) upper body wearing the sky-blue athletic tank top (used for Lat Pulldown).
 */
internal fun DrawScope.drawVectorBackTankTorso(
  neckTop: Offset,
  leftShoulder: Offset,
  rightShoulder: Offset,
  waistY: Float,
  waistHalfWidth: Float,
  hipY: Float,
  hipHalfWidth: Float
) {
  val cx = neckTop.x
  val shoulderSpan = (rightShoulder.x - leftShoulder.x).coerceAtLeast(10f)
  val neckHalfW = shoulderSpan * 0.17f
  val neckBaseY = (leftShoulder.y + rightShoulder.y) * 0.5f - shoulderSpan * 0.05f
  val strapOuterX = shoulderSpan * 0.35f
  val strapInnerX = shoulderSpan * 0.18f

  // Upper back / shoulder skin visible around racerback tank
  val skinPath = Path().apply {
    moveTo(cx - neckHalfW, neckTop.y)
    lineTo(cx + neckHalfW, neckTop.y)
    cubicTo(
      cx + neckHalfW * 1.2f, neckBaseY,
      rightShoulder.x - shoulderSpan * 0.08f, rightShoulder.y - shoulderSpan * 0.03f,
      rightShoulder.x, rightShoulder.y
    )
    lineTo(cx + waistHalfWidth * 1.05f, waistY)
    lineTo(cx - waistHalfWidth * 1.05f, waistY)
    lineTo(leftShoulder.x, leftShoulder.y)
    cubicTo(
      leftShoulder.x + shoulderSpan * 0.08f, leftShoulder.y - shoulderSpan * 0.03f,
      cx - neckHalfW * 1.2f, neckBaseY,
      cx - neckHalfW, neckTop.y
    )
    close()
  }
  drawPath(skinPath, VectorAthletePalette.skinBase)

  // Sky-blue tank top back coverage
  val armHoleY = (leftShoulder.y + rightShoulder.y) * 0.5f + shoulderSpan * 0.25f
  val tankBottomY = hipY + (hipY - waistY) * 0.15f
  val tankPath = Path().apply {
    moveTo(cx - strapOuterX, leftShoulder.y)
    lineTo(cx - strapInnerX, leftShoulder.y)
    quadraticTo(cx, leftShoulder.y + shoulderSpan * 0.10f, cx + strapInnerX, rightShoulder.y)
    lineTo(cx + strapOuterX, rightShoulder.y)
    cubicTo(
      cx + strapOuterX, armHoleY * 0.6f + rightShoulder.y * 0.4f,
      cx + waistHalfWidth * 1.12f, armHoleY,
      cx + waistHalfWidth * 1.08f, armHoleY + shoulderSpan * 0.05f
    )
    lineTo(cx + hipHalfWidth * 1.04f, tankBottomY)
    lineTo(cx - hipHalfWidth * 1.04f, tankBottomY)
    lineTo(cx - waistHalfWidth * 1.08f, armHoleY + shoulderSpan * 0.05f)
    cubicTo(
      cx - waistHalfWidth * 1.12f, armHoleY,
      cx - strapOuterX, armHoleY * 0.6f + leftShoulder.y * 0.4f,
      cx - strapOuterX, leftShoulder.y
    )
    close()
  }
  drawPath(tankPath, VectorAthletePalette.tankBlue)
  drawLine(
    color = VectorAthletePalette.tankFold,
    start = Offset(cx, leftShoulder.y + shoulderSpan * 0.18f),
    end = Offset(cx, tankBottomY - shoulderSpan * 0.08f),
    strokeWidth = (shoulderSpan * 0.035f).coerceAtLeast(1f),
    cap = StrokeCap.Round
  )
}

/**
 * Draws the side/3/4 profile upper body wearing the sky-blue sleeveless tank top with warm tan shoulder/neck.
 */
internal fun DrawScope.drawVectorSideTankTorso(
  shoulder: Offset,
  hip: Offset,
  chestDepth: Float,
  facingDir: Int
) {
  val dx = hip.x - shoulder.x
  val dy = hip.y - shoulder.y
  val len = hypot(dx, dy).coerceAtLeast(1f)
  val ux = dx / len
  val uy = dy / len
  val fx = uy * facingDir
  val fy = -ux * facingDir

  // Warm tan neck & top shoulder curve
  val neckTop = Offset(shoulder.x - ux * chestDepth * 0.65f, shoulder.y - uy * chestDepth * 0.65f)
  drawLine(
    color = VectorAthletePalette.skinBase,
    start = neckTop,
    end = shoulder,
    strokeWidth = chestDepth * 0.68f,
    cap = StrokeCap.Round
  )
  drawCircle(VectorAthletePalette.skinBase, chestDepth * 0.44f, shoulder)

  // Sky-blue tank top body wrapping chest -> waist -> hip hem
  val topFront = Offset(
    shoulder.x + ux * (len * 0.04f) + fx * (chestDepth * 0.45f),
    shoulder.y + uy * (len * 0.04f) + fy * (chestDepth * 0.45f)
  )
  val midChest = Offset(
    shoulder.x + ux * (len * 0.30f) + fx * (chestDepth * 0.72f),
    shoulder.y + uy * (len * 0.30f) + fy * (chestDepth * 0.72f)
  )
  val hipFront = Offset(
    hip.x + ux * (chestDepth * 0.14f) + fx * (chestDepth * 0.56f),
    hip.y + uy * (chestDepth * 0.14f) + fy * (chestDepth * 0.56f)
  )
  val hipBack = Offset(
    hip.x + ux * (chestDepth * 0.14f) - fx * (chestDepth * 0.54f),
    hip.y + uy * (chestDepth * 0.14f) - fy * (chestDepth * 0.54f)
  )
  val midBack = Offset(
    shoulder.x + ux * (len * 0.32f) - fx * (chestDepth * 0.58f),
    shoulder.y + uy * (len * 0.32f) - fy * (chestDepth * 0.58f)
  )
  val topBack = Offset(
    shoulder.x - ux * (len * 0.04f) - fx * (chestDepth * 0.36f),
    shoulder.y - uy * (len * 0.04f) - fy * (chestDepth * 0.36f)
  )

  val tankPath = Path().apply {
    moveTo(topFront.x, topFront.y)
    cubicTo(midChest.x, midChest.y, midChest.x, midChest.y, hipFront.x, hipFront.y)
    lineTo(hipBack.x, hipBack.y)
    cubicTo(midBack.x, midBack.y, midBack.x, midBack.y, topBack.x, topBack.y)
    close()
  }
  drawPath(tankPath, VectorAthletePalette.tankBlue)
  drawLine(
    color = VectorAthletePalette.tankFold,
    start = hipBack,
    end = hipFront,
    strokeWidth = (chestDepth * 0.11f).coerceAtLeast(1.2f),
    cap = StrokeCap.Round
  )
}

/**
 * Draws a clean flat-vector tapered limb segment with optional far-limb depth shading.
 */
internal fun DrawScope.drawVectorSegment(
  start: Offset,
  end: Offset,
  startRadius: Float,
  bellyRadius: Float,
  endRadius: Float,
  isFarLimb: Boolean = false,
  bellyBias: Float = 0.45f
) {
  val dx = end.x - start.x
  val dy = end.y - start.y
  val len = hypot(dx, dy)
  if (len < 1f) return

  val nx = -dy / len
  val ny = dx / len
  val bellyCenter = lerpOffset(start, end, bellyBias)

  val path = Path().apply {
    moveTo(start.x + nx * startRadius, start.y + ny * startRadius)
    quadraticTo(
      bellyCenter.x + nx * bellyRadius, bellyCenter.y + ny * bellyRadius,
      end.x + nx * endRadius, end.y + ny * endRadius
    )
    lineTo(end.x - nx * endRadius, end.y - ny * endRadius)
    quadraticTo(
      bellyCenter.x - nx * bellyRadius, bellyCenter.y - ny * bellyRadius,
      start.x - nx * startRadius, start.y - ny * startRadius
    )
    close()
  }

  val fill = if (isFarLimb) VectorAthletePalette.farSkinBase else VectorAthletePalette.skinBase
  drawPath(path, fill)
  drawCircle(fill, startRadius * 0.96f, start)
  drawCircle(fill, endRadius * 0.96f, end)
}

/**
 * Draws a clean flat-vector human arm (smooth shoulder, tapered bicep & forearm, articulated gripping hand).
 */
internal fun DrawScope.drawVectorArm(
  shoulder: Offset,
  elbow: Offset,
  wrist: Offset,
  unit: Float,
  isFarArm: Boolean = false
) {
  val skinFill = if (isFarArm) VectorAthletePalette.farSkinBase else VectorAthletePalette.skinBase
  val handShadow = if (isFarArm) VectorAthletePalette.farSkinShadow else VectorAthletePalette.skinShadow

  drawVectorSegment(
    start = shoulder,
    end = elbow,
    startRadius = 4.3f * unit,
    bellyRadius = 4.7f * unit,
    endRadius = 3.6f * unit,
    isFarLimb = isFarArm,
    bellyBias = 0.46f
  )

  drawVectorSegment(
    start = elbow,
    end = wrist,
    startRadius = 3.6f * unit,
    bellyRadius = 3.8f * unit,
    endRadius = 2.6f * unit,
    isFarLimb = isFarArm,
    bellyBias = 0.28f
  )

  // Articulated rounded hand & folded thumb
  val angleRad = atan2(wrist.y - elbow.y, wrist.x - elbow.x)
  val handCenter = Offset(
    x = wrist.x + cos(angleRad) * 1.4f * unit,
    y = wrist.y + sin(angleRad) * 1.4f * unit
  )
  drawCircle(skinFill, 3.4f * unit, handCenter)
  drawCircle(handShadow, 3.4f * unit, handCenter, style = Stroke(0.8f * unit))
}

/**
 * Draws a clean flat-vector leg (thigh, calf, white crew sock, and white/blue athletic sneaker).
 */
internal fun DrawScope.drawVectorLeg(
  hip: Offset,
  knee: Offset,
  ankle: Offset,
  unit: Float,
  facingDir: Int,
  isFarLeg: Boolean = false,
  shoeAngleDeg: Float = 0f,
  drawShoe: Boolean = true
) {
  drawVectorSegment(
    start = hip,
    end = knee,
    startRadius = 6.0f * unit,
    bellyRadius = 6.2f * unit,
    endRadius = 4.5f * unit,
    isFarLimb = isFarLeg,
    bellyBias = 0.42f
  )

  drawVectorSegment(
    start = knee,
    end = ankle,
    startRadius = 4.4f * unit,
    bellyRadius = 4.8f * unit,
    endRadius = 2.9f * unit,
    isFarLimb = isFarLeg,
    bellyBias = 0.34f
  )

  if (drawShoe) {
    // Crisp white crew sock cuff just above sneaker
    val dir = if (facingDir == 0) 1f else facingDir.toFloat()
    val sockTop = lerpOffset(knee, ankle, 0.84f)
    drawLine(
      color = if (isFarLeg) VectorAthletePalette.shoeShade else VectorAthletePalette.sockWhite,
      start = sockTop,
      end = ankle,
      strokeWidth = 5.6f * unit,
      cap = StrokeCap.Round
    )

    drawVectorSneaker(
      ankle = ankle,
      unit = unit,
      facingDir = if (facingDir == 0) 1 else facingDir,
      angleDeg = shoeAngleDeg,
      isFarShoe = isFarLeg
    )
    // Subtle lace/tongue hint for forward-facing shoes
    if (facingDir == 0) {
      drawLine(
        color = VectorAthletePalette.shoeBlue,
        start = Offset(ankle.x - dir * 1.5f * unit, ankle.y - 0.5f * unit),
        end = Offset(ankle.x + dir * 2.5f * unit, ankle.y - 0.5f * unit),
        strokeWidth = 1.6f * unit,
        cap = StrokeCap.Round
      )
    }
  }
}

/**
 * Draws the crisp white & royal-blue athletic sneaker matching the reference illustration.
 */
internal fun DrawScope.drawVectorSneaker(
  ankle: Offset,
  unit: Float,
  facingDir: Int,
  angleDeg: Float = 0f,
  isFarShoe: Boolean = false
) {
  val dir = if (facingDir == 0) 1f else facingDir.toFloat()
  val shoeLen = 14.0f * unit
  val shoeH = 5.8f * unit
  val upperColor = if (isFarShoe) VectorAthletePalette.shoeShade else VectorAthletePalette.shoeWhite
  val trimBlue = if (isFarShoe) VectorAthletePalette.shoeBlueDark else VectorAthletePalette.shoeBlue

  rotate(degrees = angleDeg, pivot = ankle) {
    val heelX = ankle.x - dir * 4.0f * unit
    val toeX = ankle.x + dir * (shoeLen - 4.0f * unit)
    val baseY = ankle.y + shoeH * 0.62f

    // Clean white shoe upper
    val upperPath = Path().apply {
      moveTo(ankle.x - dir * 3.4f * unit, ankle.y - 1.4f * unit)
      lineTo(ankle.x + dir * 2.4f * unit, ankle.y - 1.0f * unit)
      cubicTo(
        ankle.x + dir * 6.5f * unit, ankle.y + 0.8f * unit,
        toeX, ankle.y + 1.2f * unit,
        toeX, baseY - 1.6f * unit
      )
      lineTo(heelX, baseY - 1.6f * unit)
      close()
    }
    drawPath(upperPath, upperColor)
    drawPath(upperPath, VectorAthletePalette.shoeShade, style = Stroke(width = 0.8f * unit, join = StrokeJoin.Round))

    // Blue athletic outsole
    val soleLeft = minOf(heelX, toeX)
    drawRoundRect(
      color = trimBlue,
      topLeft = Offset(soleLeft, baseY - 1.8f * unit),
      size = Size(shoeLen, 2.5f * unit),
      cornerRadius = CornerRadius(1.2f * unit, 1.2f * unit)
    )

    // Side sneaker blue stripe
    drawLine(
      color = trimBlue,
      start = Offset(ankle.x - dir * 1.4f * unit, ankle.y + 0.8f * unit),
      end = Offset(ankle.x + dir * 4.0f * unit, ankle.y + 1.6f * unit),
      strokeWidth = 1.3f * unit,
      cap = StrokeCap.Round
    )
  }
}

/**
 * Draws natural tapered navy athletic shorts in front/back views:
 * - Curved pelvis saddle with subtle waistband & center rise drape
 * - Left & right tapered sleeves aligned dynamically along each hip-to-knee axis
 * - Softly rounded mid-thigh cuffs (zero boxy corners even in wide squats or seated spreads)
 */
internal fun DrawScope.drawVectorShortsFront(
  hipCenter: Offset,
  hipHalfWidth: Float,
  leftHip: Offset,
  leftKnee: Offset,
  rightHip: Offset,
  rightKnee: Offset,
  unit: Float
) {
  // 1. Sculpted Left & Right Tapered Thigh Sleeves aligned to each leg's angle
  drawTaperedShortsSleeve(
    hipPt = leftHip,
    kneePt = leftKnee,
    topRadius = (hipHalfWidth * 0.56f).coerceAtLeast(6.6f * unit),
    cuffRadius = (hipHalfWidth * 0.48f).coerceAtLeast(5.4f * unit),
    roundTip = 2.6f * unit,
    fillColor = VectorAthletePalette.shortsNavy,
    shadeColor = VectorAthletePalette.shortsDark
  )
  drawTaperedShortsSleeve(
    hipPt = rightHip,
    kneePt = rightKnee,
    topRadius = (hipHalfWidth * 0.56f).coerceAtLeast(6.6f * unit),
    cuffRadius = (hipHalfWidth * 0.48f).coerceAtLeast(5.4f * unit),
    roundTip = 2.6f * unit,
    fillColor = VectorAthletePalette.shortsNavy,
    shadeColor = VectorAthletePalette.shortsDark
  )

  // 2. Curved Pelvis & Hip Saddle connecting both sleeves naturally
  val waistY = hipCenter.y - 3.6f * unit
  val crotchY = hipCenter.y + 7.2f * unit
  val hipY = hipCenter.y + 2.8f * unit

  val pelvisPath = Path().apply {
    moveTo(hipCenter.x - hipHalfWidth * 1.00f, waistY)
    quadraticTo(
      hipCenter.x, waistY + 1.4f * unit,
      hipCenter.x + hipHalfWidth * 1.00f, waistY
    )
    cubicTo(
      hipCenter.x + hipHalfWidth * 1.08f, lerpFloat(waistY, hipY, 0.6f),
      rightHip.x + hipHalfWidth * 0.48f, hipY,
      rightHip.x + hipHalfWidth * 0.34f, crotchY
    )
    quadraticTo(
      hipCenter.x + hipHalfWidth * 0.2f, crotchY + 1.4f * unit,
      hipCenter.x, crotchY
    )
    quadraticTo(
      hipCenter.x - hipHalfWidth * 0.2f, crotchY + 1.4f * unit,
      leftHip.x - hipHalfWidth * 0.34f, crotchY
    )
    cubicTo(
      leftHip.x - hipHalfWidth * 0.48f, hipY,
      hipCenter.x - hipHalfWidth * 1.08f, lerpFloat(waistY, hipY, 0.6f),
      hipCenter.x - hipHalfWidth * 1.00f, waistY
    )
    close()
  }
  drawPath(pelvisPath, VectorAthletePalette.shortsNavy)

  // 3. Subtle curved waistband trim & natural center fabric drape crease
  val waistbandPath = Path().apply {
    moveTo(hipCenter.x - hipHalfWidth * 0.98f, waistY + 1.8f * unit)
    quadraticTo(
      hipCenter.x, waistY + 3.0f * unit,
      hipCenter.x + hipHalfWidth * 0.98f, waistY + 1.8f * unit
    )
  }
  drawPath(
    path = waistbandPath,
    color = VectorAthletePalette.shortsDark,
    style = Stroke(width = (1.3f * unit).coerceAtLeast(1f), cap = StrokeCap.Round)
  )

  val riseFold = Path().apply {
    moveTo(hipCenter.x, waistY + 2.8f * unit)
    quadraticTo(
      hipCenter.x + 0.7f * unit, lerpFloat(waistY, crotchY, 0.6f),
      hipCenter.x, crotchY - 0.5f * unit
    )
  }
  drawPath(
    path = riseFold,
    color = VectorAthletePalette.shortsDark,
    style = Stroke(width = (1.1f * unit).coerceAtLeast(1f), cap = StrokeCap.Round)
  )
}

/**
 * Draws a single smooth, tapered shorts leg sleeve along a hip-to-knee segment
 * with curved fabric sides, rounded lower hem cuff, and subtle inner drape fold.
 */
private fun DrawScope.drawTaperedShortsSleeve(
  hipPt: Offset,
  kneePt: Offset,
  topRadius: Float,
  cuffRadius: Float,
  roundTip: Float,
  fillColor: Color,
  shadeColor: Color
) {
  val dx = kneePt.x - hipPt.x
  val dy = kneePt.y - hipPt.y
  val len = hypot(dx, dy).coerceAtLeast(8f)
  val ux = dx / len
  val uy = dy / len
  val nx = -uy
  val ny = ux

  val start = Offset(hipPt.x - ux * (roundTip * 1.15f), hipPt.y - uy * (roundTip * 1.15f))
  val cuffCenter = lerpOffset(hipPt, kneePt, 0.48f)
  val mid = lerpOffset(start, cuffCenter, 0.50f)
  val midRadius = lerpFloat(topRadius, cuffRadius, 0.35f)

  val sleevePath = Path().apply {
    moveTo(start.x + nx * topRadius, start.y + ny * topRadius)
    quadraticTo(
      mid.x + nx * midRadius, mid.y + ny * midRadius,
      cuffCenter.x + nx * cuffRadius, cuffCenter.y + ny * cuffRadius
    )
    // Softly rounded leg cuff opening
    quadraticTo(
      cuffCenter.x + ux * roundTip, cuffCenter.y + uy * roundTip,
      cuffCenter.x - nx * cuffRadius, cuffCenter.y - ny * cuffRadius
    )
    quadraticTo(
      mid.x - nx * midRadius, mid.y - ny * midRadius,
      start.x - nx * topRadius, start.y - ny * topRadius
    )
    quadraticTo(
      start.x - ux * (roundTip * 0.65f), start.y - uy * (roundTip * 0.65f),
      start.x + nx * topRadius, start.y + ny * topRadius
    )
    close()
  }
  drawPath(sleevePath, fillColor)

  // Subtle rounded cuff hem contour
  val cuffHem = Path().apply {
    moveTo(cuffCenter.x + nx * (cuffRadius * 0.92f), cuffCenter.y + ny * (cuffRadius * 0.92f))
    quadraticTo(
      cuffCenter.x + ux * (roundTip * 0.85f), cuffCenter.y + uy * (roundTip * 0.85f),
      cuffCenter.x - nx * (cuffRadius * 0.92f), cuffCenter.y - ny * (cuffRadius * 0.92f)
    )
  }
  drawPath(
    path = cuffHem,
    color = shadeColor,
    style = Stroke(width = (roundTip * 0.45f).coerceAtLeast(1f), cap = StrokeCap.Round)
  )
}

/**
 * Draws natural tapered navy athletic shorts in side / 3/4 profile views:
 * - Curved glute/hip wrap, gradual mid-thigh taper, rounded leg cuff, and fabric drape crease
 * - Supports far-leg depth shading when [isFarSleeve] is true
 */
internal fun DrawScope.drawVectorShortsSide(
  hipCenter: Offset,
  kneeCenter: Offset,
  waistHalfDepth: Float,
  facingDir: Int = 1,
  isFarSleeve: Boolean = false
) {
  val fillColor = if (isFarSleeve) VectorAthletePalette.shortsDark else VectorAthletePalette.shortsNavy
  val shadeColor = if (isFarSleeve) Color(0xFF132033) else VectorAthletePalette.shortsDark

  val dx = kneeCenter.x - hipCenter.x
  val dy = kneeCenter.y - hipCenter.y
  val len = hypot(dx, dy).coerceAtLeast(8f)
  val ux = dx / len
  val uy = dy / len
  val nx = -uy
  val ny = ux

  val cuffCenter = lerpOffset(hipCenter, kneeCenter, 0.47f)
  val topCenter = Offset(
    hipCenter.x - ux * (waistHalfDepth * 0.36f),
    hipCenter.y - uy * (waistHalfDepth * 0.36f)
  )
  val midCenter = lerpOffset(topCenter, cuffCenter, 0.48f)

  val topR = waistHalfDepth * 1.06f
  val midR = waistHalfDepth * 0.98f
  val cuffR = waistHalfDepth * 0.80f
  val roundTip = waistHalfDepth * 0.22f

  val shortsPath = Path().apply {
    moveTo(topCenter.x + nx * topR, topCenter.y + ny * topR)
    quadraticTo(
      midCenter.x + nx * midR, midCenter.y + ny * midR,
      cuffCenter.x + nx * cuffR, cuffCenter.y + ny * cuffR
    )
    // Rounded mid-thigh leg opening
    quadraticTo(
      cuffCenter.x + ux * roundTip, cuffCenter.y + uy * roundTip,
      cuffCenter.x - nx * cuffR, cuffCenter.y - ny * cuffR
    )
    quadraticTo(
      midCenter.x - nx * midR, midCenter.y - ny * midR,
      topCenter.x - nx * topR, topCenter.y - ny * topR
    )
    // Curved waistband top contour
    quadraticTo(
      topCenter.x - ux * (roundTip * 0.7f), topCenter.y - uy * (roundTip * 0.7f),
      topCenter.x + nx * topR, topCenter.y + ny * topR
    )
    close()
  }
  drawPath(shortsPath, fillColor)

  if (!isFarSleeve) {
    // Subtle rounded cuff hem + diagonal fabric drape crease
    val cuffHem = Path().apply {
      moveTo(cuffCenter.x + nx * (cuffR * 0.92f), cuffCenter.y + ny * (cuffR * 0.92f))
      quadraticTo(
        cuffCenter.x + ux * (roundTip * 0.88f), cuffCenter.y + uy * (roundTip * 0.88f),
        cuffCenter.x - nx * (cuffR * 0.92f), cuffCenter.y - ny * (cuffR * 0.92f)
      )
    }
    drawPath(
      path = cuffHem,
      color = shadeColor,
      style = Stroke(width = (waistHalfDepth * 0.12f).coerceAtLeast(1f), cap = StrokeCap.Round)
    )

    val dir = if (facingDir == 0) 1f else facingDir.toFloat()
    val creaseStart = Offset(hipCenter.x - dir * waistHalfDepth * 0.15f, hipCenter.y + waistHalfDepth * 0.1f)
    val creaseEnd = Offset(cuffCenter.x + dir * waistHalfDepth * 0.18f, cuffCenter.y - waistHalfDepth * 0.15f)
    drawLine(
      color = shadeColor.copy(alpha = 0.75f),
      start = creaseStart,
      end = creaseEnd,
      strokeWidth = (waistHalfDepth * 0.09f).coerceAtLeast(0.9f),
      cap = StrokeCap.Round
    )
  }
}

// ============================================================================
// CLEAN FLAT-VECTOR GYM EQUIPMENT RENDERERS
// ============================================================================

internal fun DrawScope.drawVectorBench(
  padStart: Offset,
  padEnd: Offset,
  floorY: Float,
  unit: Float
) {
  val leg1Top = lerpOffset(padStart, padEnd, 0.18f)
  val leg2Top = lerpOffset(padStart, padEnd, 0.82f)
  drawLine(VectorAthletePalette.benchFrame, leg1Top, Offset(leg1Top.x, floorY), 3.6f * unit, StrokeCap.Round)
  drawLine(VectorAthletePalette.benchFrame, leg2Top, Offset(leg2Top.x, floorY), 3.6f * unit, StrokeCap.Round)
  drawLine(VectorAthletePalette.dumbbellDark, Offset(leg1Top.x - 6f * unit, floorY), Offset(leg1Top.x + 6f * unit, floorY), 3f * unit, StrokeCap.Round)
  drawLine(VectorAthletePalette.dumbbellDark, Offset(leg2Top.x - 6f * unit, floorY), Offset(leg2Top.x + 6f * unit, floorY), 3f * unit, StrokeCap.Round)

  drawLine(VectorAthletePalette.benchPad, padStart, padEnd, 8.5f * unit, StrokeCap.Round)
  drawLine(VectorAthletePalette.benchFrame, padStart, padEnd, 1.4f * unit, StrokeCap.Round)
}

internal fun DrawScope.drawVectorCableColumn(
  towerX: Float,
  topY: Float,
  floorY: Float,
  pulleyY: Float,
  cableTargets: List<Offset>,
  unit: Float
) {
  drawLine(VectorAthletePalette.benchFrame, Offset(towerX, topY), Offset(towerX, floorY), 5.5f * unit, StrokeCap.Round)
  drawLine(VectorAthletePalette.dumbbellDark, Offset(towerX - 8f * unit, floorY), Offset(towerX + 8f * unit, floorY), 3.5f * unit, StrokeCap.Round)

  val pulleyCenter = Offset(towerX, pulleyY)
  drawCircle(VectorAthletePalette.dumbbellBevel, 4.5f * unit, pulleyCenter)
  drawCircle(VectorAthletePalette.steelLight, 2.0f * unit, pulleyCenter)

  cableTargets.forEach { hand ->
    drawLine(
      color = VectorAthletePalette.steelBar,
      start = pulleyCenter,
      end = hand,
      strokeWidth = 1.8f * unit,
      cap = StrokeCap.Round
    )
  }
}

/**
 * Draws the chunky clean vector dumbbells matching the reference illustration
 * (dark slate double-disc ends with bevel highlight and chrome bar handle).
 */
internal fun DrawScope.drawVectorDumbbell(
  center: Offset,
  angleDeg: Float,
  unit: Float
) {
  val barHalf = 7.0f * unit
  val plateW = 4.2f * unit
  val plateH = 9.0f * unit

  rotate(degrees = angleDeg, pivot = center) {
    // Chrome bar behind fist
    drawLine(
      color = VectorAthletePalette.steelBar,
      start = Offset(center.x - barHalf - 1.5f * unit, center.y),
      end = Offset(center.x + barHalf + 1.5f * unit, center.y),
      strokeWidth = 2.6f * unit,
      cap = StrokeCap.Round
    )

    // Left & Right double-disc plates
    for (sign in listOf(-1f, 1f)) {
      val plateX = center.x + sign * barHalf - plateW / 2f
      drawRoundRect(
        color = VectorAthletePalette.dumbbellDark,
        topLeft = Offset(plateX, center.y - plateH / 2f),
        size = Size(plateW, plateH),
        cornerRadius = CornerRadius(1.6f * unit, 1.6f * unit)
      )
      drawRoundRect(
        color = VectorAthletePalette.dumbbellBevel,
        topLeft = Offset(plateX + (if (sign < 0) 0.5f else 1.4f) * unit, center.y - plateH / 2f + 1.0f * unit),
        size = Size(plateW * 0.45f, plateH - 2.0f * unit),
        cornerRadius = CornerRadius(1.0f * unit, 1.0f * unit)
      )
    }
  }
}

internal fun DrawScope.drawVectorBarbellFront(
  leftPlateCenter: Offset,
  rightPlateCenter: Offset,
  unit: Float
) {
  drawLine(
    color = VectorAthletePalette.steelBar,
    start = leftPlateCenter,
    end = rightPlateCenter,
    strokeWidth = 3.2f * unit,
    cap = StrokeCap.Round
  )
  for (pt in listOf(leftPlateCenter, rightPlateCenter)) {
    drawRoundRect(
      color = VectorAthletePalette.dumbbellDark,
      topLeft = Offset(pt.x - 3.4f * unit, pt.y - 10.5f * unit),
      size = Size(6.8f * unit, 21f * unit),
      cornerRadius = CornerRadius(2f * unit, 2f * unit)
    )
    drawLine(
      color = VectorAthletePalette.dumbbellBevel,
      start = Offset(pt.x, pt.y - 8.5f * unit),
      end = Offset(pt.x, pt.y + 8.5f * unit),
      strokeWidth = 2.0f * unit,
      cap = StrokeCap.Round
    )
  }
}

internal fun DrawScope.drawVectorBarbellSidePlate(
  center: Offset,
  unit: Float
) {
  drawCircle(VectorAthletePalette.dumbbellDark, 11.5f * unit, center)
  drawCircle(VectorAthletePalette.dumbbellBevel, 8.8f * unit, center, style = Stroke(2.0f * unit))
  drawCircle(VectorAthletePalette.steelBar, 3.6f * unit, center)
  drawCircle(VectorAthletePalette.dumbbellDark, 1.4f * unit, center)
}
