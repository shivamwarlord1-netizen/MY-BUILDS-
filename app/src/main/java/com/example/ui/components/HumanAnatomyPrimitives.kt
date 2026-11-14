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
  isBackView: Boolean = false,
  headAngleDeg: Float = 0f
) {
  rotate(degrees = headAngleDeg, pivot = center) {
    if (isBackView || facingDir == 0) {
      // Symmetrical ears behind head silhouette
      drawCircle(VectorAthletePalette.skinShadow, radius * 0.22f, Offset(center.x - radius * 0.93f, center.y + radius * 0.08f))
      drawCircle(VectorAthletePalette.skinBase, radius * 0.15f, Offset(center.x - radius * 0.91f, center.y + radius * 0.08f))
      drawCircle(VectorAthletePalette.skinShadow, radius * 0.22f, Offset(center.x + radius * 0.93f, center.y + radius * 0.08f))
      drawCircle(VectorAthletePalette.skinBase, radius * 0.15f, Offset(center.x + radius * 0.91f, center.y + radius * 0.08f))
    }

    // Smooth friendly head & jaw silhouette
    val headPath = Path().apply {
      if (isBackView || facingDir == 0) {
        moveTo(center.x, center.y - radius * 1.00f)
        cubicTo(
          center.x + radius * 1.00f, center.y - radius * 1.00f,
          center.x + radius * 0.96f, center.y + radius * 0.28f,
          center.x + radius * 0.54f, center.y + radius * 0.84f
        )
        cubicTo(
          center.x + radius * 0.22f, center.y + radius * 1.12f,
          center.x - radius * 0.22f, center.y + radius * 1.12f,
          center.x - radius * 0.54f, center.y + radius * 0.84f
        )
        cubicTo(
          center.x - radius * 0.96f, center.y + radius * 0.28f,
          center.x - radius * 1.00f, center.y - radius * 1.00f,
          center.x, center.y - radius * 1.00f
        )
      } else {
        val d = facingDir.toFloat()
        moveTo(center.x - d * radius * 0.10f, center.y - radius * 1.00f)
        cubicTo(
          center.x + d * radius * 0.90f, center.y - radius * 0.92f,
          center.x + d * radius * 1.02f, center.y - radius * 0.08f,
          center.x + d * radius * 0.90f, center.y + radius * 0.38f
        )
        cubicTo(
          center.x + d * radius * 0.80f, center.y + radius * 0.90f,
          center.x + d * radius * 0.34f, center.y + radius * 1.10f,
          center.x - d * radius * 0.22f, center.y + radius * 0.86f
        )
        cubicTo(
          center.x - d * radius * 0.68f, center.y + radius * 0.62f,
          center.x - d * radius * 1.02f, center.y - radius * 0.42f,
          center.x - d * radius * 0.10f, center.y - radius * 1.00f
        )
      }
      close()
    }
    drawPath(headPath, VectorAthletePalette.skinBase)

    if (isBackView) {
      // Tapered posterior hair coverage leaving clean nape skin above neck
      val backHair = Path().apply {
        moveTo(center.x - radius * 0.95f, center.y + radius * 0.14f)
        cubicTo(
          center.x - radius * 1.08f, center.y - radius * 1.22f,
          center.x + radius * 1.08f, center.y - radius * 1.22f,
          center.x + radius * 0.95f, center.y + radius * 0.14f
        )
        cubicTo(
          center.x + radius * 0.82f, center.y + radius * 0.42f,
          center.x + radius * 0.62f, center.y + radius * 0.56f,
          center.x + radius * 0.44f, center.y + radius * 0.60f
        )
        quadraticTo(
          center.x, center.y + radius * 0.54f,
          center.x - radius * 0.44f, center.y + radius * 0.60f
        )
        cubicTo(
          center.x - radius * 0.62f, center.y + radius * 0.56f,
          center.x - radius * 0.82f, center.y + radius * 0.42f,
          center.x - radius * 0.95f, center.y + radius * 0.14f
        )
        close()
      }
      drawPath(backHair, VectorAthletePalette.hairDark)

      val crownSheen = Path().apply {
        moveTo(center.x - radius * 0.46f, center.y - radius * 0.78f)
        quadraticTo(center.x, center.y - radius * 0.94f, center.x + radius * 0.46f, center.y - radius * 0.78f)
      }
      drawPath(
        path = crownSheen,
        color = VectorAthletePalette.hairHighlight,
        style = Stroke(width = (radius * 0.11f).coerceAtLeast(1.1f), cap = StrokeCap.Round)
      )
    } else if (facingDir == 0) {
      // Natural sculpted side-swept dark hair hugging temples & ears
      val hairPath = Path().apply {
        moveTo(center.x - radius * 0.92f, center.y - radius * 0.04f)
        cubicTo(
          center.x - radius * 1.08f, center.y - radius * 0.82f,
          center.x - radius * 0.48f, center.y - radius * 1.26f,
          center.x + radius * 0.10f, center.y - radius * 1.24f
        )
        cubicTo(
          center.x + radius * 0.72f, center.y - radius * 1.20f,
          center.x + radius * 1.06f, center.y - radius * 0.74f,
          center.x + radius * 0.92f, center.y - radius * 0.04f
        )
        // Right sideburn tapering cleanly to forehead hairline
        cubicTo(
          center.x + radius * 0.86f, center.y - radius * 0.24f,
          center.x + radius * 0.80f, center.y - radius * 0.40f,
          center.x + radius * 0.62f, center.y - radius * 0.48f
        )
        // Smooth asymmetric swept fringe across forehead
        cubicTo(
          center.x + radius * 0.30f, center.y - radius * 0.62f,
          center.x - radius * 0.22f, center.y - radius * 0.60f,
          center.x - radius * 0.62f, center.y - radius * 0.44f
        )
        // Left temple sideburn
        cubicTo(
          center.x - radius * 0.80f, center.y - radius * 0.36f,
          center.x - radius * 0.86f, center.y - radius * 0.22f,
          center.x - radius * 0.92f, center.y - radius * 0.04f
        )
        close()
      }
      drawPath(hairPath, VectorAthletePalette.hairDark)

      val crownSheen = Path().apply {
        moveTo(center.x - radius * 0.42f, center.y - radius * 0.92f)
        quadraticTo(center.x + radius * 0.06f, center.y - radius * 1.04f, center.x + radius * 0.46f, center.y - radius * 0.88f)
      }
      drawPath(
        path = crownSheen,
        color = VectorAthletePalette.hairHighlight,
        style = Stroke(width = (radius * 0.11f).coerceAtLeast(1.1f), cap = StrokeCap.Round)
      )

      // Friendly eyebrows, expressive dark eyes, gentle nose, calm smile
      val eyeY = center.y + radius * 0.02f
      val eyeOffsetX = radius * 0.34f
      val browY = center.y - radius * 0.18f

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

      drawCircle(VectorAthletePalette.eyeDark, radius * 0.095f, Offset(center.x - eyeOffsetX, eyeY))
      drawCircle(VectorAthletePalette.eyeDark, radius * 0.095f, Offset(center.x + eyeOffsetX, eyeY))

      drawLine(
        color = VectorAthletePalette.skinShadow,
        start = Offset(center.x, eyeY + radius * 0.06f),
        end = Offset(center.x + radius * 0.05f, eyeY + radius * 0.28f),
        strokeWidth = (radius * 0.08f).coerceAtLeast(1f),
        cap = StrokeCap.Round
      )

      val smilePath = Path().apply {
        moveTo(center.x - radius * 0.22f, center.y + radius * 0.50f)
        quadraticTo(
          center.x, center.y + radius * 0.65f,
          center.x + radius * 0.22f, center.y + radius * 0.50f
        )
      }
      drawPath(
        path = smilePath,
        color = VectorAthletePalette.mouthTone,
        style = Stroke(width = (radius * 0.09f).coerceAtLeast(1.1f), cap = StrokeCap.Round)
      )
    } else {
      // Side-profile hair wrapping cleanly from forehead quiff down to tapered nape
      val d = facingDir.toFloat()
      val sideHair = Path().apply {
        moveTo(center.x + d * radius * 0.78f, center.y - radius * 0.44f)
        cubicTo(
          center.x + d * radius * 0.88f, center.y - radius * 1.06f,
          center.x + d * radius * 0.14f, center.y - radius * 1.25f,
          center.x - d * radius * 0.56f, center.y - radius * 1.08f
        )
        cubicTo(
          center.x - d * radius * 1.06f, center.y - radius * 0.86f,
          center.x - d * radius * 1.00f, center.y + radius * 0.14f,
          center.x - d * radius * 0.66f, center.y + radius * 0.58f
        )
        // Smooth curve along nape up behind the ear
        cubicTo(
          center.x - d * radius * 0.46f, center.y + radius * 0.54f,
          center.x - d * radius * 0.36f, center.y + radius * 0.24f,
          center.x - d * radius * 0.32f, center.y - radius * 0.04f
        )
        // Tidy sideburn in front of the ear
        cubicTo(
          center.x - d * radius * 0.18f, center.y - radius * 0.06f,
          center.x - d * radius * 0.12f, center.y + radius * 0.08f,
          center.x - d * radius * 0.10f, center.y - radius * 0.20f
        )
        // Curved temple hairline back to forehead
        cubicTo(
          center.x - d * radius * 0.02f, center.y - radius * 0.48f,
          center.x + d * radius * 0.38f, center.y - radius * 0.50f,
          center.x + d * radius * 0.78f, center.y - radius * 0.44f
        )
        close()
      }
      drawPath(sideHair, VectorAthletePalette.hairDark)

      val crownSheen = Path().apply {
        moveTo(center.x - d * radius * 0.42f, center.y - radius * 0.84f)
        quadraticTo(
          center.x + d * radius * 0.08f, center.y - radius * 0.94f,
          center.x + d * radius * 0.46f, center.y - radius * 0.76f
        )
      }
      drawPath(
        path = crownSheen,
        color = VectorAthletePalette.hairHighlight,
        style = Stroke(width = (radius * 0.11f).coerceAtLeast(1.1f), cap = StrokeCap.Round)
      )

      // Ear placed cleanly at the sideburn/nape recess
      val earCenter = Offset(center.x - d * radius * 0.24f, center.y + radius * 0.08f)
      drawCircle(VectorAthletePalette.skinBase, radius * 0.21f, earCenter)
      drawCircle(VectorAthletePalette.skinShadow, radius * 0.11f, earCenter)

      // Side profile brow, eye & calm smile
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
}

/**
 * Draws the front upper body wearing the light sky-blue sleeveless athletic tank top,
 * with athletic traps, rounded deltoid caps, subtle pec contour, and V-taper waist.
 * Supports shifting shoulders horizontally relative to [hipCenterX] for rotational lifts.
 */
internal fun DrawScope.drawVectorFrontTankTorso(
  neckTop: Offset,
  leftShoulder: Offset,
  rightShoulder: Offset,
  waistY: Float,
  waistHalfWidth: Float,
  hipY: Float,
  hipHalfWidth: Float,
  hipCenterX: Float = neckTop.x
) {
  val neckX = neckTop.x
  val shoulderSpan = (rightShoulder.x - leftShoulder.x).coerceAtLeast(10f)
  val neckHalfW = shoulderSpan * 0.165f
  val shoulderMidY = (leftShoulder.y + rightShoulder.y) * 0.5f
  val neckBaseY = shoulderMidY - shoulderSpan * 0.08f
  val strapOuterX = shoulderSpan * 0.37f
  val strapInnerX = shoulderSpan * 0.21f
  val chestMidY = shoulderMidY + shoulderSpan * 0.23f
  val waistX = lerpFloat(neckX, hipCenterX, 0.52f)

  // 1. Athletic tan neck, fuller traps & rounded deltoid caps beneath tank straps
  val skinPath = Path().apply {
    moveTo(neckX - neckHalfW, neckTop.y)
    lineTo(neckX + neckHalfW, neckTop.y)
    cubicTo(
      neckX + neckHalfW * 1.18f, neckBaseY,
      rightShoulder.x - shoulderSpan * 0.12f, rightShoulder.y - shoulderSpan * 0.08f,
      rightShoulder.x, rightShoulder.y
    )
    lineTo(neckX + strapInnerX, chestMidY)
    lineTo(neckX - strapInnerX, chestMidY)
    lineTo(leftShoulder.x, leftShoulder.y)
    cubicTo(
      leftShoulder.x + shoulderSpan * 0.12f, leftShoulder.y - shoulderSpan * 0.08f,
      neckX - neckHalfW * 1.18f, neckBaseY,
      neckX - neckHalfW, neckTop.y
    )
    close()
  }
  drawPath(skinPath, VectorAthletePalette.skinBase)
  val deltRadius = shoulderSpan * 0.128f
  drawCircle(VectorAthletePalette.skinBase, deltRadius, leftShoulder)
  drawCircle(VectorAthletePalette.skinBase, deltRadius, rightShoulder)
  // Subtle lateral deltoid cap contour
  drawCircle(
    color = VectorAthletePalette.skinShadow.copy(alpha = 0.38f),
    radius = deltRadius * 0.86f,
    center = leftShoulder,
    style = Stroke(width = (shoulderSpan * 0.022f).coerceAtLeast(0.8f))
  )
  drawCircle(
    color = VectorAthletePalette.skinShadow.copy(alpha = 0.38f),
    radius = deltRadius * 0.86f,
    center = rightShoulder,
    style = Stroke(width = (shoulderSpan * 0.022f).coerceAtLeast(0.8f))
  )

  // Subtle under-chin neck shadow (head draws on top cleanly)
  drawOval(
    color = VectorAthletePalette.skinShadow.copy(alpha = 0.50f),
    topLeft = Offset(neckX - neckHalfW * 0.90f, neckTop.y + neckHalfW * 0.10f),
    size = Size(neckHalfW * 1.80f, neckHalfW * 0.85f)
  )

  // 2. Light Sky-Blue Sleeveless Athletic Tank Top with muscular chest/lat V-taper
  val armHoleY = shoulderMidY + shoulderSpan * 0.27f
  val tankBottomY = hipY + (hipY - waistY) * 0.16f
  val chestLatHalfW = (waistHalfWidth * 1.24f).coerceAtLeast(hipHalfWidth * 1.04f)
  val midWaistHalfW = waistHalfWidth * 1.04f

  val tankPath = Path().apply {
    moveTo(neckX - strapOuterX, leftShoulder.y)
    lineTo(neckX - strapInnerX, leftShoulder.y)
    cubicTo(
      neckX - strapInnerX * 0.6f, chestMidY,
      neckX + strapInnerX * 0.6f, chestMidY,
      neckX + strapInnerX, rightShoulder.y
    )
    lineTo(neckX + strapOuterX, rightShoulder.y)
    cubicTo(
      neckX + strapOuterX + shoulderSpan * 0.03f, armHoleY * 0.45f + rightShoulder.y * 0.55f,
      waistX + chestLatHalfW * 1.06f, armHoleY - shoulderSpan * 0.06f,
      waistX + chestLatHalfW, armHoleY
    )
    cubicTo(
      waistX + midWaistHalfW, lerpFloat(armHoleY, tankBottomY, 0.55f),
      hipCenterX + hipHalfWidth * 1.03f, tankBottomY - shoulderSpan * 0.05f,
      hipCenterX + hipHalfWidth * 1.03f, tankBottomY
    )
    quadraticTo(
      hipCenterX, tankBottomY + shoulderSpan * 0.05f,
      hipCenterX - hipHalfWidth * 1.03f, tankBottomY
    )
    cubicTo(
      hipCenterX - hipHalfWidth * 1.03f, tankBottomY - shoulderSpan * 0.05f,
      waistX - midWaistHalfW, lerpFloat(armHoleY, tankBottomY, 0.55f),
      waistX - chestLatHalfW, armHoleY
    )
    cubicTo(
      waistX - chestLatHalfW * 1.06f, armHoleY - shoulderSpan * 0.06f,
      neckX - strapOuterX - shoulderSpan * 0.03f, armHoleY * 0.45f + leftShoulder.y * 0.55f,
      neckX - strapOuterX, leftShoulder.y
    )
    close()
  }
  drawPath(tankPath, VectorAthletePalette.tankBlue)

  // Scoop collar trim & subtle pectoral separation contour
  drawPath(
    path = Path().apply {
      moveTo(neckX - strapInnerX, leftShoulder.y + shoulderSpan * 0.02f)
      cubicTo(
        neckX - strapInnerX * 0.6f, chestMidY,
        neckX + strapInnerX * 0.6f, chestMidY,
        neckX + strapInnerX, rightShoulder.y + shoulderSpan * 0.02f
      )
    },
    color = VectorAthletePalette.tankFold,
    style = Stroke(width = (shoulderSpan * 0.045f).coerceAtLeast(1.2f), cap = StrokeCap.Round)
  )
  val pecY = armHoleY + shoulderSpan * 0.04f
  drawPath(
    path = Path().apply {
      moveTo(waistX - chestLatHalfW * 0.72f, pecY - shoulderSpan * 0.02f)
      quadraticTo(waistX - chestLatHalfW * 0.34f, pecY + shoulderSpan * 0.05f, waistX - shoulderSpan * 0.02f, pecY)
      moveTo(waistX + shoulderSpan * 0.02f, pecY)
      quadraticTo(waistX + chestLatHalfW * 0.34f, pecY + shoulderSpan * 0.05f, waistX + chestLatHalfW * 0.72f, pecY - shoulderSpan * 0.02f)
    },
    color = VectorAthletePalette.tankFold.copy(alpha = 0.65f),
    style = Stroke(width = (shoulderSpan * 0.030f).coerceAtLeast(1f), cap = StrokeCap.Round)
  )
  drawLine(
    color = VectorAthletePalette.tankFold,
    start = Offset(hipCenterX - hipHalfWidth * 0.95f, tankBottomY - shoulderSpan * 0.03f),
    end = Offset(hipCenterX + hipHalfWidth * 0.95f, tankBottomY - shoulderSpan * 0.03f),
    strokeWidth = (shoulderSpan * 0.035f).coerceAtLeast(1f),
    cap = StrokeCap.Round
  )
}

/**
 * Draws the posterior (back) upper body wearing the sky-blue athletic tank top (used for Lat Pulldown),
 * featuring athletic traps, broader lat sweep, and subtle upper-back contour.
 */
internal fun DrawScope.drawVectorBackTankTorso(
  neckTop: Offset,
  leftShoulder: Offset,
  rightShoulder: Offset,
  waistY: Float,
  waistHalfWidth: Float,
  hipY: Float,
  hipHalfWidth: Float,
  hipCenterX: Float = neckTop.x
) {
  val neckX = neckTop.x
  val shoulderSpan = (rightShoulder.x - leftShoulder.x).coerceAtLeast(10f)
  val neckHalfW = shoulderSpan * 0.16f
  val shoulderMidY = (leftShoulder.y + rightShoulder.y) * 0.5f
  val neckBaseY = shoulderMidY - shoulderSpan * 0.08f
  val strapOuterX = shoulderSpan * 0.36f
  val strapInnerX = shoulderSpan * 0.18f
  val waistX = lerpFloat(neckX, hipCenterX, 0.52f)

  // Upper back / shoulder skin visible around racerback tank
  val skinPath = Path().apply {
    moveTo(neckX - neckHalfW, neckTop.y)
    lineTo(neckX + neckHalfW, neckTop.y)
    cubicTo(
      neckX + neckHalfW * 1.20f, neckBaseY,
      rightShoulder.x - shoulderSpan * 0.10f, rightShoulder.y - shoulderSpan * 0.08f,
      rightShoulder.x, rightShoulder.y
    )
    lineTo(waistX + waistHalfWidth * 1.12f, waistY)
    lineTo(waistX - waistHalfWidth * 1.12f, waistY)
    lineTo(leftShoulder.x, leftShoulder.y)
    cubicTo(
      leftShoulder.x + shoulderSpan * 0.10f, leftShoulder.y - shoulderSpan * 0.08f,
      neckX - neckHalfW * 1.20f, neckBaseY,
      neckX - neckHalfW, neckTop.y
    )
    close()
  }
  drawPath(skinPath, VectorAthletePalette.skinBase)
  drawCircle(VectorAthletePalette.skinBase, shoulderSpan * 0.125f, leftShoulder)
  drawCircle(VectorAthletePalette.skinBase, shoulderSpan * 0.125f, rightShoulder)

  // Sky-blue tank top back coverage with muscular V-taper lats
  val armHoleY = shoulderMidY + shoulderSpan * 0.25f
  val tankBottomY = hipY + (hipY - waistY) * 0.15f
  val latHalfW = (waistHalfWidth * 1.26f).coerceAtLeast(hipHalfWidth * 1.04f)
  val tankPath = Path().apply {
    moveTo(neckX - strapOuterX, leftShoulder.y)
    lineTo(neckX - strapInnerX, leftShoulder.y)
    quadraticTo(neckX, shoulderMidY + shoulderSpan * 0.10f, neckX + strapInnerX, rightShoulder.y)
    lineTo(neckX + strapOuterX, rightShoulder.y)
    cubicTo(
      neckX + strapOuterX + shoulderSpan * 0.02f, armHoleY * 0.55f + rightShoulder.y * 0.45f,
      waistX + latHalfW * 1.05f, armHoleY - shoulderSpan * 0.03f,
      waistX + latHalfW, armHoleY + shoulderSpan * 0.05f
    )
    lineTo(hipCenterX + hipHalfWidth * 1.03f, tankBottomY)
    lineTo(hipCenterX - hipHalfWidth * 1.03f, tankBottomY)
    lineTo(waistX - latHalfW, armHoleY + shoulderSpan * 0.05f)
    cubicTo(
      waistX - latHalfW * 1.05f, armHoleY - shoulderSpan * 0.03f,
      neckX - strapOuterX - shoulderSpan * 0.02f, armHoleY * 0.55f + leftShoulder.y * 0.45f,
      neckX - strapOuterX, leftShoulder.y
    )
    close()
  }
  drawPath(tankPath, VectorAthletePalette.tankBlue)
  drawLine(
    color = VectorAthletePalette.tankFold,
    start = Offset(waistX, shoulderMidY + shoulderSpan * 0.18f),
    end = Offset(hipCenterX, tankBottomY - shoulderSpan * 0.08f),
    strokeWidth = (shoulderSpan * 0.035f).coerceAtLeast(1f),
    cap = StrokeCap.Round
  )
  // Subtle shoulder blade V-contour
  drawPath(
    path = Path().apply {
      moveTo(waistX - latHalfW * 0.58f, armHoleY + shoulderSpan * 0.02f)
      lineTo(waistX, armHoleY + shoulderSpan * 0.18f)
      lineTo(waistX + latHalfW * 0.58f, armHoleY + shoulderSpan * 0.02f)
    },
    color = VectorAthletePalette.tankFold.copy(alpha = 0.55f),
    style = Stroke(width = (shoulderSpan * 0.028f).coerceAtLeast(0.9f), cap = StrokeCap.Round, join = StrokeJoin.Round)
  )
}

/**
 * Draws the side/3/4 profile upper body wearing the sky-blue sleeveless tank top with muscular chest,
 * upper back sweep, rounded deltoid cap, and neck connected directly into [headCenter].
 */
internal fun DrawScope.drawVectorSideTankTorso(
  shoulder: Offset,
  hip: Offset,
  chestDepth: Float,
  facingDir: Int,
  headCenter: Offset? = null
) {
  val dx = hip.x - shoulder.x
  val dy = hip.y - shoulder.y
  val len = hypot(dx, dy).coerceAtLeast(1f)
  val ux = dx / len
  val uy = dy / len
  val fx = uy * facingDir
  val fy = -ux * facingDir

  // Warm tan muscular neck connected seamlessly toward headCenter + fuller rounded deltoid cap
  val neckBase = Offset(
    shoulder.x - ux * chestDepth * 0.12f,
    shoulder.y - uy * chestDepth * 0.12f
  )
  val neckEnd = if (headCenter != null) {
    lerpOffset(neckBase, headCenter, 0.88f)
  } else {
    Offset(shoulder.x - ux * chestDepth * 0.72f, shoulder.y - uy * chestDepth * 0.72f)
  }
  drawVectorSegment(
    start = neckBase,
    end = neckEnd,
    startRadius = chestDepth * 0.30f,
    bellyRadius = chestDepth * 0.27f,
    endRadius = chestDepth * 0.24f,
    isFarLimb = false,
    bellyBias = 0.45f
  )
  val deltRadius = chestDepth * 0.41f
  drawCircle(VectorAthletePalette.skinBase, deltRadius, shoulder)
  drawCircle(
    color = VectorAthletePalette.skinShadow.copy(alpha = 0.36f),
    radius = deltRadius * 0.86f,
    center = shoulder,
    style = Stroke(width = (chestDepth * 0.055f).coerceAtLeast(0.9f))
  )

  // Sky-blue tank top body wrapping fuller chest -> tapered waist -> hip hem
  val topFront = Offset(
    shoulder.x + ux * (len * 0.10f) + fx * (chestDepth * 0.50f),
    shoulder.y + uy * (len * 0.10f) + fy * (chestDepth * 0.50f)
  )
  val midChest = Offset(
    shoulder.x + ux * (len * 0.32f) + fx * (chestDepth * 0.77f),
    shoulder.y + uy * (len * 0.32f) + fy * (chestDepth * 0.77f)
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
    shoulder.x + ux * (len * 0.34f) - fx * (chestDepth * 0.62f),
    shoulder.y + uy * (len * 0.34f) - fy * (chestDepth * 0.62f)
  )
  val topBack = Offset(
    shoulder.x - ux * (len * 0.02f) - fx * (chestDepth * 0.38f),
    shoulder.y - uy * (len * 0.02f) - fy * (chestDepth * 0.38f)
  )

  val tankPath = Path().apply {
    moveTo(topFront.x, topFront.y)
    cubicTo(midChest.x, midChest.y, midChest.x, midChest.y, hipFront.x, hipFront.y)
    lineTo(hipBack.x, hipBack.y)
    cubicTo(midBack.x, midBack.y, midBack.x, midBack.y, topBack.x, topBack.y)
    quadraticTo(
      shoulder.x + ux * (len * 0.06f) + fx * (chestDepth * 0.08f),
      shoulder.y + uy * (len * 0.06f) + fy * (chestDepth * 0.08f),
      topFront.x,
      topFront.y
    )
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
 * Draws a muscular flat-vector human arm (rounded shoulder, defined bicep & forearm, articulated gripping hand).
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
    startRadius = 4.9f * unit,
    bellyRadius = 5.5f * unit,
    endRadius = 4.0f * unit,
    isFarLimb = isFarArm,
    bellyBias = 0.44f
  )

  drawVectorSegment(
    start = elbow,
    end = wrist,
    startRadius = 4.0f * unit,
    bellyRadius = 4.3f * unit,
    endRadius = 2.8f * unit,
    isFarLimb = isFarArm,
    bellyBias = 0.26f
  )

  // Subtle upper-arm bicep/tricep definition contour on near arm
  if (!isFarArm && hypot(elbow.x - shoulder.x, elbow.y - shoulder.y) > 8f * unit) {
    val cutStart = lerpOffset(shoulder, elbow, 0.28f)
    val cutEnd = lerpOffset(shoulder, elbow, 0.72f)
    drawLine(
      color = VectorAthletePalette.skinShadow.copy(alpha = 0.42f),
      start = cutStart,
      end = cutEnd,
      strokeWidth = (0.9f * unit).coerceAtLeast(0.8f),
      cap = StrokeCap.Round
    )
  }

  // Articulated rounded hand & folded thumb
  val angleRad = atan2(wrist.y - elbow.y, wrist.x - elbow.x)
  val handCenter = Offset(
    x = wrist.x + cos(angleRad) * 1.4f * unit,
    y = wrist.y + sin(angleRad) * 1.4f * unit
  )
  drawCircle(skinFill, 3.5f * unit, handCenter)
  drawCircle(handShadow, 3.5f * unit, handCenter, style = Stroke(0.8f * unit))
}

/**
 * Draws a muscular flat-vector leg (athletic quad sweep, defined calf, white crew sock, and sneaker).
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
    startRadius = 6.6f * unit,
    bellyRadius = 7.2f * unit,
    endRadius = 4.9f * unit,
    isFarLimb = isFarLeg,
    bellyBias = 0.44f
  )

  drawVectorSegment(
    start = knee,
    end = ankle,
    startRadius = 4.8f * unit,
    bellyRadius = 5.5f * unit,
    endRadius = 3.1f * unit,
    isFarLimb = isFarLeg,
    bellyBias = 0.30f
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
