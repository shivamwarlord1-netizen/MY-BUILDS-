package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SensorsOff
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.model.ActivityRecord
import com.example.data.model.Habit
import com.example.steps.StepPermissionHelper
import com.example.steps.StepTrackerStatus
import com.example.ui.components.HolisticWellnessRing
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.components.bounceClick
import com.example.ui.theme.BioCyan
import com.example.ui.theme.DuskIndigo
import com.example.ui.theme.KineticViolet
import com.example.ui.theme.KinetiqBlack
import com.example.ui.theme.KinetiqBorderGlow
import com.example.ui.theme.KinetiqBorderSubtle
import com.example.ui.theme.KinetiqCardSurface
import com.example.ui.theme.KinetiqElevatedCard
import com.example.ui.theme.KinetiqObsidianSurface
import com.example.ui.theme.KinetiqPurpleDeep
import com.example.ui.theme.KinetiqPurpleLavender
import com.example.ui.theme.KinetiqPurpleNeon
import com.example.ui.theme.KinetiqPurplePrimary
import com.example.ui.theme.KinetiqTextMuted
import com.example.ui.theme.KinetiqTextSubtle
import com.example.ui.theme.KinetiqTextWhite
import com.example.ui.theme.RadiantAmber
import com.example.ui.viewmodel.WellnessUiState
import com.example.ui.viewmodel.WellnessViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun TodayScreen(
  uiState: WellnessUiState,
  viewModel: WellnessViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  var showAddWaterDialog by remember { mutableStateOf(false) }
  var showLogSleepDialog by remember { mutableStateOf(false) }
  var showAddHabitDialog by remember { mutableStateOf(false) }
  var showWaterLogsExpanded by remember { mutableStateOf(false) }
  var permissionDeniedOnce by remember { mutableStateOf(false) }

  val stepPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) {
    viewModel.onStepPermissionsResult()
    if (!StepPermissionHelper.hasActivityRecognitionPermission(context)) {
      permissionDeniedOnce = true
    } else {
      permissionDeniedOnce = false
    }
  }

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        viewModel.refreshStepTrackerState(autoStartIfPermitted = true)
        if (StepPermissionHelper.hasActivityRecognitionPermission(context)) {
          permissionDeniedOnce = false
        }
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  val targetSteps = uiState.userProfile.targetSteps
  val currentSteps = uiState.todayActivity?.stepCount ?: 0
  val stepProgress = (currentSteps.toFloat() / targetSteps).coerceIn(0f, 1f)

  val targetWater = uiState.userProfile.targetWaterMl
  val currentWater = uiState.todayWaterTotalMl
  val waterProgress = (currentWater.toFloat() / targetWater).coerceIn(0f, 1f)

  val targetSleepHours = uiState.userProfile.targetSleepHours
  val recordedSleepMinutes = uiState.todaySleep?.durationMinutes ?: 0
  val sleepProgress = ((recordedSleepMinutes / 60f) / targetSleepHours).coerceIn(0f, 1f)

  val totalHabits = uiState.activeHabits.size
  val completedHabits = uiState.todayHabitLogs.size
  val habitProgress = if (totalHabits > 0) (completedHabits.toFloat() / totalHabits).coerceIn(0f, 1f) else 0f

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      // 1. Holistic 4-Pillar Equilibrium Ring (with animated sweep & breathing pulse)
      HolisticWellnessRing(
        stepProgress = stepProgress,
        waterProgress = waterProgress,
        sleepProgress = sleepProgress,
        habitProgress = habitProgress
      )
    }

    item {
      // 2. Interactive Vagus Nerve Respiration (Box Breathing)
      InteractiveBreathingCard()
    }

    item {
      // 3. Real-Time Movement & Step Counter Card
      ActivityPillarCard(
        currentSteps = currentSteps,
        targetSteps = targetSteps,
        activeMinutes = uiState.todayActivity?.activeMinutes ?: 0,
        distanceKm = uiState.todayActivity?.distanceKm ?: 0f,
        calories = uiState.todayActivity?.caloriesEstimated ?: 0,
        isRestDay = uiState.todayActivity?.isRestDay ?: false,
        pastWeekDates = uiState.pastWeekDates,
        pastWeekActivities = uiState.pastWeekActivities,
        todayDate = uiState.todayDate,
        stepTrackerStatus = uiState.stepTrackerStatus,
        showSettingsShortcut = permissionDeniedOnce,
        onEnableTracking = {
          val perms = StepPermissionHelper.requiredRuntimePermissions()
          if (perms.isNotEmpty()) {
            stepPermissionLauncher.launch(perms)
          } else {
            viewModel.startStepTracking()
          }
        },
        onTogglePauseTracking = {
          if (uiState.stepTrackerStatus == StepTrackerStatus.ACTIVE) {
            viewModel.pauseStepTracking()
          } else {
            viewModel.startStepTracking()
          }
        },
        onOpenAppSettings = {
          val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
          )
          context.startActivity(intent)
        },
        onToggleRestDay = { viewModel.setRestDay(!(uiState.todayActivity?.isRestDay ?: false)) }
      )
    }

    item {
      // 4. Bio-Hydration Tracker Card
      HydrationPillarCard(
        currentWaterMl = currentWater,
        targetWaterMl = targetWater,
        logs = uiState.todayWaterLogs,
        isExpanded = showWaterLogsExpanded,
        onToggleExpand = { showWaterLogsExpanded = !showWaterLogsExpanded },
        onQuickAddWater = { amount, type -> viewModel.logWater(amount, type) },
        onOpenCustomDialog = { showAddWaterDialog = true },
        onDeleteLog = { viewModel.deleteWaterLog(it) }
      )
    }

    item {
      // 5. Circadian Sleep Restoration Card
      SleepPillarCard(
        sleepRecord = uiState.todaySleep,
        targetHours = targetSleepHours,
        onOpenLogSleepDialog = { showLogSleepDialog = true }
      )
    }

    item {
      // 6. Habit Stack Rituals Card
      HabitsPillarCard(
        habits = uiState.activeHabits,
        completedHabitIds = uiState.todayHabitLogs.map { it.habitId }.toSet(),
        onToggleHabit = { viewModel.toggleHabit(it) },
        onOpenAddHabitDialog = { showAddHabitDialog = true },
        onDeleteHabit = { viewModel.deleteHabit(it) }
      )
    }

    item {
      // 7. Gentle Daytime Telemetry Cues
      GentleRemindersCard(
        waterRemainingMl = (targetWater - currentWater).coerceAtLeast(0),
        stepsRemaining = (targetSteps - currentSteps).coerceAtLeast(0)
      )
    }

    item {
      // 8. Medical Safety Disclaimer
      MedicalDisclaimerBanner()
      Spacer(modifier = Modifier.height(20.dp))
    }
  }

  // Dialogs
  if (showAddWaterDialog) {
    AddWaterCustomDialog(
      onDismiss = { showAddWaterDialog = false },
      onConfirm = { amountMl, type ->
        viewModel.logWater(amountMl, type)
        showAddWaterDialog = false
      }
    )
  }

  if (showLogSleepDialog) {
    LogSleepDialog(
      onDismiss = { showLogSleepDialog = false },
      onConfirm = { bedtime, wake, rating, notes ->
        viewModel.logSleep(bedtime, wake, rating, notes)
        showLogSleepDialog = false
      }
    )
  }

  if (showAddHabitDialog) {
    AddHabitDialog(
      onDismiss = { showAddHabitDialog = false },
      onConfirm = { title, cat, icon ->
        viewModel.addCustomHabit(title, cat, icon)
        showAddHabitDialog = false
      }
    )
  }
}

// ------------------- INTERACTIVE MINDFUL BREATHING CARD -------------------

@Composable
private fun InteractiveBreathingCard() {
  var isPlaying by remember { mutableStateOf(false) }
  var breathPhase by remember { mutableStateOf("Ready") }
  var secondsInPhase by remember { mutableIntStateOf(0) }

  LaunchedEffect(isPlaying) {
    if (isPlaying) {
      while (isPlaying) {
        breathPhase = "Inhale slowly..."
        for (sec in 4 downTo 1) {
          secondsInPhase = sec
          delay(1000)
        }
        breathPhase = "Hold gently..."
        for (sec in 4 downTo 1) {
          secondsInPhase = sec
          delay(1000)
        }
        breathPhase = "Exhale smoothly..."
        for (sec in 4 downTo 1) {
          secondsInPhase = sec
          delay(1000)
        }
        breathPhase = "Rest empty..."
        for (sec in 4 downTo 1) {
          secondsInPhase = sec
          delay(1000)
        }
      }
    } else {
      breathPhase = "4-4-4-4 Vagus Nerve Cadence"
      secondsInPhase = 0
    }
  }

  val infiniteTransition = rememberInfiniteTransition(label = "box_breath_anim")
  val breathScale by infiniteTransition.animateFloat(
    initialValue = if (isPlaying) 0.85f else 1.0f,
    targetValue = if (isPlaying) 1.25f else 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "breath_scale"
  )

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = 1.dp,
        brush = Brush.linearGradient(
          colors = listOf(KinetiqPurpleNeon.copy(alpha = 0.35f), Color.Transparent)
        ),
        shape = RoundedCornerShape(22.dp)
      ),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = KinetiqCardSurface)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.size(54.dp)
        ) {
          Box(
            modifier = Modifier
              .size(48.dp)
              .scale(breathScale)
              .background(
                color = KinetiqPurpleNeon.copy(alpha = if (isPlaying) 0.35f else 0.12f),
                shape = CircleShape
              )
          )
          Icon(
            imageVector = Icons.Default.Spa,
            contentDescription = null,
            tint = KinetiqPurpleNeon,
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
          Text(
            text = "RESONANCE BREATHING",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.2.sp,
              fontSize = 9.sp
            ),
            color = KinetiqPurpleNeon
          )
          Text(
            text = if (isPlaying) "$breathPhase ($secondsInPhase s)" else breathPhase,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.SemiBold,
              color = KinetiqTextWhite
            )
          )
        }
      }

      IconButton(
        onClick = { isPlaying = !isPlaying },
        modifier = Modifier
          .size(44.dp)
          .background(
            brush = Brush.linearGradient(
              colors = listOf(KinetiqPurpleNeon, KinetiqPurplePrimary)
            ),
            shape = CircleShape
          )
          .bounceClick { isPlaying = !isPlaying }
      ) {
        Icon(
          imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
          contentDescription = if (isPlaying) "Pause breathing" else "Start breathing",
          tint = KinetiqBlack,
          modifier = Modifier.size(22.dp)
        )
      }
    }
  }
}

// ------------------- PILLAR CARDS -------------------

@Composable
private fun ActivityPillarCard(
  currentSteps: Int,
  targetSteps: Int,
  activeMinutes: Int,
  distanceKm: Float,
  calories: Int,
  isRestDay: Boolean,
  pastWeekDates: List<String>,
  pastWeekActivities: List<ActivityRecord>,
  todayDate: String,
  stepTrackerStatus: StepTrackerStatus,
  showSettingsShortcut: Boolean,
  onEnableTracking: () -> Unit,
  onTogglePauseTracking: () -> Unit,
  onOpenAppSettings: () -> Unit,
  onToggleRestDay: () -> Unit
) {
  val safeTarget = targetSteps.coerceAtLeast(1)
  val animatedSteps by animateIntAsState(
    targetValue = currentSteps,
    animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
    label = "anim_live_step_count"
  )
  val animProgress by animateFloatAsState(
    targetValue = (currentSteps.toFloat() / safeTarget).coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
    label = "anim_activity_progress"
  )

  val livePulseTransition = rememberInfiniteTransition(label = "live_step_pulse")
  val livePulseAlpha by livePulseTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "live_dot_alpha"
  )

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = 1.dp,
        color = KinetiqBorderSubtle,
        shape = RoundedCornerShape(22.dp)
      ),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = KinetiqCardSurface)
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
              .size(42.dp)
              .background(KineticViolet.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.DirectionsWalk,
              contentDescription = null,
              tint = KineticViolet
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "REAL-TIME STEPS",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.2.sp,
                  fontSize = 10.sp
                ),
                color = KineticViolet
              )
              Spacer(modifier = Modifier.width(8.dp))
              val (badgeLabel, badgeColor) = when (stepTrackerStatus) {
                StepTrackerStatus.ACTIVE -> "LIVE" to Color(0xFF22C55E)
                StepTrackerStatus.PAUSED -> "PAUSED" to RadiantAmber
                StepTrackerStatus.PERMISSION_REQUIRED -> "SETUP" to KineticViolet
                StepTrackerStatus.SENSOR_UNAVAILABLE -> "NO SENSOR" to KinetiqTextMuted
              }
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = badgeColor.copy(alpha = 0.16f),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, badgeColor.copy(alpha = 0.45f)),
                modifier = Modifier.testTag("step_status_badge")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  if (stepTrackerStatus == StepTrackerStatus.ACTIVE) {
                    Box(
                      modifier = Modifier
                        .size(6.dp)
                        .background(badgeColor.copy(alpha = livePulseAlpha), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                  }
                  Text(
                    text = badgeLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 0.6.sp
                    ),
                    color = badgeColor
                  )
                }
              }
            }

            Text(
              text = if (isRestDay) "Active Recovery Day" else "Target: $targetSteps steps",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = KinetiqTextWhite
            )
          }
        }

        FilterChip(
          selected = isRestDay,
          onClick = onToggleRestDay,
          label = { Text("Rest Day", fontSize = 11.sp, color = if (isRestDay) KinetiqBlack else KinetiqTextMuted) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = KineticViolet,
            containerColor = KinetiqObsidianSurface
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isRestDay) KineticViolet else KinetiqBorderSubtle
          ),
          modifier = Modifier.bounceClick(scaleDown = 0.92f) { onToggleRestDay() }
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      val goalReached = currentSteps >= safeTarget
      val ringAccent = if (goalReached) Color(0xFF22C55E) else KineticViolet
      val progressPct = (animProgress * 100f).toInt().coerceIn(0, 100)
      val remainingSteps = (safeTarget - currentSteps).coerceAtLeast(0)

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Circular Progress Indicator wrapping current step count
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(124.dp)
            .testTag("step_circular_progress")
        ) {
          Canvas(
            modifier = Modifier
              .fillMaxSize()
              .padding(6.dp)
          ) {
            val strokePx = 10.dp.toPx()
            val radius = (size.minDimension - strokePx) / 2f
            drawCircle(
              color = KineticViolet.copy(alpha = 0.16f),
              radius = radius,
              style = Stroke(width = strokePx)
            )
            drawArc(
              brush = Brush.linearGradient(
                colors = if (goalReached) {
                  listOf(Color(0xFF22C55E), Color(0xFF4ADE80))
                } else {
                  listOf(KineticViolet, KinetiqPurpleNeon)
                }
              ),
              startAngle = -90f,
              sweepAngle = animProgress * 360f,
              useCenter = false,
              style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "$animatedSteps",
              style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                letterSpacing = (-0.5).sp
              ),
              maxLines = 1,
              color = ringAccent,
              modifier = Modifier.testTag("today_step_count_text")
            )
            Text(
              text = "STEPS",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.9.sp
              ),
              color = KinetiqTextMuted
            )
          }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Column {
            Text(
              text = if (goalReached) "Daily Target Reached!" else "$progressPct% of daily goal",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = if (goalReached) Color(0xFF22C55E) else KinetiqTextWhite
            )
            Text(
              text = if (goalReached) {
                "$safeTarget daily steps completed"
              } else {
                "$remainingSteps steps remaining"
              },
              style = MaterialTheme.typography.labelSmall,
              color = KinetiqTextMuted
            )
          }

          Surface(
            color = KinetiqObsidianSurface,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(0.8.dp, KinetiqBorderSubtle),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              StatMiniMetric("${String.format(Locale.US, "%.1f", distanceKm)} km", "Distance")
              StatMiniMetric("$activeMinutes min", "Active")
              StatMiniMetric("$calories kcal", "Energy")
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 7-Day Weekly Step History Line Chart
      WeeklyStepLineChart(
        pastWeekDates = pastWeekDates,
        pastWeekActivities = pastWeekActivities,
        todayDate = todayDate,
        currentSteps = currentSteps,
        targetSteps = safeTarget
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Live Hardware Step Sensor Controls & Status Strip
      when (stepTrackerStatus) {
        StepTrackerStatus.ACTIVE -> {
          Surface(
            color = KinetiqObsidianSurface,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqBorderSubtle),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Sensors,
                  contentDescription = null,
                  tint = Color(0xFF22C55E),
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Counting steps continuously in background",
                  style = MaterialTheme.typography.bodySmall,
                  color = KinetiqTextMuted
                )
              }

              Spacer(modifier = Modifier.width(8.dp))

              OutlinedButton(
                onClick = onTogglePauseTracking,
                modifier = Modifier
                  .heightIn(min = 48.dp)
                  .testTag("step_tracking_toggle_button")
                  .bounceClick { onTogglePauseTracking() },
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqBorderSubtle),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = KinetiqTextWhite)
              ) {
                Icon(
                  imageVector = Icons.Default.Pause,
                  contentDescription = "Pause step tracking",
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Pause", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }

        StepTrackerStatus.PAUSED -> {
          Surface(
            color = KinetiqObsidianSurface,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqBorderSubtle),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Step tracking paused",
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                  color = KinetiqTextWhite
                )
                Text(
                  text = "Resume to track steps live",
                  style = MaterialTheme.typography.labelSmall,
                  color = KinetiqTextMuted
                )
              }

              Spacer(modifier = Modifier.width(8.dp))

              Button(
                onClick = onTogglePauseTracking,
                modifier = Modifier
                  .heightIn(min = 48.dp)
                  .testTag("step_tracking_toggle_button")
                  .bounceClick { onTogglePauseTracking() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = KineticViolet,
                  contentColor = KinetiqBlack
                )
              ) {
                Icon(
                  imageVector = Icons.Default.PlayArrow,
                  contentDescription = "Resume step tracking",
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Resume", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        StepTrackerStatus.PERMISSION_REQUIRED -> {
          Surface(
            color = KineticViolet.copy(alpha = 0.08f),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, KineticViolet.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Sensors,
                  contentDescription = null,
                  tint = KineticViolet,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Allow Physical Activity access to track your daily steps automatically in real time.",
                  style = MaterialTheme.typography.bodySmall,
                  color = KinetiqTextWhite
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Button(
                  onClick = onEnableTracking,
                  modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .testTag("enable_step_tracking_button")
                    .bounceClick { onEnableTracking() },
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = KineticViolet,
                    contentColor = KinetiqBlack
                  )
                ) {
                  Text("Enable Live Tracking", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                if (showSettingsShortcut) {
                  OutlinedButton(
                    onClick = onOpenAppSettings,
                    modifier = Modifier
                      .heightIn(min = 48.dp)
                      .testTag("open_step_settings_button")
                      .bounceClick { onOpenAppSettings() },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqBorderSubtle),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = KinetiqTextWhite)
                  ) {
                    Text("Settings", fontSize = 12.sp)
                  }
                }
              }
            }
          }
        }

        StepTrackerStatus.SENSOR_UNAVAILABLE -> {
          Surface(
            color = KinetiqObsidianSurface,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqBorderSubtle),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("step_sensor_unavailable_notice")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.SensorsOff,
                contentDescription = "Step sensor unavailable",
                tint = KinetiqTextMuted,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "Hardware step sensor unavailable on this device or emulator. Real-time step tracking requires a built-in step counter sensor.",
                style = MaterialTheme.typography.bodySmall,
                color = KinetiqTextMuted
              )
            }
          }
        }
      }
    }
  }
}

internal data class DailyStepPoint(
  val date: String,
  val dayLabel: String,
  val steps: Int,
  val isToday: Boolean
)

internal fun buildWeeklyStepPoints(
  pastWeekDates: List<String>,
  pastWeekActivities: List<ActivityRecord>,
  todayDate: String,
  currentSteps: Int
): List<DailyStepPoint> {
  val datesToPlot = if (pastWeekDates.isNotEmpty()) pastWeekDates else listOf(todayDate)
  val activityMap = pastWeekActivities.associateBy { it.date }
  val inSdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
  val outSdf = SimpleDateFormat("EEE", Locale.US)

  return datesToPlot.map { date ->
    val isToday = date == todayDate
    val recorded = activityMap[date]?.stepCount ?: 0
    val steps = if (isToday) maxOf(recorded, currentSteps) else recorded
    val parsed = runCatching { inSdf.parse(date) }.getOrNull()
    val shortDay = parsed?.let { outSdf.format(it) } ?: date.takeLast(2)
    DailyStepPoint(
      date = date,
      dayLabel = if (isToday) "Today" else shortDay,
      steps = steps.coerceAtLeast(0),
      isToday = isToday
    )
  }
}

@Composable
private fun WeeklyStepLineChart(
  pastWeekDates: List<String>,
  pastWeekActivities: List<ActivityRecord>,
  todayDate: String,
  currentSteps: Int,
  targetSteps: Int
) {
  val points = remember(pastWeekDates, pastWeekActivities, todayDate, currentSteps) {
    buildWeeklyStepPoints(
      pastWeekDates = pastWeekDates,
      pastWeekActivities = pastWeekActivities,
      todayDate = todayDate,
      currentSteps = currentSteps
    )
  }
  val haptic = LocalHapticFeedback.current
  var selectedIndex by remember(points.size) {
    mutableIntStateOf((points.size - 1).coerceAtLeast(0))
  }
  val safeSelectedIndex = selectedIndex.coerceIn(0, (points.size - 1).coerceAtLeast(0))
  val selectedPoint = points.getOrNull(safeSelectedIndex)

  var chartVisible by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) { chartVisible = true }
  val animReveal by animateFloatAsState(
    targetValue = if (chartVisible) 1f else 0f,
    animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
    label = "weekly_step_line_reveal"
  )

  val weeklyAverage = remember(points) {
    if (points.isEmpty()) 0 else points.map { it.steps }.average().roundToInt()
  }
  val safeTarget = targetSteps.coerceAtLeast(1)
  val maxObserved = points.maxOfOrNull { it.steps } ?: 0
  val maxChartSteps = (maxOf(safeTarget, maxObserved, 1000) * 1.12f)
  val emeraldColor = Color(0xFF22C55E)

  Surface(
    color = KinetiqObsidianSurface,
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(0.8.dp, KinetiqBorderSubtle),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("weekly_step_line_chart")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "7-DAY STEP TREND",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.1.sp,
              fontSize = 10.sp
            ),
            color = KinetiqPurpleNeon
          )
          Text(
            text = "Avg $weeklyAverage steps/day • Goal $safeTarget",
            style = MaterialTheme.typography.labelSmall,
            color = KinetiqTextMuted
          )
        }

        if (selectedPoint != null) {
          val selectedMetGoal = selectedPoint.steps >= safeTarget
          Surface(
            color = if (selectedMetGoal) {
              emeraldColor.copy(alpha = 0.14f)
            } else {
              KineticViolet.copy(alpha = 0.14f)
            },
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(
              0.8.dp,
              if (selectedMetGoal) emeraldColor.copy(alpha = 0.45f) else KineticViolet.copy(alpha = 0.45f)
            ),
            modifier = Modifier.testTag("weekly_step_selected_badge")
          ) {
            Text(
              text = "${selectedPoint.dayLabel}: ${selectedPoint.steps}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = if (selectedMetGoal) emeraldColor else KinetiqTextWhite,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(124.dp)
      ) {
        Canvas(
          modifier = Modifier
            .fillMaxSize()
            .pointerInput(points) {
              detectTapGestures { tapOffset ->
                if (points.isEmpty()) return@detectTapGestures
                val sidePad = 14.dp.toPx()
                val chartW = (size.width - sidePad * 2f).coerceAtLeast(1f)
                val nearest = if (points.size == 1) {
                  0
                } else {
                  points.indices.minByOrNull { i ->
                    val nodeX = sidePad + (i.toFloat() / (points.size - 1)) * chartW
                    kotlin.math.abs(tapOffset.x - nodeX)
                  } ?: 0
                }
                if (nearest != selectedIndex) {
                  haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                  selectedIndex = nearest
                }
              }
            }
        ) {
          if (points.isEmpty()) return@Canvas

          val sidePad = 14.dp.toPx()
          val topPad = 12.dp.toPx()
          val bottomPad = 10.dp.toPx()
          val chartW = (size.width - sidePad * 2f).coerceAtLeast(1f)
          val chartH = (size.height - topPad - bottomPad).coerceAtLeast(1f)
          val baseLineY = topPad + chartH

          // Subtle baseline & midpoint horizontal gridlines
          drawLine(
            color = KinetiqBorderSubtle.copy(alpha = 0.5f),
            start = Offset(sidePad, baseLineY),
            end = Offset(sidePad + chartW, baseLineY),
            strokeWidth = 1.dp.toPx()
          )
          drawLine(
            color = KinetiqBorderSubtle.copy(alpha = 0.3f),
            start = Offset(sidePad, topPad + chartH * 0.5f),
            end = Offset(sidePad + chartW, topPad + chartH * 0.5f),
            strokeWidth = 0.8.dp.toPx()
          )

          // Dashed daily step target reference line
          val targetFraction = (safeTarget.toFloat() / maxChartSteps).coerceIn(0f, 1f)
          val targetY = topPad + chartH - (targetFraction * chartH)
          drawLine(
            color = emeraldColor.copy(alpha = 0.55f),
            start = Offset(sidePad, targetY),
            end = Offset(sidePad + chartW, targetY),
            strokeWidth = 1.2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 5.dp.toPx()), 0f)
          )

          val coords = points.mapIndexed { i, point ->
            val x = if (points.size == 1) {
              sidePad + chartW / 2f
            } else {
              sidePad + (i.toFloat() / (points.size - 1)) * chartW
            }
            val ratio = (point.steps.toFloat() / maxChartSteps).coerceIn(0f, 1f)
            val y = topPad + chartH - (ratio * chartH * animReveal)
            Offset(x, y)
          }

          if (coords.size >= 2) {
            val linePath = Path().apply {
              moveTo(coords.first().x, coords.first().y)
              for (i in 1 until coords.size) {
                val prev = coords[i - 1]
                val curr = coords[i]
                val midX = (prev.x + curr.x) / 2f
                cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
              }
            }

            val fillPath = Path().apply {
              addPath(linePath)
              lineTo(coords.last().x, baseLineY)
              lineTo(coords.first().x, baseLineY)
              close()
            }

            drawPath(
              path = fillPath,
              brush = Brush.verticalGradient(
                colors = listOf(
                  KineticViolet.copy(alpha = 0.30f),
                  KineticViolet.copy(alpha = 0.04f),
                  Color.Transparent
                ),
                startY = topPad,
                endY = baseLineY
              )
            )

            drawPath(
              path = linePath,
              color = KineticViolet,
              style = Stroke(
                width = 2.6.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
              )
            )
          }

          // Vertical guide on selected node
          coords.getOrNull(safeSelectedIndex)?.let { sel ->
            drawLine(
              color = KinetiqBorderGlow.copy(alpha = 0.55f),
              start = Offset(sel.x, sel.y),
              end = Offset(sel.x, baseLineY),
              strokeWidth = 1.dp.toPx(),
              pathEffect = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx()), 0f)
            )
          }

          // Data point nodes
          coords.forEachIndexed { index, center ->
            val metGoal = points[index].steps >= safeTarget
            val nodeColor = if (metGoal) emeraldColor else KinetiqPurpleNeon
            val isSelected = index == safeSelectedIndex

            if (isSelected) {
              drawCircle(
                color = nodeColor.copy(alpha = 0.28f),
                radius = 8.5.dp.toPx(),
                center = center
              )
              drawCircle(
                color = nodeColor,
                radius = 4.8.dp.toPx(),
                center = center
              )
              drawCircle(
                color = KinetiqObsidianSurface,
                radius = 2.dp.toPx(),
                center = center
              )
            } else {
              drawCircle(
                color = nodeColor,
                radius = 3.4.dp.toPx(),
                center = center
              )
              drawCircle(
                color = KinetiqObsidianSurface,
                radius = 1.4.dp.toPx(),
                center = center
              )
            }
          }
        }
      }

      // Interactive day selector labels (48.dp minimum touch target height)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        points.forEachIndexed { index, point ->
          val isSelected = index == safeSelectedIndex
          val metGoal = point.steps >= safeTarget
          Box(
            modifier = Modifier
              .weight(1f)
              .heightIn(min = 48.dp)
              .clip(RoundedCornerShape(8.dp))
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                selectedIndex = index
              }
              .testTag("weekly_step_day_$index"),
            contentAlignment = Alignment.Center
          ) {
            Box(
              modifier = Modifier
                .background(
                  color = if (isSelected) KineticViolet.copy(alpha = 0.18f) else Color.Transparent,
                  shape = RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 4.dp, vertical = 3.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = point.dayLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = if (isSelected || point.isToday) FontWeight.Bold else FontWeight.Medium
                ),
                color = when {
                  isSelected && metGoal -> emeraldColor
                  isSelected -> KinetiqTextWhite
                  point.isToday -> KinetiqPurpleNeon
                  else -> KinetiqTextSubtle
                },
                maxLines = 1
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun HydrationPillarCard(
  currentWaterMl: Int,
  targetWaterMl: Int,
  logs: List<com.example.data.model.HydrationRecord>,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
  onQuickAddWater: (Int, String) -> Unit,
  onOpenCustomDialog: () -> Unit,
  onDeleteLog: (Long) -> Unit
) {
  val glassCount = (currentWaterMl / 250)
  val progress = (currentWaterMl.toFloat() / targetWaterMl).coerceIn(0f, 1f)

  val animWaterProgress by animateFloatAsState(
    targetValue = progress,
    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
    label = "anim_water_progress"
  )

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = 1.dp,
        color = KinetiqBorderSubtle,
        shape = RoundedCornerShape(22.dp)
      ),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = KinetiqCardSurface)
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .background(BioCyan.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.WaterDrop,
              contentDescription = null,
              tint = BioCyan
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "BIO-HYDRATION",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                fontSize = 10.sp
              ),
              color = BioCyan
            )
            Text(
              text = "Target: $targetWaterMl ml (~${targetWaterMl / 250} glasses)",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = KinetiqTextWhite
            )
          }
        }

        IconButton(
          onClick = onToggleExpand,
          modifier = Modifier.bounceClick(scaleDown = 0.88f) { onToggleExpand() }
        ) {
          Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = "Expand hydration logs",
            tint = KinetiqTextMuted
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Column {
          Text(
            text = "$currentWaterMl ml",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Black,
              color = BioCyan,
              fontSize = 32.sp
            )
          )
          Text(
            text = "$glassCount glasses consumed",
            style = MaterialTheme.typography.labelSmall,
            color = KinetiqTextMuted
          )
        }

        Text(
          text = "${(animWaterProgress * 100).toInt()}% of goal",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = BioCyan
          )
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      LinearProgressIndicator(
        progress = { animWaterProgress },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp)),
        color = BioCyan,
        trackColor = BioCyan.copy(alpha = 0.15f)
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Quick Hydration Buttons with spring bounce
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = { onQuickAddWater(200, "Cup") },
          modifier = Modifier
            .weight(1f)
            .bounceClick { onQuickAddWater(200, "Cup") },
          border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqBorderSubtle),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = KinetiqTextWhite),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("+200ml", fontSize = 11.sp)
        }
        OutlinedButton(
          onClick = { onQuickAddWater(250, "Glass") },
          modifier = Modifier
            .weight(1f)
            .bounceClick { onQuickAddWater(250, "Glass") },
          border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqBorderSubtle),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = KinetiqTextWhite),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("+250ml", fontSize = 11.sp)
        }
        OutlinedButton(
          onClick = { onQuickAddWater(500, "Flask") },
          modifier = Modifier
            .weight(1f)
            .bounceClick { onQuickAddWater(500, "Flask") },
          border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqBorderSubtle),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = KinetiqTextWhite),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("+500ml", fontSize = 11.sp)
        }
        Button(
          onClick = onOpenCustomDialog,
          modifier = Modifier
            .weight(1f)
            .bounceClick { onOpenCustomDialog() },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = BioCyan,
            contentColor = KinetiqBlack
          )
        ) {
          Text("+Custom", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      // Expandable Log History List
      AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        Column(modifier = Modifier.padding(top = 16.dp)) {
          Text(
            text = "Today's Intake Logs",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = KinetiqTextWhite
          )
          Spacer(modifier = Modifier.height(8.dp))

          if (logs.isEmpty()) {
            Text(
              text = "No water logs recorded yet today. Tap buttons above to record.",
              style = MaterialTheme.typography.bodySmall,
              color = KinetiqTextMuted
            )
          } else {
            val timeSdf = SimpleDateFormat("h:mm a", Locale.getDefault())
            logs.take(5).forEach { record ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = BioCyan,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "${record.amountMl} ml (${record.containerType})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = KinetiqTextWhite
                  )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = timeSdf.format(Date(record.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = KinetiqTextSubtle
                  )
                  IconButton(
                    onClick = { onDeleteLog(record.id) },
                    modifier = Modifier
                      .size(28.dp)
                      .bounceClick { onDeleteLog(record.id) }
                  ) {
                    Icon(
                      imageVector = Icons.Default.Delete,
                      contentDescription = "Delete entry",
                      tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SleepPillarCard(
  sleepRecord: com.example.data.model.SleepRecord?,
  targetHours: Float,
  onOpenLogSleepDialog: () -> Unit
) {
  val hasSleepLogged = sleepRecord != null
  val durationHours = if (hasSleepLogged) sleepRecord!!.durationMinutes / 60f else 0f
  val hoursInt = (durationHours).toInt()
  val minutesInt = ((durationHours - hoursInt) * 60).toInt()

  val animSleepProgress by animateFloatAsState(
    targetValue = (durationHours / targetHours).coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
    label = "anim_sleep_progress"
  )

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = 1.dp,
        color = KinetiqBorderSubtle,
        shape = RoundedCornerShape(22.dp)
      ),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = KinetiqCardSurface)
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .background(DuskIndigo.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Bedtime,
              contentDescription = null,
              tint = DuskIndigo
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "CIRCADIAN RESTORATION",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                fontSize = 10.sp
              ),
              color = DuskIndigo
            )
            Text(
              text = "Target: ${String.format(Locale.US, "%.1f", targetHours)} hours",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = KinetiqTextWhite
            )
          }
        }

        Button(
          onClick = onOpenLogSleepDialog,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = DuskIndigo,
            contentColor = KinetiqBlack
          ),
          modifier = Modifier.bounceClick { onOpenLogSleepDialog() }
        ) {
          Text(if (hasSleepLogged) "Update" else "Log Sleep", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (hasSleepLogged) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          Column {
            Text(
              text = "${hoursInt}h ${minutesInt}m",
              style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                color = DuskIndigo,
                fontSize = 32.sp
              )
            )
            Text(
              text = if (sleepRecord!!.notes.isNotBlank()) sleepRecord.notes else "Restful morning logged",
              style = MaterialTheme.typography.labelSmall,
              color = KinetiqTextMuted
            )
          }

          // Star rating
          Row {
            for (i in 1..5) {
              Icon(
                imageVector = if (i <= sleepRecord!!.qualityRating) Icons.Default.Star else Icons.Outlined.StarBorder,
                contentDescription = null,
                tint = if (i <= sleepRecord.qualityRating) RadiantAmber else KinetiqBorderSubtle,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LinearProgressIndicator(
          progress = { animSleepProgress },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = DuskIndigo,
          trackColor = DuskIndigo.copy(alpha = 0.15f)
        )
      } else {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          color = KinetiqObsidianSurface,
          border = androidx.compose.foundation.BorderStroke(0.8.dp, KinetiqBorderSubtle)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "No sleep recorded for last night. Tap 'Log Sleep' to track your bedtime and restorative quality.",
              style = MaterialTheme.typography.bodySmall,
              color = KinetiqTextMuted
            )
          }
        }
      }
    }
  }
}

@Composable
private fun HabitsPillarCard(
  habits: List<Habit>,
  completedHabitIds: Set<Long>,
  onToggleHabit: (Long) -> Unit,
  onOpenAddHabitDialog: () -> Unit,
  onDeleteHabit: (Long) -> Unit
) {
  val completedCount = habits.count { completedHabitIds.contains(it.id) }
  val total = habits.size
  val haptic = LocalHapticFeedback.current

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = 1.dp,
        color = KinetiqBorderSubtle,
        shape = RoundedCornerShape(22.dp)
      ),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = KinetiqCardSurface)
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .background(RadiantAmber.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.SelfImprovement,
              contentDescription = null,
              tint = RadiantAmber
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "HABIT STACK",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                fontSize = 10.sp
              ),
              color = RadiantAmber
            )
            Text(
              text = "$completedCount of $total completed today",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = KinetiqTextWhite
            )
          }
        }

        IconButton(
          onClick = onOpenAddHabitDialog,
          modifier = Modifier
            .size(38.dp)
            .background(KinetiqObsidianSurface, CircleShape)
            .border(0.8.dp, KinetiqBorderSubtle, CircleShape)
            .testTag("add_habit_button")
            .bounceClick { onOpenAddHabitDialog() }
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add custom habit",
            tint = RadiantAmber,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      if (habits.isEmpty()) {
        Text(
          text = "No active rituals configured. Tap '+' to create your first sustainable daily anchor.",
          style = MaterialTheme.typography.bodySmall,
          color = KinetiqTextMuted
        )
      } else {
        habits.forEach { habit ->
          val isDone = completedHabitIds.contains(habit.id)
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .border(
                width = 0.8.dp,
                color = if (isDone) RadiantAmber.copy(alpha = 0.4f) else KinetiqBorderSubtle,
                shape = RoundedCornerShape(14.dp)
              )
              .bounceClick(scaleDown = 0.96f) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onToggleHabit(habit.id)
              },
            shape = RoundedCornerShape(14.dp),
            color = if (isDone) RadiantAmber.copy(alpha = 0.12f) else KinetiqObsidianSurface
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Checkbox(
                  checked = isDone,
                  onCheckedChange = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onToggleHabit(habit.id)
                  },
                  colors = CheckboxDefaults.colors(
                    checkedColor = RadiantAmber,
                    checkmarkColor = KinetiqBlack,
                    uncheckedColor = KinetiqBorderSubtle
                  )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = habit.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = if (isDone) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isDone) KinetiqTextWhite else KinetiqTextMuted
                  )
                  Text(
                    text = habit.category.uppercase(Locale.ROOT),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.sp,
                      letterSpacing = 0.8.sp
                    ),
                    color = KinetiqTextSubtle
                  )
                }
              }

              IconButton(
                onClick = { onDeleteHabit(habit.id) },
                modifier = Modifier
                  .size(28.dp)
                  .bounceClick { onDeleteHabit(habit.id) }
              ) {
                Icon(
                  imageVector = Icons.Default.Delete,
                  contentDescription = "Delete habit",
                  tint = KinetiqTextSubtle,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun GentleRemindersCard(
  waterRemainingMl: Int,
  stepsRemaining: Int
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = 1.dp,
        color = KinetiqBorderSubtle,
        shape = RoundedCornerShape(22.dp)
      ),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = KinetiqCardSurface)
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.NotificationsActive,
          contentDescription = null,
          tint = KinetiqPurpleNeon,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = "DAYTIME RECOVERY CUES",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            fontSize = 10.sp
          ),
          color = KinetiqPurpleNeon
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      val reminderTexts = mutableListOf<String>()
      if (waterRemainingMl > 0) {
        reminderTexts.add("💧 Hydration telemetry: ~$waterRemainingMl ml remaining to reach your comfortable target.")
      } else {
        reminderTexts.add("💧 Hydration complete: You've reached your daily fluid quota. Sip as desired!")
      }

      if (stepsRemaining > 0) {
        reminderTexts.add("🚶 Movement momentum: A quick 15-minute stroll bridges your remaining $stepsRemaining steps.")
      } else {
        reminderTexts.add("🚶 Movement complete: Step baseline conquered with steady pace!")
      }
      reminderTexts.add("🌙 Digital sunset: Lower ambient illumination 45 mins prior to sleep for natural melatonin peak.")

      reminderTexts.forEach { text ->
        Text(
          text = text,
          style = MaterialTheme.typography.bodySmall,
          color = KinetiqTextMuted,
          modifier = Modifier.padding(vertical = 4.dp),
          lineHeight = 18.sp
        )
      }
    }
  }
}

@Composable
private fun StatMiniMetric(value: String, label: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
      color = KinetiqTextWhite
    )
    Text(
      text = label.uppercase(Locale.ROOT),
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 9.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp
      ),
      color = KinetiqTextSubtle
    )
  }
}

// ------------------- DIALOGS -------------------

@Composable
fun AddWaterCustomDialog(
  onDismiss: () -> Unit,
  onConfirm: (Int, String) -> Unit
) {
  var amountText by remember { mutableStateOf("300") }
  var containerType by remember { mutableStateOf("Glass") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = KinetiqElevatedCard,
    titleContentColor = KinetiqTextWhite,
    textContentColor = KinetiqTextMuted,
    title = { Text("Custom Fluid Intake") },
    text = {
      Column {
        Text("Volume in milliliters (ml):")
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = amountText,
          onValueChange = { amountText = it.filter { char -> char.isDigit() } },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("Vessel Type:")
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("Glass", "Bottle", "Mug").forEach { type ->
            FilterChip(
              selected = containerType == type,
              onClick = { containerType = type },
              label = { Text(type, fontSize = 12.sp) }
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amount = amountText.toIntOrNull() ?: 0
          if (amount > 0) onConfirm(amount, containerType)
        },
        colors = ButtonDefaults.buttonColors(containerColor = BioCyan, contentColor = KinetiqBlack)
      ) {
        Text("Log Intake", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = KinetiqTextMuted)
      }
    }
  )
}

@Composable
fun LogSleepDialog(
  onDismiss: () -> Unit,
  onConfirm: (Long, Long, Int, String) -> Unit
) {
  var sleepHoursFloat by remember { mutableFloatStateOf(7.5f) }
  var rating by remember { mutableIntStateOf(4) }
  var notes by remember { mutableStateOf("Felt calm and rested") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = KinetiqElevatedCard,
    titleContentColor = KinetiqTextWhite,
    textContentColor = KinetiqTextMuted,
    title = { Text("Log Circadian Sleep") },
    text = {
      Column {
        Text(
          text = "Duration: ${String.format(Locale.US, "%.1f", sleepHoursFloat)} hours",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
          color = KinetiqTextWhite
        )
        Slider(
          value = sleepHoursFloat,
          onValueChange = { sleepHoursFloat = it },
          valueRange = 3f..12f,
          steps = 17
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Recovery Rating (1–5 Stars):",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
          color = KinetiqTextWhite
        )
        Row(
          modifier = Modifier.padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          for (i in 1..5) {
            IconButton(
              onClick = { rating = i },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = if (i <= rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                contentDescription = "$i stars",
                tint = if (i <= rating) RadiantAmber else KinetiqBorderSubtle
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("Wake feeling notes:")
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val now = System.currentTimeMillis()
          val durationMillis = (sleepHoursFloat * 3600 * 1000).toLong()
          val bedtime = now - durationMillis
          onConfirm(bedtime, now, rating, notes)
        },
        colors = ButtonDefaults.buttonColors(containerColor = DuskIndigo, contentColor = KinetiqBlack)
      ) {
        Text("Save Record", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = KinetiqTextMuted)
      }
    }
  )
}

@Composable
fun AddHabitDialog(
  onDismiss: () -> Unit,
  onConfirm: (String, String, String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Mindfulness") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = KinetiqElevatedCard,
    titleContentColor = KinetiqTextWhite,
    textContentColor = KinetiqTextMuted,
    title = { Text("Anchor New Habit") },
    text = {
      Column {
        Text("Habit Name:")
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          placeholder = { Text("e.g. 10-min Posture Walk") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Category Domain:")
        Spacer(modifier = Modifier.height(6.dp))
        val categories = listOf("Movement", "Hydration", "Rest", "Mindfulness", "Nutrition")
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            categories.take(3).forEach { cat ->
              FilterChip(
                selected = category == cat,
                onClick = { category = cat },
                label = { Text(cat, fontSize = 11.sp) }
              )
            }
          }
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            categories.drop(3).forEach { cat ->
              FilterChip(
                selected = category == cat,
                onClick = { category = cat },
                label = { Text(cat, fontSize = 11.sp) }
              )
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            onConfirm(title.trim(), category, "self_improvement")
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = RadiantAmber, contentColor = KinetiqBlack)
      ) {
        Text("Save Ritual", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = KinetiqTextMuted)
      }
    }
  )
}
