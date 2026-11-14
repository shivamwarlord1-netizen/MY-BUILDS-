package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.theme.kinetiqColors
import com.example.ui.viewmodel.WellnessUiState
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun InsightsScreen(
  uiState: WellnessUiState,
  modifier: Modifier = Modifier
) {
  val dates = uiState.pastWeekDates
  val targetSteps = uiState.userProfile.targetSteps
  val targetWater = uiState.userProfile.targetWaterMl
  val targetSleep = uiState.userProfile.targetSleepHours

  val activitiesByDate = uiState.pastWeekActivities.associateBy { it.date }
  val sleepByDate = uiState.pastWeekSleep.associateBy { it.date }

  val weekSteps = dates.map { d -> activitiesByDate[d]?.stepCount ?: 0 }
  val avgSteps = if (weekSteps.isNotEmpty()) weekSteps.average().toInt() else 0

  val weekSleepHours = dates.map { d -> (sleepByDate[d]?.durationMinutes ?: 0) / 60f }
  val avgSleep = if (weekSleepHours.any { it > 0 }) {
    weekSleepHours.filter { it > 0 }.average().toFloat()
  } else 0f

  val totalHabits = uiState.activeHabits.size
  val weekHabitLogs = uiState.pastWeekHabitLogs
  val habitCompletionRate = if (totalHabits > 0 && dates.isNotEmpty()) {
    val totalOpportunities = totalHabits * dates.size
    val doneCount = weekHabitLogs.size
    ((doneCount.toFloat() / totalOpportunities) * 100).toInt().coerceIn(0, 100)
  } else 0

  val colorScheme = MaterialTheme.colorScheme
  val semanticColors = MaterialTheme.kinetiqColors

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Column(modifier = Modifier.padding(top = 4.dp)) {
        Text(
          text = "WEEKLY TELEMETRY",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
          ),
          color = colorScheme.primary
        )
        Text(
          text = "Bio-Kinetic Insights",
          style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Black,
            color = colorScheme.onBackground
          )
        )
        Text(
          text = "7-day holistic trend analysis computed strictly from on-device records.",
          style = MaterialTheme.typography.bodySmall,
          color = colorScheme.onSurfaceVariant
        )
      }
    }

    item {
      WeeklyDigestCard(
        avgSteps = avgSteps,
        targetSteps = targetSteps,
        avgSleep = avgSleep,
        targetSleep = targetSleep,
        habitRate = habitCompletionRate
      )
    }

    item {
      WeeklyBarChartCard(
        title = "Kinetic Movement Output",
        subtitle = "7-Day Step Progression (Target: $targetSteps)",
        icon = Icons.Default.DirectionsWalk,
        iconTint = semanticColors.steps,
        dates = dates,
        values = weekSteps.map { it.toFloat() },
        targetValue = targetSteps.toFloat(),
        barColor = semanticColors.steps,
        unitLabel = "steps",
        formatValue = { "${it.toInt()}" }
      )
    }

    item {
      WeeklyBarChartCard(
        title = "Restorative Sleep Duration",
        subtitle = "Nightly Sleep Hours (Target: ${String.format(Locale.US, "%.1f", targetSleep)}h)",
        icon = Icons.Default.Bedtime,
        iconTint = semanticColors.sleep,
        dates = dates,
        values = weekSleepHours,
        targetValue = targetSleep,
        barColor = semanticColors.sleep,
        unitLabel = "hours",
        formatValue = { String.format(Locale.US, "%.1fh", it) }
      )
    }

    item {
      HabitConsistencyCard(
        completionRate = habitCompletionRate,
        habitsCount = totalHabits,
        totalLogs = weekHabitLogs.size
      )
    }

    item {
      LifestyleTakeawaysCard(
        avgSteps = avgSteps,
        avgSleep = avgSleep,
        targetSteps = targetSteps,
        targetSleep = targetSleep
      )
    }

    item {
      MedicalDisclaimerBanner()
      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

@Composable
private fun WeeklyDigestCard(
  avgSteps: Int,
  targetSteps: Int,
  avgSleep: Float,
  targetSleep: Float,
  habitRate: Int
) {
  val colorScheme = MaterialTheme.colorScheme
  val semanticColors = MaterialTheme.kinetiqColors
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = 1.dp,
        brush = Brush.linearGradient(
          colors = listOf(colorScheme.primary.copy(alpha = 0.45f), colorScheme.outline.copy(alpha = 0.65f))
        ),
        shape = RoundedCornerShape(22.dp)
      ),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = semanticColors.surface1)
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = colorScheme.primary,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = "WEEKLY DIGEST SYNTHESIS",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            fontSize = 10.sp
          ),
          color = colorScheme.primary
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      val stepAnalysis = if (avgSteps >= targetSteps) {
        "• Movement: Averaged $avgSteps steps/day, exceeding your daily activity baseline with consistent pacing."
      } else {
        "• Movement: Daily average is $avgSteps steps. Adding an easy 12-minute walk closes the remaining gap comfortably."
      }

      val sleepAnalysis = if (avgSleep >= targetSleep - 0.5f) {
        "• Circadian Rest: Averaged ${String.format(Locale.US, "%.1f", avgSleep)} hours of sleep, supporting physical cellular recovery."
      } else if (avgSleep > 0f) {
        "• Circadian Rest: Averaged ${String.format(Locale.US, "%.1f", avgSleep)} hours. An earlier digital sunset can improve sleep depth."
      } else {
        "• Circadian Rest: Log your sleep regularly to observe circadian rhythm stabilization."
      }

      val habitAnalysis = "• Habit Momentum: Executed $habitRate% of planned wellness rituals over the past 7 days."

      Text(
        text = "$stepAnalysis\n\n$sleepAnalysis\n\n$habitAnalysis",
        style = MaterialTheme.typography.bodyMedium,
        color = colorScheme.onSurface,
        lineHeight = 20.sp
      )
    }
  }
}

@Composable
private fun WeeklyBarChartCard(
  title: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconTint: Color,
  dates: List<String>,
  values: List<Float>,
  targetValue: Float,
  barColor: Color,
  unitLabel: String,
  formatValue: (Float) -> String
) {
  val maxValue = (values.maxOrNull() ?: targetValue).coerceAtLeast(targetValue * 1.2f).coerceAtLeast(1f)

  val animFraction by animateFloatAsState(
    targetValue = 1f,
    animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
    label = "bar_anim_fraction"
  )

  val colorScheme = MaterialTheme.colorScheme
  val semanticColors = MaterialTheme.kinetiqColors

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, colorScheme.outline.copy(alpha = 0.65f), RoundedCornerShape(22.dp)),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = semanticColors.surface1)
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .background(iconTint.copy(alpha = 0.15f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colorScheme.onSurface
          )
          Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Custom Canvas Bar Chart with Theme Reference Line
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(140.dp)
      ) {
        val targetLineColor = colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
        Canvas(modifier = Modifier.fillMaxSize()) {
          val count = dates.size.coerceAtLeast(1)
          val totalW = size.width
          val totalH = size.height - 24.dp.toPx()
          val barW = (totalW / count) * 0.42f
          val spacing = totalW / count

          // Draw Target Reference Line
          val targetY = totalH - ((targetValue / maxValue) * totalH)
          drawLine(
            color = targetLineColor,
            start = Offset(0f, targetY),
            end = Offset(totalW, targetY),
            strokeWidth = 1.5.dp.toPx()
          )

          // Draw Animated Bars
          values.forEachIndexed { i, v ->
            val barH = (((v / maxValue) * totalH * animFraction)).coerceAtLeast(4.dp.toPx())
            val x = (i * spacing) + (spacing / 2) - (barW / 2)
            val y = totalH - barH

            val isGoalMet = v >= targetValue
            val fill = if (isGoalMet) barColor else barColor.copy(alpha = 0.45f)

            drawRoundRect(
              color = fill,
              topLeft = Offset(x, y),
              size = Size(barW, barH),
              cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
          }
        }
      }

      // Day labels underneath
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        val inSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outSdf = SimpleDateFormat("EEE", Locale.getDefault())
        dates.forEach { d ->
          val label = try {
            val dateObj = inSdf.parse(d)
            if (dateObj != null) outSdf.format(dateObj).uppercase(Locale.ROOT) else d.takeLast(2)
          } catch (e: Exception) {
            d.takeLast(2)
          }
          Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              letterSpacing = 0.5.sp
            ),
            color = colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

@Composable
private fun HabitConsistencyCard(
  completionRate: Int,
  habitsCount: Int,
  totalLogs: Int
) {
  val colorScheme = MaterialTheme.colorScheme
  val semanticColors = MaterialTheme.kinetiqColors
  val habitTint = semanticColors.habits
  val animRate by animateFloatAsState(
    targetValue = completionRate / 100f,
    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
    label = "habit_rate_anim"
  )

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, colorScheme.outline.copy(alpha = 0.65f), RoundedCornerShape(22.dp)),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = semanticColors.surface1)
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .background(habitTint.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.SelfImprovement,
              contentDescription = null,
              tint = habitTint,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "HABIT MOMENTUM",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                fontSize = 10.sp
              ),
              color = habitTint
            )
            Text(
              text = "$totalLogs completions across $habitsCount active rituals",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = colorScheme.onSurface
            )
          }
        }

        Text(
          text = "$completionRate%",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Black,
            color = habitTint,
            fontSize = 26.sp
          )
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      LinearProgressIndicator(
        progress = { animRate },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp)),
        color = habitTint,
        trackColor = habitTint.copy(alpha = 0.15f)
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Sustainable habit stack loops reinforce neurological pathways without requiring intense daily willpower. Keep showing up steadily.",
        style = MaterialTheme.typography.bodySmall,
        color = colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun LifestyleTakeawaysCard(
  avgSteps: Int,
  avgSleep: Float,
  targetSteps: Int,
  targetSleep: Float
) {
  val colorScheme = MaterialTheme.colorScheme
  val semanticColors = MaterialTheme.kinetiqColors
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, colorScheme.outline.copy(alpha = 0.65f), RoundedCornerShape(22.dp)),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = semanticColors.surface1)
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Lightbulb,
          contentDescription = null,
          tint = colorScheme.primary,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = "BIO-TELEMETRY TAKEAWAYS",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            fontSize = 10.sp
          ),
          color = colorScheme.primary
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "• Consistent wake-up anchors stabilize daytime cortisol peaks.\n• 7,500+ daily steps optimizes cardiovascular longevity plateaus without overtraining.\n• Scheduled recovery days allow neuromuscular glycogen recovery.",
        style = MaterialTheme.typography.bodyMedium,
        color = colorScheme.onSurface,
        lineHeight = 20.sp
      )
    }
  }
}
