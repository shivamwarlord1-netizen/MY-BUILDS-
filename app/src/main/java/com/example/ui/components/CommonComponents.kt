package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BioCyan
import com.example.ui.theme.BioCyanTrack
import com.example.ui.theme.DuskIndigo
import com.example.ui.theme.DuskIndigoTrack
import com.example.ui.theme.KineticViolet
import com.example.ui.theme.KineticVioletTrack
import com.example.ui.theme.KinetiqBlack
import com.example.ui.theme.KinetiqBorderGlow
import com.example.ui.theme.KinetiqBorderSubtle
import com.example.ui.theme.KinetiqBrandTitleStyle
import com.example.ui.theme.KinetiqCardSurface
import com.example.ui.theme.KinetiqElevatedCard
import com.example.ui.theme.KinetiqObsidianSurface
import com.example.ui.theme.KinetiqPurpleLavender
import com.example.ui.theme.KinetiqPurpleNeon
import com.example.ui.theme.KinetiqPurplePrimary
import com.example.ui.theme.KinetiqTextMuted
import com.example.ui.theme.KinetiqTextSubtle
import com.example.ui.theme.KinetiqTextWhite
import com.example.ui.theme.RadiantAmber
import com.example.ui.theme.RadiantAmberTrack
import com.example.ui.viewmodel.Screen

@Composable
fun AuraTopBar(
  dateText: String,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 14.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      modifier = Modifier.weight(1f),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Official KinetiQ Emblem
      KinetiQLogo(size = 44.dp)

      Spacer(modifier = Modifier.width(12.dp))

      Text(
        text = "KinetiQ",
        style = KinetiqBrandTitleStyle,
        maxLines = 1,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.testTag("header_brand_title")
      )
    }

    Surface(
      shape = RoundedCornerShape(12.dp),
      color = MaterialTheme.colorScheme.surface,
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
    ) {
      Text(
        text = dateText,
        style = MaterialTheme.typography.titleSmall.copy(
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 0.2.sp
        ),
        maxLines = 1,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
          .padding(horizontal = 13.dp, vertical = 7.dp)
          .testTag("header_date_text")
      )
    }
  }
}

@Composable
fun AuraBottomNavigation(
  currentScreen: Screen,
  onSelectScreen: (Screen) -> Unit,
  modifier: Modifier = Modifier
) {
  val colorScheme = MaterialTheme.colorScheme
  val navColors = NavigationBarItemDefaults.colors(
    selectedIconColor = colorScheme.onSurface,
    selectedTextColor = colorScheme.primary,
    indicatorColor = colorScheme.primary.copy(alpha = 0.22f),
    unselectedIconColor = colorScheme.onSurfaceVariant,
    unselectedTextColor = colorScheme.onSurfaceVariant
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
          colors = listOf(colorScheme.outline.copy(alpha = 0.5f), Color.Transparent)
        ),
        shape = androidx.compose.ui.graphics.RectangleShape
      )
  ) {
    NavigationBar(
      modifier = Modifier.fillMaxWidth(),
      containerColor = colorScheme.background,
      tonalElevation = 0.dp
    ) {
      NavigationBarItem(
        selected = currentScreen == Screen.TODAY,
        onClick = { onSelectScreen(Screen.TODAY) },
        icon = {
          Icon(
            imageVector = if (currentScreen == Screen.TODAY) Icons.Filled.Today else Icons.Outlined.Today,
            contentDescription = "Today Dashboard"
          )
        },
        label = { Text("Today", fontSize = 11.sp) },
        colors = navColors,
        modifier = Modifier.testTag("nav_today")
      )

      NavigationBarItem(
        selected = currentScreen == Screen.INSIGHTS,
        onClick = { onSelectScreen(Screen.INSIGHTS) },
        icon = {
          Icon(
            imageVector = if (currentScreen == Screen.INSIGHTS) Icons.Filled.PieChart else Icons.Outlined.PieChart,
            contentDescription = "Insights"
          )
        },
        label = { Text("Insights", fontSize = 11.sp) },
        colors = navColors,
        modifier = Modifier.testTag("nav_insights")
      )

      NavigationBarItem(
        selected = currentScreen == Screen.EXERCISES,
        onClick = { onSelectScreen(Screen.EXERCISES) },
        icon = {
          Icon(
            imageVector = if (currentScreen == Screen.EXERCISES) Icons.Filled.FitnessCenter else Icons.Outlined.FitnessCenter,
            contentDescription = "Exercises"
          )
        },
        label = { Text("Exercises", fontSize = 11.sp) },
        colors = navColors,
        modifier = Modifier.testTag("nav_exercises")
      )

      NavigationBarItem(
        selected = currentScreen == Screen.SETTINGS,
        onClick = { onSelectScreen(Screen.SETTINGS) },
        icon = {
          Icon(
            imageVector = if (currentScreen == Screen.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
            contentDescription = "Settings"
          )
        },
        label = { Text("Settings", fontSize = 11.sp) },
        colors = navColors,
        modifier = Modifier.testTag("nav_settings")
      )
    }
  }
}

@Composable
fun HolisticWellnessRing(
  stepProgress: Float,
  waterProgress: Float,
  sleepProgress: Float,
  habitProgress: Float,
  modifier: Modifier = Modifier
) {
  // Smooth animated arcs on value transitions
  val animSteps by animateFloatAsState(
    targetValue = stepProgress.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
    label = "anim_steps_ring"
  )
  val animWater by animateFloatAsState(
    targetValue = waterProgress.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 1100, delayMillis = 100, easing = FastOutSlowInEasing),
    label = "anim_water_ring"
  )
  val animSleep by animateFloatAsState(
    targetValue = sleepProgress.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 1100, delayMillis = 200, easing = FastOutSlowInEasing),
    label = "anim_sleep_ring"
  )
  val animHabits by animateFloatAsState(
    targetValue = habitProgress.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 1100, delayMillis = 300, easing = FastOutSlowInEasing),
    label = "anim_habit_ring"
  )

  val breathingScale = rememberBreathingScale(minScale = 0.97f, maxScale = 1.03f, durationMillis = 2800)

  Card(
    modifier = modifier
      .fillMaxWidth()
      .border(
        width = 1.dp,
        brush = Brush.linearGradient(
          colors = listOf(KinetiqBorderGlow.copy(alpha = 0.45f), KinetiqBorderSubtle.copy(alpha = 0.3f))
        ),
        shape = RoundedCornerShape(26.dp)
      ),
    shape = RoundedCornerShape(26.dp),
    colors = CardDefaults.cardColors(containerColor = KinetiqCardSurface),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Column(
      modifier = Modifier.padding(22.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "KINETIC EQUILIBRIUM",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.5.sp
            ),
            color = KinetiqPurpleNeon
          )
          Text(
            text = "Holistic Daily Load",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = KinetiqTextWhite
          )
        }

        Box(
          modifier = Modifier
            .background(
              color = KinetiqObsidianSurface,
              shape = RoundedCornerShape(10.dp)
            )
            .border(0.8.dp, KinetiqBorderSubtle, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
          Text(
            text = "4 PILLARS",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              fontSize = 10.sp
            ),
            color = KinetiqPurpleLavender
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(195.dp)
      ) {
        Canvas(modifier = Modifier.size(176.dp)) {
          val strokeWidth = 11.5.dp.toPx()

          // Track 1 (Outer): Kinetic Movement
          val r1 = (size.minDimension / 2) - strokeWidth
          drawCircle(
            color = KineticVioletTrack,
            radius = r1,
            style = Stroke(strokeWidth)
          )
          drawArc(
            color = KineticViolet,
            startAngle = -90f,
            sweepAngle = animSteps * 360f,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
          )

          // Track 2: Bio Hydration
          val r2 = r1 - strokeWidth - 5.dp.toPx()
          drawCircle(
            color = BioCyanTrack,
            radius = r2,
            style = Stroke(strokeWidth)
          )
          drawArc(
            color = BioCyan,
            startAngle = -90f,
            sweepAngle = animWater * 360f,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
          )

          // Track 3: Restorative Sleep
          val r3 = r2 - strokeWidth - 5.dp.toPx()
          drawCircle(
            color = DuskIndigoTrack,
            radius = r3,
            style = Stroke(strokeWidth)
          )
          drawArc(
            color = DuskIndigo,
            startAngle = -90f,
            sweepAngle = animSleep * 360f,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
          )

          // Track 4 (Inner): Mindful Habits
          val r4 = r3 - strokeWidth - 5.dp.toPx()
          drawCircle(
            color = RadiantAmberTrack,
            radius = r4,
            style = Stroke(strokeWidth)
          )
          drawArc(
            color = RadiantAmber,
            startAngle = -90f,
            sweepAngle = animHabits * 360f,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
          )
        }

        // Center Score / Status with gentle organic breathing motion
        val avgScore = (((animSteps + animWater + animSleep + animHabits) / 4f) * 100).toInt()
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.scale(breathingScale)
        ) {
          Text(
            text = "$avgScore%",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Black,
              fontSize = 28.sp,
              letterSpacing = (-0.5).sp
            ),
            color = KinetiqTextWhite
          )
          Text(
            text = "OPTIMAL",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.2.sp,
              fontSize = 9.sp
            ),
            color = KinetiqPurpleNeon
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Legend in sleek dark capsule
      Surface(
        color = KinetiqObsidianSurface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, KinetiqBorderSubtle),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          LegendChip("Kinetic", KineticViolet)
          LegendChip("Hydration", BioCyan)
          LegendChip("Sleep", DuskIndigo)
          LegendChip("Habits", RadiantAmber)
        }
      }
    }
  }
}

@Composable
private fun LegendChip(label: String, color: Color) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .background(color, CircleShape)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
      color = KinetiqTextMuted
    )
  }
}

@Composable
fun MedicalDisclaimerBanner(modifier: Modifier = Modifier) {
  val colorScheme = MaterialTheme.colorScheme
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .border(0.8.dp, colorScheme.outline.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
    shape = RoundedCornerShape(14.dp),
    color = colorScheme.surface
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Info,
        contentDescription = null,
        tint = colorScheme.primary,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        text = "KinetiQ is a personal lifestyle wellness companion. It does not provide medical diagnoses or prescriptions.",
        style = MaterialTheme.typography.labelSmall,
        color = colorScheme.onSurfaceVariant,
        lineHeight = 15.sp
      )
    }
  }
}

@Composable
fun BannerToast(
  message: String?,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colorScheme = MaterialTheme.colorScheme
  AnimatedVisibility(
    visible = message != null,
    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
    modifier = modifier
  ) {
    if (message != null) {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp)
          .border(
            width = 1.dp,
            brush = Brush.linearGradient(
              colors = listOf(colorScheme.primary, colorScheme.secondary)
            ),
            shape = RoundedCornerShape(16.dp)
          ),
        shape = RoundedCornerShape(16.dp),
        color = colorScheme.surfaceVariant,
        shadowElevation = 8.dp
      ) {
        Row(
          modifier = Modifier.padding(start = 16.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = message,
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
              color = colorScheme.onSurface
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(48.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Dismiss",
              tint = colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}
