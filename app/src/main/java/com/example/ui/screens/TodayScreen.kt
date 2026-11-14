@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.exercise.Exercise
import com.example.data.exercise.ExerciseDatabase
import com.example.data.exercise.ExerciseTier
import com.example.data.model.ActivityRecord
import com.example.data.model.Habit
import com.example.data.model.HabitLog
import com.example.data.model.HydrationRecord
import com.example.data.model.SleepRecord
import com.example.steps.StepPermissionHelper
import com.example.steps.StepTrackerStatus
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.theme.KinetiqHeroScoreStyle
import com.example.ui.theme.KinetiqSemanticColors
import com.example.ui.theme.KinetiqValueTextStyle
import com.example.ui.theme.kinetiqColors
import com.example.ui.viewmodel.WellnessUiState
import com.example.ui.viewmodel.WellnessViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

// ============================================================================
// Pure Domain Helpers (Real Data Calculations & Legacy Test Compatibility)
// ============================================================================

data class DailyStepPoint(
  val date: String,
  val dayLabel: String,
  val steps: Int,
  val isToday: Boolean
)

fun buildWeeklyStepPoints(
  pastWeekDates: List<String>,
  pastWeekActivities: List<ActivityRecord>,
  todayDate: String,
  currentSteps: Int
): List<DailyStepPoint> {
  val activityMap = pastWeekActivities.associateBy { it.date }
  val inputFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
  val dayFmt = SimpleDateFormat("EEE", Locale.getDefault())

  return pastWeekDates.map { dateStr ->
    val isToday = dateStr == todayDate
    val steps = if (isToday) currentSteps else (activityMap[dateStr]?.stepCount ?: 0)
    val dayLabel = if (isToday) {
      "Today"
    } else {
      try {
        inputFmt.parse(dateStr)?.let { dayFmt.format(it) } ?: dateStr.takeLast(2)
      } catch (_: Exception) {
        dateStr.takeLast(2)
      }
    }
    DailyStepPoint(date = dateStr, dayLabel = dayLabel, steps = steps, isToday = isToday)
  }
}

data class TodayDayStripItem(
  val date: String,
  val dayShort: String,
  val dayNumber: String,
  val fullLabel: String,
  val isToday: Boolean,
  val readinessRatio: Float,
  val hasAnyRecord: Boolean
)

data class TodayReadinessSummary(
  val scorePercent: Int,
  val statusLabel: String,
  val coachingInsight: String,
  val streakDays: Int,
  val isHighReadiness: Boolean
)

fun buildSevenDayStrip(
  pastWeekDates: List<String>,
  todayDate: String,
  targetSteps: Int,
  targetWaterMl: Int,
  targetSleepHours: Float,
  totalHabits: Int,
  todaySteps: Int,
  todayWaterMl: Int,
  todaySleepMinutes: Int,
  todayHabitCount: Int,
  pastWeekActivities: List<ActivityRecord>,
  pastWeekHydration: List<HydrationRecord>,
  pastWeekSleep: List<SleepRecord>,
  pastWeekHabitLogs: List<HabitLog>
): List<TodayDayStripItem> {
  val activityByDate = pastWeekActivities.associateBy { it.date }
  val waterByDate = pastWeekHydration.groupBy { it.date }.mapValues { (_, list) -> list.sumOf { it.amountMl } }
  val sleepByDate = pastWeekSleep.associateBy { it.date }
  val habitsByDate = pastWeekHabitLogs.groupBy { it.date }

  val parseFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
  val shortDayFmt = SimpleDateFormat("EEE", Locale.getDefault())
  val numFmt = SimpleDateFormat("d", Locale.getDefault())
  val fullFmt = SimpleDateFormat("EEE, d MMM", Locale.getDefault())

  return pastWeekDates.map { date ->
    val isToday = date == todayDate
    val steps = if (isToday) todaySteps else (activityByDate[date]?.stepCount ?: 0)
    val water = if (isToday) todayWaterMl else (waterByDate[date] ?: 0)
    val sleepMin = if (isToday) todaySleepMinutes else (sleepByDate[date]?.durationMinutes ?: 0)
    val habitCnt = if (isToday) todayHabitCount else (habitsByDate[date]?.size ?: 0)

    val stepRatio = (steps.toFloat() / targetSteps.coerceAtLeast(1)).coerceIn(0f, 1f)
    val waterRatio = (water.toFloat() / targetWaterMl.coerceAtLeast(1)).coerceIn(0f, 1f)
    val sleepRatio = ((sleepMin / 60f) / targetSleepHours.coerceAtLeast(1f)).coerceIn(0f, 1f)
    val habitRatio = if (totalHabits > 0) (habitCnt.toFloat() / totalHabits).coerceIn(0f, 1f) else 0f

    val parsed = try { parseFmt.parse(date) } catch (_: Exception) { null }
    val hasRecord = steps > 0 || water > 0 || sleepMin > 0 || habitCnt > 0 || activityByDate[date]?.isRestDay == true
    val readiness = ((stepRatio + waterRatio + sleepRatio + habitRatio) / 4f).coerceIn(0f, 1f)

    TodayDayStripItem(
      date = date,
      dayShort = parsed?.let { shortDayFmt.format(it).take(3) } ?: date.takeLast(2),
      dayNumber = parsed?.let { numFmt.format(it) } ?: date.takeLast(2),
      fullLabel = if (isToday) "Today" else (parsed?.let { fullFmt.format(it) } ?: date),
      isToday = isToday,
      readinessRatio = readiness,
      hasAnyRecord = hasRecord
    )
  }
}

fun computeTodayReadiness(
  stepProgress: Float,
  waterProgress: Float,
  sleepProgress: Float,
  habitProgress: Float,
  isRestDay: Boolean,
  waterRemainingMl: Int,
  stepsRemaining: Int,
  sleepMinutes: Int,
  stripItems: List<TodayDayStripItem>
): TodayReadinessSummary {
  var streak = 0
  for (item in stripItems.asReversed()) {
    if (item.hasAnyRecord) streak += 1
    else if (!item.isToday) break
  }

  val rawScore = if (isRestDay) {
    ((waterProgress * 0.35f + sleepProgress * 0.40f + habitProgress * 0.25f) * 100f).roundToInt().coerceIn(0, 100)
  } else {
    (((stepProgress + waterProgress + sleepProgress + habitProgress) / 4f) * 100f).roundToInt().coerceIn(0, 100)
  }

  val statusLabel = when {
    isRestDay -> "REST DAY ACTIVE"
    rawScore >= 85 -> "OPTIMAL BALANCE"
    rawScore >= 55 -> "STEADY PACE"
    rawScore > 0 -> "WARMING UP"
    else -> "READY TO START"
  }

  val insight = when {
    isRestDay -> "Rest day active. Focus on light hydration and sleep tonight."
    rawScore == 0 -> "Log your first glass of water"
    waterRemainingMl >= 250 -> "You're ${waterRemainingMl} ml behind on water. A glass now keeps you on track."
    stepsRemaining >= 500 -> "${formatStepCount(stepsRemaining)} steps left to hit today's movement goal."
    sleepMinutes <= 0 -> "Log last night's sleep to complete today's balance."
    habitProgress < 1f -> "Finish your remaining habits to round out the day."
    else -> "All four pillars balanced today. Nicely paced."
  }

  return TodayReadinessSummary(
    scorePercent = rawScore,
    statusLabel = statusLabel,
    coachingInsight = insight,
    streakDays = streak,
    isHighReadiness = rawScore >= 75 || isRestDay
  )
}

fun pickFeaturedExercise(isRestDay: Boolean, rotationOffset: Int): Exercise {
  val pool = if (isRestDay) {
    ExerciseDatabase.getExercisesByTier(ExerciseTier.BEGINNER)
  } else {
    ExerciseDatabase.exercises
  }
  if (pool.isEmpty()) return ExerciseDatabase.exercises.first()
  val idx = ((rotationOffset % pool.size) + pool.size) % pool.size
  return pool[idx]
}

internal fun formatStepCount(steps: Int): String {
  return String.format(Locale.US, "%,d", steps)
}

internal fun formatHeaderDate(dateString: String): String {
  return try {
    val inFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val outFmt = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
    val parsed = inFmt.parse(dateString)
    if (parsed != null) outFmt.format(parsed) else outFmt.format(Date())
  } catch (_: Exception) {
    SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(Date())
  }
}

internal fun formatLitresOrMl(ml: Int): String {
  return if (ml >= 1000) {
    String.format(Locale.US, "%.1f L", ml / 1000f)
  } else {
    "$ml ml"
  }
}

internal fun formatSleepDuration(minutes: Int?): String {
  if (minutes == null || minutes <= 0) return "—"
  val h = minutes / 60
  val m = minutes % 60
  return if (m == 0) "${h}h" else "${h}h ${m}m"
}

// ============================================================================
// TodayScreen (Step 2: Calm Header + Hero Ring + Breakdown Sheet + Base Sheet)
// ============================================================================

@Composable
fun TodayScreen(
  uiState: WellnessUiState,
  viewModel: WellnessViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  val haptic = LocalHapticFeedback.current
  val colors = MaterialTheme.kinetiqColors
  val listState = rememberLazyListState()

  val reduceMotion = rememberReduceMotionEnabled()

  var showBreakdownSheet by remember { mutableStateOf(false) }
  var showLogSheet by remember { mutableStateOf(false) }
  var showWaterLogsSheet by remember { mutableStateOf(false) }
  var showCustomWaterDialog by remember { mutableStateOf(false) }
  var showCustomStepsDialog by remember { mutableStateOf(false) }
  var showLogSleepDialog by remember { mutableStateOf(false) }
  var showAddHabitDialog by remember { mutableStateOf(false) }
  var isBreathingActive by remember { mutableStateOf(false) }
  var dismissedReminderIds by remember { mutableStateOf(setOf<String>()) }

  val isScrolledDown by remember {
    derivedStateOf {
      listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 72
    }
  }

  val stepPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) {
    viewModel.onStepPermissionsResult()
  }

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        viewModel.refreshStepTrackerState(autoStartIfPermitted = true)
      } else if (event == Lifecycle.Event.ON_STOP) {
        isBreathingActive = false
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      isBreathingActive = false
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  val targetSteps = uiState.userProfile.targetSteps.coerceAtLeast(1)
  val targetWater = uiState.userProfile.targetWaterMl.coerceAtLeast(1)
  val targetSleepHours = uiState.userProfile.targetSleepHours.coerceAtLeast(1f)
  val totalHabits = uiState.activeHabits.size

  val currentSteps = uiState.todayActivity?.stepCount ?: 0
  val isRestDay = uiState.todayActivity?.isRestDay ?: false
  val currentWaterMl = uiState.todayWaterTotalMl
  val sleepMinutes = uiState.todaySleep?.durationMinutes ?: 0
  val completedHabitIds = remember(uiState.todayHabitLogs) {
    uiState.todayHabitLogs.map { it.habitId }.toSet()
  }
  val completedHabits = uiState.activeHabits.count { completedHabitIds.contains(it.id) }

  val stepProgress = (currentSteps.toFloat() / targetSteps).coerceIn(0f, 1f)
  val waterProgress = (currentWaterMl.toFloat() / targetWater).coerceIn(0f, 1f)
  val sleepProgress = ((sleepMinutes / 60f) / targetSleepHours).coerceIn(0f, 1f)
  val habitProgress = if (totalHabits > 0) (completedHabits.toFloat() / totalHabits).coerceIn(0f, 1f) else 0f

  val stripItems = remember(
    uiState.pastWeekDates,
    uiState.todayDate,
    targetSteps,
    targetWater,
    targetSleepHours,
    totalHabits,
    currentSteps,
    currentWaterMl,
    sleepMinutes,
    uiState.todayHabitLogs,
    uiState.pastWeekActivities,
    uiState.pastWeekHydration,
    uiState.pastWeekSleep,
    uiState.pastWeekHabitLogs
  ) {
    buildSevenDayStrip(
      pastWeekDates = uiState.pastWeekDates,
      todayDate = uiState.todayDate,
      targetSteps = targetSteps,
      targetWaterMl = targetWater,
      targetSleepHours = targetSleepHours,
      totalHabits = totalHabits,
      todaySteps = currentSteps,
      todayWaterMl = currentWaterMl,
      todaySleepMinutes = sleepMinutes,
      todayHabitCount = uiState.todayHabitLogs.size,
      pastWeekActivities = uiState.pastWeekActivities,
      pastWeekHydration = uiState.pastWeekHydration,
      pastWeekSleep = uiState.pastWeekSleep,
      pastWeekHabitLogs = uiState.pastWeekHabitLogs
    )
  }

  val waterRemaining = (targetWater - currentWaterMl).coerceAtLeast(0)
  val stepsRemaining = (targetSteps - currentSteps).coerceAtLeast(0)

  val balanceSummary = remember(
    stepProgress,
    waterProgress,
    sleepProgress,
    habitProgress,
    isRestDay,
    waterRemaining,
    stepsRemaining,
    sleepMinutes,
    stripItems
  ) {
    computeTodayReadiness(
      stepProgress = stepProgress,
      waterProgress = waterProgress,
      sleepProgress = sleepProgress,
      habitProgress = habitProgress,
      isRestDay = isRestDay,
      waterRemainingMl = waterRemaining,
      stepsRemaining = stepsRemaining,
      sleepMinutes = sleepMinutes,
      stripItems = stripItems
    )
  }

  val headerDate = remember(uiState.todayDate) { formatHeaderDate(uiState.todayDate) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(colors.background),
    contentAlignment = Alignment.TopCenter
  ) {
    LazyColumn(
      state = listState,
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 680.dp),
      contentPadding = PaddingValues(bottom = 96.dp)
    ) {
      // 1. Calm Header: "Today" + date, Streak chip on right
      item(key = "today_header") {
        TodayCalmHeader(
          dateLabel = headerDate,
          streakDays = balanceSummary.streakDays,
          modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
      }

      // 2. Hero: 4-Segment Daily Balance Ring (0-100) + Single Human Insight Sentence
      item(key = "today_hero") {
        TodayBalanceHero(
          score = balanceSummary.scorePercent,
          insightSentence = balanceSummary.coachingInsight,
          stepProgress = stepProgress,
          waterProgress = waterProgress,
          sleepProgress = sleepProgress,
          habitProgress = habitProgress,
          reduceMotion = reduceMotion,
          onRingClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            showBreakdownSheet = true
          },
          modifier = Modifier
            .fillMaxWidth()
            .zIndex(-1f)
            .graphicsLayer {
              if (!reduceMotion && listState.firstVisibleItemIndex <= 1) {
                val offsetPx = if (listState.firstVisibleItemIndex == 0) {
                  listState.firstVisibleItemScrollOffset.toFloat() * 0.25f
                } else {
                  listState.firstVisibleItemScrollOffset.toFloat() * 0.35f + 24f
                }
                translationY = offsetPx
                alpha = (1f - (offsetPx / 420f)).coerceIn(0.25f, 1f)
              }
            }
            .padding(horizontal = 20.dp)
            .padding(top = 4.dp, bottom = 24.dp)
        )
      }

      // 3. Layered Surface-1 Rounded Sheet (28dp top corners) scrolling up over the hero
      item(key = "today_sheet_container") {
        Surface(
          shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
          color = colors.surface1,
          modifier = Modifier
            .fillMaxWidth()
            .zIndex(1f)
            .heightIn(min = 520.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
          ) {
            // 4. Asymmetric Progress Bento
            CalmSectionReveal(index = 0, reduceMotion = reduceMotion) {
              AsymmetricPillarsBento(
                currentWaterMl = currentWaterMl,
                targetWaterMl = targetWater,
                waterProgress = waterProgress,
                currentSteps = currentSteps,
                targetSteps = targetSteps,
                stepProgress = stepProgress,
                hasActivityRecord = uiState.todayActivity != null,
                stepTrackerStatus = uiState.stepTrackerStatus,
                sleepMinutes = uiState.todaySleep?.durationMinutes,
                targetSleepHours = targetSleepHours,
                sleepProgress = sleepProgress,
                reduceMotion = reduceMotion,
                onQuickAddWater = { amount, container ->
                  haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                  viewModel.logWater(amount, container)
                },
                onOpenWaterLogs = { showWaterLogsSheet = true },
                onOpenCustomWater = {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  showCustomWaterDialog = true
                },
                onStepsClick = {
                  if (uiState.stepTrackerStatus == StepTrackerStatus.PERMISSION_REQUIRED) {
                    val perms = StepPermissionHelper.requiredRuntimePermissions()
                    if (perms.isNotEmpty()) stepPermissionLauncher.launch(perms)
                    else showCustomStepsDialog = true
                  } else {
                    showCustomStepsDialog = true
                  }
                },
                onSleepClick = { showLogSleepDialog = true }
              )
            }

            // 5. Flat Habits Checklist (7-day dot strip + morphing check + haptic)
            CalmSectionReveal(index = 1, reduceMotion = reduceMotion) {
              FlatHabitsChecklist(
                habits = uiState.activeHabits,
                completedHabitIds = completedHabitIds,
                pastWeekDates = uiState.pastWeekDates,
                todayDate = uiState.todayDate,
                pastWeekHabitLogs = uiState.pastWeekHabitLogs,
                todayHabitLogs = uiState.todayHabitLogs,
                reduceMotion = reduceMotion,
                onToggleHabit = { habitId ->
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  viewModel.toggleHabit(habitId)
                },
                onDeleteHabit = { habitId ->
                  haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                  viewModel.deleteHabit(habitId)
                }
              )
            }

            // 6. Breathing: Collapsed one-row ("1-minute reset" + Start) -> expands inline 4s/6s
            CalmSectionReveal(index = 2, reduceMotion = reduceMotion) {
              OneMinuteResetBreathing(
                isActive = isBreathingActive,
                reduceMotion = reduceMotion,
                onToggle = {
                  haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                  isBreathingActive = !isBreathingActive
                },
                onComplete = {
                  isBreathingActive = false
                }
              )
            }

            // 7. Gentle Reminders (slim dismissible rows, no header card)
            val reminders = remember(waterRemaining, stepsRemaining, sleepMinutes, isRestDay, dismissedReminderIds, colors) {
              buildGentleReminders(
                colors = colors,
                waterRemainingMl = waterRemaining,
                stepsRemaining = stepsRemaining,
                sleepMinutes = sleepMinutes,
                isRestDay = isRestDay
              ).filterNot { it.id in dismissedReminderIds }
            }
            if (reminders.isNotEmpty()) {
              CalmSectionReveal(index = 3, reduceMotion = reduceMotion) {
                GentleRemindersList(
                  reminders = reminders,
                  onDismissReminder = { id ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    dismissedReminderIds = dismissedReminderIds + id
                  }
                )
              }
            }

            CalmSectionReveal(index = 4, reduceMotion = reduceMotion) {
              MedicalDisclaimerBanner()
            }
          }
        }
      }
    }

    // 8. Extended FAB "Log" (bottom-end above nav, collapses on scroll)
    ExtendedFloatingActionButton(
      onClick = {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        showLogSheet = true
      },
      expanded = !isScrolledDown,
      icon = {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "Log entry"
        )
      },
      text = {
        Text(
          text = "Log",
          style = MaterialTheme.typography.labelLarge
        )
      },
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 20.dp, bottom = 16.dp)
        .heightIn(min = 48.dp)
        .testTag("today_log_fab")
    )
  }

  if (showLogSheet) {
    LogPickerBottomSheet(
      onPickWater = {
        showLogSheet = false
        showCustomWaterDialog = true
      },
      onPickSteps = {
        showLogSheet = false
        if (uiState.stepTrackerStatus == StepTrackerStatus.PERMISSION_REQUIRED) {
          val perms = StepPermissionHelper.requiredRuntimePermissions()
          if (perms.isNotEmpty()) stepPermissionLauncher.launch(perms)
          else showCustomStepsDialog = true
        } else {
          showCustomStepsDialog = true
        }
      },
      onPickSleep = {
        showLogSheet = false
        showLogSleepDialog = true
      },
      onPickHabit = {
        showLogSheet = false
        showAddHabitDialog = true
      },
      onDismiss = { showLogSheet = false }
    )
  }

  if (showWaterLogsSheet) {
    WaterLogsModalSheet(
      currentWaterMl = currentWaterMl,
      targetWaterMl = targetWater,
      waterLogs = uiState.todayWaterLogs,
      onDeleteLog = {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        viewModel.deleteWaterLog(it)
      },
      onAddCustom = {
        showWaterLogsSheet = false
        showCustomWaterDialog = true
      },
      onDismiss = { showWaterLogsSheet = false }
    )
  }

  if (showCustomWaterDialog) {
    CustomWaterInputDialog(
      onDismiss = { showCustomWaterDialog = false },
      onConfirm = { amountMl, container ->
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        viewModel.logWater(amountMl, container)
        showCustomWaterDialog = false
      }
    )
  }

  if (showCustomStepsDialog) {
    CustomStepsInputDialog(
      stepTrackerStatus = uiState.stepTrackerStatus,
      onToggleSensor = {
        if (uiState.stepTrackerStatus == StepTrackerStatus.ACTIVE) viewModel.pauseStepTracking()
        else viewModel.startStepTracking()
      },
      onDismiss = { showCustomStepsDialog = false },
      onConfirm = { steps ->
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        viewModel.logSteps(steps)
        showCustomStepsDialog = false
      }
    )
  }

  if (showLogSleepDialog) {
    CalmSleepDialog(
      onDismiss = { showLogSleepDialog = false },
      onConfirm = { bedtime, wake, rating, notes ->
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        viewModel.logSleep(bedtime, wake, rating, notes)
        showLogSleepDialog = false
      }
    )
  }

  if (showAddHabitDialog) {
    CalmAddHabitDialog(
      onDismiss = { showAddHabitDialog = false },
      onConfirm = { title, category, icon ->
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        viewModel.addCustomHabit(title, category, icon)
        showAddHabitDialog = false
      }
    )
  }

  if (showBreakdownSheet) {
    DailyBalanceBreakdownSheet(
      score = balanceSummary.scorePercent,
      isRestDay = isRestDay,
      currentSteps = currentSteps,
      targetSteps = targetSteps,
      stepProgress = stepProgress,
      currentWaterMl = currentWaterMl,
      targetWaterMl = targetWater,
      waterProgress = waterProgress,
      sleepMinutes = sleepMinutes,
      targetSleepHours = targetSleepHours,
      sleepProgress = sleepProgress,
      completedHabits = completedHabits,
      totalHabits = totalHabits,
      habitProgress = habitProgress,
      stepTrackerStatus = uiState.stepTrackerStatus,
      onToggleRestDay = {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        viewModel.setRestDay(!isRestDay)
      },
      onToggleStepSensor = {
        if (uiState.stepTrackerStatus == StepTrackerStatus.PERMISSION_REQUIRED) {
          val perms = StepPermissionHelper.requiredRuntimePermissions()
          if (perms.isNotEmpty()) stepPermissionLauncher.launch(perms)
        } else if (uiState.stepTrackerStatus == StepTrackerStatus.ACTIVE) {
          viewModel.pauseStepTracking()
        } else {
          viewModel.startStepTracking()
        }
      },
      onOpenSettings = {
        val intent = Intent(
          Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
          Uri.fromParts("package", context.packageName, null)
        )
        context.startActivity(intent)
      },
      onDismiss = { showBreakdownSheet = false }
    )
  }
}

@Composable
private fun TodayCalmHeader(
  dateLabel: String,
  streakDays: Int,
  modifier: Modifier = Modifier
) {
  val colors = MaterialTheme.kinetiqColors
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(
        text = "Today",
        style = MaterialTheme.typography.titleLarge,
        color = colors.textPrimary,
        modifier = Modifier.testTag("today_greeting_title")
      )
      Text(
        text = dateLabel,
        style = MaterialTheme.typography.labelMedium,
        color = colors.textSecondary
      )
    }

    Surface(
      shape = RoundedCornerShape(50),
      color = colors.surface2,
      modifier = Modifier
        .heightIn(min = 36.dp)
        .semantics { contentDescription = "Current streak, $streakDays days" }
        .testTag("today_streak_badge")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
      ) {
        Icon(
          imageVector = Icons.Default.LocalFireDepartment,
          contentDescription = null,
          tint = colors.habits,
          modifier = Modifier.size(16.dp)
        )
        Text(
          text = "$streakDays",
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
          color = colors.textPrimary
        )
      }
    }
  }
}

@Composable
private fun TodayBalanceHero(
  score: Int,
  insightSentence: String,
  stepProgress: Float,
  waterProgress: Float,
  sleepProgress: Float,
  habitProgress: Float,
  reduceMotion: Boolean,
  onRingClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = MaterialTheme.kinetiqColors
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val pressScale by animateFloatAsState(
    targetValue = if (isPressed && !reduceMotion) 0.97f else 1f,
    animationSpec = if (reduceMotion) tween(0) else spring(dampingRatio = 0.8f),
    label = "hero_press_scale"
  )

  // Sweep once on mount (900ms), subsequent updates spring cleanly (damping 0.8)
  val mountSweep = remember { Animatable(if (reduceMotion) 1f else 0f) }
  LaunchedEffect(reduceMotion) {
    if (reduceMotion) {
      mountSweep.snapTo(1f)
    } else {
      mountSweep.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 900, easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f))
      )
    }
  }

  val animSteps by animateFloatAsState(
    targetValue = stepProgress.coerceIn(0f, 1f),
    animationSpec = if (reduceMotion) tween(0) else spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
    label = "hero_steps"
  )
  val animWater by animateFloatAsState(
    targetValue = waterProgress.coerceIn(0f, 1f),
    animationSpec = if (reduceMotion) tween(0) else spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
    label = "hero_water"
  )
  val animSleep by animateFloatAsState(
    targetValue = sleepProgress.coerceIn(0f, 1f),
    animationSpec = if (reduceMotion) tween(0) else spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
    label = "hero_sleep"
  )
  val animHabits by animateFloatAsState(
    targetValue = habitProgress.coerceIn(0f, 1f),
    animationSpec = if (reduceMotion) tween(0) else spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
    label = "hero_habits"
  )

  val displayScore = (score * mountSweep.value).roundToInt()

  val ringDescription =
    "Daily balance, $score out of 100. Steps ${(stepProgress * 100).roundToInt()} percent, " +
      "Water ${(waterProgress * 100).roundToInt()} percent, Sleep ${(sleepProgress * 100).roundToInt()} percent, " +
      "Habits ${(habitProgress * 100).roundToInt()} percent. Double tap for breakdown."

  val fontScale = LocalDensity.current.fontScale
  val ringDiameter = remember(fontScale) {
    val extra = (fontScale - 1f).coerceAtLeast(0f) * 0.45f
    (228.dp * (1f + extra)).coerceAtMost(320.dp)
  }

  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(ringDiameter)
        .scale(pressScale)
        .clip(CircleShape)
        .clickable(
          interactionSource = interactionSource,
          indication = null,
          onClick = onRingClick
        )
        .semantics { contentDescription = ringDescription }
        .testTag("hero_readiness_card")
    ) {
      Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        val strokePx = 10.dp.toPx()
        val gapDegrees = 10f
        val segmentArc = 90f - gapDegrees // 80 degrees per pillar
        val sweepFactor = mountSweep.value

        val segments = listOf(
          Triple(-85f, colors.steps, animSteps),
          Triple(5f, colors.water, animWater),
          Triple(95f, colors.sleep, animSleep),
          Triple(185f, colors.habits, animHabits)
        )

        segments.forEach { (startAngle, pillarColor, progress) ->
          drawArc(
            color = pillarColor.copy(alpha = 0.12f),
            startAngle = startAngle,
            sweepAngle = segmentArc,
            useCenter = false,
            style = Stroke(width = strokePx, cap = StrokeCap.Round)
          )
          val activeSweep = segmentArc * progress * sweepFactor
          if (activeSweep > 0.5f) {
            drawArc(
              color = pillarColor,
              startAngle = startAngle,
              sweepAngle = activeSweep,
              useCenter = false,
              style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
          }
        }
      }

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 24.dp)
      ) {
        Text(
          text = "Daily balance",
          style = MaterialTheme.typography.labelMedium,
          color = colors.textSecondary
        )
        Text(
          text = "$displayScore",
          style = KinetiqHeroScoreStyle,
          color = colors.textPrimary,
          maxLines = 1
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
      text = insightSentence,
      style = MaterialTheme.typography.bodyMedium,
      color = colors.textSecondary,
      textAlign = TextAlign.Center,
      modifier = Modifier
        .widthIn(max = 320.dp)
        .padding(horizontal = 8.dp)
    )
  }
}

@Composable
private fun DailyBalanceBreakdownSheet(
  score: Int,
  isRestDay: Boolean,
  currentSteps: Int,
  targetSteps: Int,
  stepProgress: Float,
  currentWaterMl: Int,
  targetWaterMl: Int,
  waterProgress: Float,
  sleepMinutes: Int,
  targetSleepHours: Float,
  sleepProgress: Float,
  completedHabits: Int,
  totalHabits: Int,
  habitProgress: Float,
  stepTrackerStatus: StepTrackerStatus,
  onToggleRestDay: () -> Unit,
  onToggleStepSensor: () -> Unit,
  onOpenSettings: () -> Unit,
  onDismiss: () -> Unit
) {
  val colors = MaterialTheme.kinetiqColors
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = colors.surface1,
    contentColor = colors.textPrimary
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 28.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Daily balance breakdown",
            style = MaterialTheme.typography.titleLarge,
            color = colors.textPrimary
          )
          Text(
            text = "Combined from today's four pillars",
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary
          )
        }
        Text(
          text = "$score%",
          style = KinetiqValueTextStyle.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.primary
        )
      }

      HorizontalDivider(color = colors.divider)

      BreakdownPillarRow(
        icon = Icons.Default.DirectionsWalk,
        title = "Steps",
        valueText = if (currentSteps > 0) "${formatStepCount(currentSteps)} / ${formatStepCount(targetSteps)}" else "— / ${formatStepCount(targetSteps)}",
        percent = (stepProgress * 100).roundToInt(),
        color = colors.steps
      )
      BreakdownPillarRow(
        icon = Icons.Default.WaterDrop,
        title = "Water",
        valueText = "${formatLitresOrMl(currentWaterMl)} / ${formatLitresOrMl(targetWaterMl)}",
        percent = (waterProgress * 100).roundToInt(),
        color = colors.water
      )
      BreakdownPillarRow(
        icon = Icons.Default.Bedtime,
        title = "Sleep",
        valueText = "${formatSleepDuration(sleepMinutes)} / ${String.format(Locale.US, "%.0fh", targetSleepHours)}",
        percent = (sleepProgress * 100).roundToInt(),
        color = colors.sleep
      )
      BreakdownPillarRow(
        icon = Icons.Default.SelfImprovement,
        title = "Habits",
        valueText = if (totalHabits > 0) "$completedHabits / $totalHabits" else "—",
        percent = (habitProgress * 100).roundToInt(),
        color = colors.habits
      )

      HorizontalDivider(color = colors.divider)

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 48.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(colors.surface2)
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Rest day",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = colors.textPrimary
          )
          Text(
            text = "Weights hydration and sleep while pausing step pressure",
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
          checked = isRestDay,
          onCheckedChange = { onToggleRestDay() }
        )
      }

      if (stepTrackerStatus != StepTrackerStatus.SENSOR_UNAVAILABLE) {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = colors.surface2,
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
              if (stepTrackerStatus == StepTrackerStatus.PERMISSION_REQUIRED) onOpenSettings()
              onToggleStepSensor()
            }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = when (stepTrackerStatus) {
                StepTrackerStatus.ACTIVE -> "Live step counter active"
                StepTrackerStatus.PAUSED -> "Live step counter paused"
                StepTrackerStatus.PERMISSION_REQUIRED -> "Allow activity permission for live steps"
                StepTrackerStatus.SENSOR_UNAVAILABLE -> "No hardware step counter"
              },
              style = MaterialTheme.typography.bodyMedium,
              color = colors.textPrimary,
              modifier = Modifier.weight(1f)
            )
            Text(
              text = when (stepTrackerStatus) {
                StepTrackerStatus.ACTIVE -> "Pause"
                StepTrackerStatus.PAUSED -> "Resume"
                else -> "Enable"
              },
              style = MaterialTheme.typography.labelLarge,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    }
  }
}

@Composable
private fun BreakdownPillarRow(
  icon: ImageVector,
  title: String,
  valueText: String,
  percent: Int,
  color: Color
) {
  val colors = MaterialTheme.kinetiqColors
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 2.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.weight(1f)
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .background(color.copy(alpha = 0.14f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = color,
          modifier = Modifier.size(17.dp)
        )
      }
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
          color = colors.textPrimary
        )
        Text(
          text = valueText,
          style = MaterialTheme.typography.labelMedium,
          color = colors.textSecondary
        )
      }
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      if (percent >= 100) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = "Goal reached",
          tint = color,
          modifier = Modifier.size(16.dp)
        )
      }
      Text(
        text = "$percent%",
        style = KinetiqValueTextStyle.copy(fontSize = 15.sp),
        color = color
      )
    }
  }
}

// ============================================================================
// Step 3: Asymmetric Progress Bento (Tall Hydration + Stacked Steps & Sleep)
// ============================================================================

@Composable
private fun AsymmetricPillarsBento(
  currentWaterMl: Int,
  targetWaterMl: Int,
  waterProgress: Float,
  currentSteps: Int,
  targetSteps: Int,
  stepProgress: Float,
  hasActivityRecord: Boolean,
  stepTrackerStatus: StepTrackerStatus,
  sleepMinutes: Int?,
  targetSleepHours: Float,
  sleepProgress: Float,
  reduceMotion: Boolean,
  onQuickAddWater: (Int, String) -> Unit,
  onOpenWaterLogs: () -> Unit,
  onOpenCustomWater: () -> Unit,
  onStepsClick: () -> Unit,
  onSleepClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = MaterialTheme.kinetiqColors
  val progressSpec = if (reduceMotion) tween<Float>(0) else spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow)
  val animWater by animateFloatAsState(
    targetValue = waterProgress.coerceIn(0f, 1f),
    animationSpec = progressSpec,
    label = "bento_water"
  )
  val animSteps by animateFloatAsState(
    targetValue = stepProgress.coerceIn(0f, 1f),
    animationSpec = progressSpec,
    label = "bento_steps"
  )
  val animSleep by animateFloatAsState(
    targetValue = sleepProgress.coerceIn(0f, 1f),
    animationSpec = progressSpec,
    label = "bento_sleep"
  )

  val waterPct = (waterProgress * 100).roundToInt()
  val stepsPct = (stepProgress * 100).roundToInt()
  val sleepPct = (sleepProgress * 100).roundToInt()

  val waterInteraction = remember { MutableInteractionSource() }
  val waterPressed by waterInteraction.collectIsPressedAsState()
  val waterScale by animateFloatAsState(
    targetValue = if (waterPressed) 0.97f else 1f,
    animationSpec = spring(dampingRatio = 0.8f),
    label = "water_tile_scale"
  )

  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(IntrinsicSize.Max),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Left Column: Tall Hydration Tile (liquid fill + +250/+500 ml + long-press custom)
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = colors.surface2,
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight()
        .scale(waterScale)
        .clip(RoundedCornerShape(24.dp))
        .combinedClickable(
          interactionSource = waterInteraction,
          indication = null,
          onClick = onOpenWaterLogs,
          onLongClick = onOpenCustomWater
        )
        .semantics {
          contentDescription = "Water, ${formatLitresOrMl(currentWaterMl)} of ${formatLitresOrMl(targetWaterMl)}, $waterPct percent. Tap for logs, long press for custom."
        }
        .testTag("hydration_bento_tile")
    ) {
      Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          if (animWater > 0.01f) {
            val fillHeight = size.height * (animWater * 0.82f)
            val topY = size.height - fillHeight
            drawRect(
              color = colors.water.copy(alpha = 0.14f),
              topLeft = Offset(0f, topY),
              size = Size(size.width, fillHeight)
            )
          }
        }

        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          verticalArrangement = Arrangement.SpaceBetween
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.WaterDrop,
                  contentDescription = null,
                  tint = colors.water,
                  modifier = Modifier.size(18.dp)
                )
                Text(
                  text = "Water",
                  style = MaterialTheme.typography.labelMedium,
                  color = colors.textSecondary
                )
              }

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
              ) {
                if (waterPct >= 100) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Goal reached",
                    tint = colors.water,
                    modifier = Modifier.size(14.dp)
                  )
                }
                Text(
                  text = "$waterPct%",
                  style = MaterialTheme.typography.labelMedium,
                  color = colors.water
                )
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = if (currentWaterMl > 0) formatLitresOrMl(currentWaterMl) else "—",
              style = KinetiqValueTextStyle.copy(fontSize = 24.sp),
              color = colors.textPrimary
            )
            Text(
              text = "of ${formatLitresOrMl(targetWaterMl)}",
              style = MaterialTheme.typography.labelMedium,
              color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))
            PillarThinProgressLine(progress = animWater, color = colors.water)
          }

          Spacer(modifier = Modifier.height(16.dp))

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              TonalWaterQuickButton(
                label = "+250",
                accent = colors.water,
                onClick = { onQuickAddWater(250, "Glass") },
                modifier = Modifier
                  .weight(1f)
                  .testTag("quick_water_250_button")
              )
              TonalWaterQuickButton(
                label = "+500",
                accent = colors.water,
                onClick = { onQuickAddWater(500, "Flask") },
                modifier = Modifier
                  .weight(1f)
                  .testTag("quick_water_500_button")
              )
            }
            Text(
              text = "Hold tile for custom",
              style = MaterialTheme.typography.labelSmall,
              color = colors.textSecondary,
              modifier = Modifier.fillMaxWidth(),
              textAlign = TextAlign.Center
            )
          }
        }
      }
    }

    // Right Column: Two Smaller Stacked Tiles (Steps + Sleep)
    Column(
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight(),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      val stepsValue = if (currentSteps > 0 || hasActivityRecord) formatStepCount(currentSteps) else "—"
      val stepsSubtitle = if (stepTrackerStatus == StepTrackerStatus.PERMISSION_REQUIRED && currentSteps == 0) {
        "Tap to enable sensor"
      } else {
        "of ${formatStepCount(targetSteps)} · $stepsPct%"
      }

      CompactPillarTile(
        icon = Icons.Default.DirectionsWalk,
        label = "Steps",
        valueText = stepsValue,
        goalText = stepsSubtitle,
        progress = animSteps,
        isGoalReached = stepsPct >= 100,
        accent = colors.steps,
        contentDesc = "Steps, $currentSteps of $targetSteps, $stepsPct percent.",
        onClick = onStepsClick,
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .testTag("steps_bento_tile")
      )

      val sleepValue = formatSleepDuration(sleepMinutes)
      val sleepSubtitle = if (sleepMinutes != null && sleepMinutes > 0) {
        "of ${String.format(Locale.US, "%.0fh", targetSleepHours)} · $sleepPct%"
      } else {
        "Tap to log sleep"
      }

      CompactPillarTile(
        icon = Icons.Default.Bedtime,
        label = "Sleep",
        valueText = sleepValue,
        goalText = sleepSubtitle,
        progress = animSleep,
        isGoalReached = sleepPct >= 100,
        accent = colors.sleep,
        contentDesc = "Sleep, ${if (sleepMinutes != null && sleepMinutes > 0) sleepValue else "not logged"} of ${targetSleepHours.toInt()} hours.",
        onClick = onSleepClick,
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .testTag("sleep_bento_tile")
      )
    }
  }
}

@Composable
private fun CompactPillarTile(
  icon: ImageVector,
  label: String,
  valueText: String,
  goalText: String,
  progress: Float,
  isGoalReached: Boolean,
  accent: Color,
  contentDesc: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = MaterialTheme.kinetiqColors
  val interaction = remember { MutableInteractionSource() }
  val pressed by interaction.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (pressed) 0.97f else 1f,
    animationSpec = spring(dampingRatio = 0.8f),
    label = "compact_tile_scale"
  )

  Surface(
    shape = RoundedCornerShape(24.dp),
    color = colors.surface2,
    modifier = modifier
      .scale(scale)
      .clip(RoundedCornerShape(24.dp))
      .clickable(interactionSource = interaction, indication = null, onClick = onClick)
      .semantics { contentDescription = contentDesc }
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(17.dp)
          )
          Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary
          )
        }
        if (isGoalReached) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Goal reached",
            tint = accent,
            modifier = Modifier.size(15.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = valueText,
          style = KinetiqValueTextStyle,
          color = colors.textPrimary
        )
        Text(
          text = goalText,
          style = MaterialTheme.typography.labelMedium,
          color = colors.textSecondary
        )
      }

      Spacer(modifier = Modifier.height(8.dp))
      PillarThinProgressLine(progress = progress, color = accent)
    }
  }
}

@Composable
private fun PillarThinProgressLine(
  progress: Float,
  color: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(4.dp)
      .clip(RoundedCornerShape(50))
      .background(color.copy(alpha = 0.12f))
  ) {
    if (progress > 0.005f) {
      Box(
        modifier = Modifier
          .fillMaxWidth(progress.coerceIn(0.02f, 1f))
          .height(4.dp)
          .clip(RoundedCornerShape(50))
          .background(color)
      )
    }
  }
}

@Composable
private fun TonalWaterQuickButton(
  label: String,
  accent: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = MaterialTheme.kinetiqColors
  val interaction = remember { MutableInteractionSource() }
  val pressed by interaction.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (pressed) 0.97f else 1f,
    animationSpec = spring(dampingRatio = 0.8f),
    label = "water_btn_scale"
  )

  Surface(
    shape = RoundedCornerShape(50),
    color = colors.surface3,
    modifier = modifier
      .heightIn(min = 48.dp)
      .scale(scale)
      .clip(RoundedCornerShape(50))
      .clickable(interactionSource = interaction, indication = null, onClick = onClick)
  ) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
        color = accent,
        maxLines = 1
      )
    }
  }
}

@Composable
private fun WaterLogsModalSheet(
  currentWaterMl: Int,
  targetWaterMl: Int,
  waterLogs: List<HydrationRecord>,
  onDeleteLog: (Long) -> Unit,
  onAddCustom: () -> Unit,
  onDismiss: () -> Unit
) {
  val colors = MaterialTheme.kinetiqColors
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val timeFmt = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = colors.surface1,
    contentColor = colors.textPrimary
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 28.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Today's water",
            style = MaterialTheme.typography.titleLarge,
            color = colors.textPrimary
          )
          Text(
            text = "${formatLitresOrMl(currentWaterMl)} of ${formatLitresOrMl(targetWaterMl)}",
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary
          )
        }
        TextButton(
          onClick = onAddCustom,
          modifier = Modifier.heightIn(min = 48.dp)
        ) {
          Text("Custom amount", color = MaterialTheme.colorScheme.primary)
        }
      }

      HorizontalDivider(color = colors.divider)

      if (waterLogs.isEmpty()) {
        Text(
          text = "No water logged yet today.",
          style = MaterialTheme.typography.bodyMedium,
          color = colors.textSecondary,
          modifier = Modifier.padding(vertical = 12.dp)
        )
      } else {
        waterLogs.take(8).forEach { entry ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = null,
                tint = colors.water,
                modifier = Modifier.size(16.dp)
              )
              Column {
                Text(
                  text = "${entry.amountMl} ml · ${entry.containerType}",
                  style = MaterialTheme.typography.bodyMedium,
                  color = colors.textPrimary
                )
                Text(
                  text = timeFmt.format(Date(entry.timestamp)),
                  style = MaterialTheme.typography.labelMedium,
                  color = colors.textSecondary
                )
              }
            }

            IconButton(
              onClick = { onDeleteLog(entry.id) },
              modifier = Modifier.size(48.dp)
            ) {
              Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Delete water entry",
                tint = colors.textSecondary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
          HorizontalDivider(color = colors.divider)
        }
      }
    }
  }
}

@Composable
private fun CustomWaterInputDialog(
  onDismiss: () -> Unit,
  onConfirm: (Int, String) -> Unit
) {
  val colors = MaterialTheme.kinetiqColors
  var amountText by remember { mutableStateOf("350") }
  var container by remember { mutableStateOf("Bottle") }
  val containers = listOf("Glass", "Bottle", "Flask", "Mug")

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = colors.surface1,
    titleContentColor = colors.textPrimary,
    textContentColor = colors.textPrimary,
    title = {
      Text("Log water", style = MaterialTheme.typography.titleLarge)
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
          value = amountText,
          onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
          label = { Text("Amount (ml)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          containers.forEach { item ->
            val selected = item == container
            Surface(
              shape = RoundedCornerShape(50),
              color = if (selected) colors.water.copy(alpha = 0.2f) else colors.surface2,
              modifier = Modifier
                .weight(1f)
                .heightIn(min = 40.dp)
                .clip(RoundedCornerShape(50))
                .clickable { container = item }
            ) {
              Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 4.dp)) {
                Text(
                  text = item,
                  style = MaterialTheme.typography.labelMedium,
                  color = if (selected) colors.water else colors.textSecondary,
                  maxLines = 1
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val ml = amountText.toIntOrNull()?.coerceIn(25, 4000) ?: 250
          onConfirm(ml, container)
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier.heightIn(min = 48.dp)
      ) {
        Text("Add water")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) {
        Text("Cancel", color = colors.textSecondary)
      }
    }
  )
}

@Composable
private fun CustomStepsInputDialog(
  stepTrackerStatus: StepTrackerStatus,
  onToggleSensor: () -> Unit,
  onDismiss: () -> Unit,
  onConfirm: (Int) -> Unit
) {
  val colors = MaterialTheme.kinetiqColors
  var stepsText by remember { mutableStateOf("1000") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = colors.surface1,
    titleContentColor = colors.textPrimary,
    textContentColor = colors.textPrimary,
    title = {
      Text("Add steps", style = MaterialTheme.typography.titleLarge)
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
          value = stepsText,
          onValueChange = { stepsText = it.filter { ch -> ch.isDigit() } },
          label = { Text("Steps to add") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(500, 1000, 2500).forEach { preset ->
            Surface(
              shape = RoundedCornerShape(50),
              color = colors.surface2,
              modifier = Modifier
                .weight(1f)
                .heightIn(min = 40.dp)
                .clip(RoundedCornerShape(50))
                .clickable { stepsText = preset.toString() }
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = "+$preset",
                  style = MaterialTheme.typography.labelMedium,
                  color = colors.steps
                )
              }
            }
          }
        }
        if (stepTrackerStatus == StepTrackerStatus.ACTIVE || stepTrackerStatus == StepTrackerStatus.PAUSED) {
          HorizontalDivider(color = colors.divider)
          TextButton(
            onClick = onToggleSensor,
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(min = 48.dp)
          ) {
            Text(
              text = if (stepTrackerStatus == StepTrackerStatus.ACTIVE) "Pause live step counter" else "Resume live step counter",
              color = colors.textSecondary
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val steps = stepsText.toIntOrNull()?.coerceIn(1, 50000) ?: 500
          onConfirm(steps)
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier.heightIn(min = 48.dp)
      ) {
        Text("Add steps")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) {
        Text("Cancel", color = colors.textSecondary)
      }
    }
  )
}

@Composable
private fun CalmSleepDialog(
  onDismiss: () -> Unit,
  onConfirm: (Long, Long, Int, String) -> Unit
) {
  val colors = MaterialTheme.kinetiqColors
  var hoursText by remember { mutableStateOf("7.5") }
  var qualityRating by remember { mutableStateOf(4) }
  var notes by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = colors.surface1,
    titleContentColor = colors.textPrimary,
    textContentColor = colors.textPrimary,
    title = {
      Text("Log sleep", style = MaterialTheme.typography.titleLarge)
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
          value = hoursText,
          onValueChange = { hoursText = it.filter { ch -> ch.isDigit() || ch == '.' } },
          label = { Text("Hours slept") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        Text("Sleep quality", style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          for (rating in 1..5) {
            val selected = rating <= qualityRating
            Surface(
              shape = RoundedCornerShape(50),
              color = if (selected) colors.sleep.copy(alpha = 0.22f) else colors.surface2,
              modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp)
                .clip(RoundedCornerShape(50))
                .clickable { qualityRating = rating }
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = "$rating",
                  style = MaterialTheme.typography.labelLarge,
                  color = if (selected) colors.sleep else colors.textSecondary
                )
              }
            }
          }
        }
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Notes (optional)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val hours = hoursText.toFloatOrNull()?.coerceIn(0.5f, 16f) ?: 7.5f
          val durationMs = (hours * 3600f * 1000f).toLong()
          val wakeMs = System.currentTimeMillis()
          val bedMs = wakeMs - durationMs
          onConfirm(bedMs, wakeMs, qualityRating, notes.trim())
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier.heightIn(min = 48.dp)
      ) {
        Text("Save sleep")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) {
        Text("Cancel", color = colors.textSecondary)
      }
    }
  )
}

// ============================================================================
// Step 4 & 5: Habits (Flat + 7-day dots + Morph Check), Breathing (4s/6s),
// Reminders (Dismissible), Log FAB Sheet, Staggered Motion & Reduce-Motion
// ============================================================================

@Composable
private fun rememberReduceMotionEnabled(): Boolean {
  val context = LocalContext.current
  return remember(context) {
    try {
      Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f
      ) == 0f
    } catch (_: Exception) {
      false
    }
  }
}

@Composable
private fun CalmSectionReveal(
  index: Int,
  reduceMotion: Boolean,
  content: @Composable () -> Unit
) {
  if (reduceMotion) {
    content()
    return
  }

  val progress = remember { Animatable(0f) }
  LaunchedEffect(Unit) {
    if (index > 0) delay(index * 40L)
    progress.animateTo(
      targetValue = 1f,
      animationSpec = tween(
        durationMillis = 300,
        easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
      )
    )
  }

  Box(
    modifier = Modifier.graphicsLayer {
      alpha = progress.value
      translationY = (1f - progress.value) * 12.dp.toPx()
    }
  ) {
    content()
  }
}

@Composable
private fun FlatHabitsChecklist(
  habits: List<Habit>,
  completedHabitIds: Set<Long>,
  pastWeekDates: List<String>,
  todayDate: String,
  pastWeekHabitLogs: List<HabitLog>,
  todayHabitLogs: List<HabitLog>,
  reduceMotion: Boolean,
  onToggleHabit: (Long) -> Unit,
  onDeleteHabit: (Long) -> Unit
) {
  val colors = MaterialTheme.kinetiqColors
  val completedCount = habits.count { it.id in completedHabitIds }
  val total = habits.size

  val logsByHabitDate = remember(pastWeekHabitLogs, todayHabitLogs, todayDate) {
    val map = HashMap<Long, MutableSet<String>>()
    pastWeekHabitLogs.forEach { log ->
      if (log.date != todayDate) {
        map.getOrPut(log.habitId) { mutableSetOf() }.add(log.date)
      }
    }
    todayHabitLogs.forEach { log ->
      map.getOrPut(log.habitId) { mutableSetOf() }.add(todayDate)
    }
    map
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Habits",
        style = MaterialTheme.typography.titleLarge,
        color = colors.textPrimary
      )
      Text(
        text = if (total > 0) "$completedCount of $total" else "—",
        style = MaterialTheme.typography.labelMedium,
        color = colors.textSecondary
      )
    }

    if (habits.isEmpty()) {
      Text(
        text = "No habits yet. Tap Log to add your first daily habit.",
        style = MaterialTheme.typography.bodyMedium,
        color = colors.textSecondary,
        modifier = Modifier.padding(vertical = 8.dp)
      )
    } else {
      habits.forEachIndexed { idx, habit ->
        val isDone = habit.id in completedHabitIds
        val completedDates = logsByHabitDate[habit.id] ?: emptySet()
        val weekStatus = pastWeekDates.map { date ->
          if (date == todayDate) isDone else (date in completedDates)
        }

        val cornerRadius by animateDpAsState(
          targetValue = if (isDone) 6.dp else 13.dp,
          animationSpec = if (reduceMotion) tween(0) else spring(dampingRatio = 0.8f),
          label = "habit_check_morph"
        )
        val rowInteraction = remember { MutableInteractionSource() }
        val rowPressed by rowInteraction.collectIsPressedAsState()
        val rowScale by animateFloatAsState(
          targetValue = if (rowPressed && !reduceMotion) 0.97f else 1f,
          animationSpec = if (reduceMotion) tween(0) else spring(dampingRatio = 0.8f),
          label = "habit_row_scale"
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .scale(rowScale)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
              interactionSource = rowInteraction,
              indication = null
            ) { onToggleHabit(habit.id) }
            .padding(vertical = 10.dp)
            .semantics {
              contentDescription = "${habit.title}, ${if (isDone) "completed" else "not completed"}"
            },
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(cornerRadius))
                .background(if (isDone) colors.habits else Color.Transparent)
                .border(
                  width = 1.5.dp,
                  color = if (isDone) colors.habits else colors.textSecondary.copy(alpha = 0.6f),
                  shape = RoundedCornerShape(cornerRadius)
                ),
              contentAlignment = Alignment.Center
            ) {
              if (isDone) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = if (colors.isLight) Color.White else colors.background,
                  modifier = Modifier.size(16.dp)
                )
              }
            }

            Column(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = habit.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                  fontWeight = if (isDone) FontWeight.Medium else FontWeight.Normal
                ),
                color = colors.textPrimary
              )

              // 7-day dot strip per habit
              if (weekStatus.isNotEmpty()) {
                Row(
                  horizontalArrangement = Arrangement.spacedBy(5.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  weekStatus.forEach { doneOnDay ->
                    Box(
                      modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(
                          if (doneOnDay) colors.habits else colors.divider
                        )
                    )
                  }
                }
              }
            }
          }

          IconButton(
            onClick = { onDeleteHabit(habit.id) },
            modifier = Modifier.size(48.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Remove ${habit.title}",
              tint = colors.textSecondary,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        if (idx < habits.lastIndex) {
          HorizontalDivider(color = colors.divider)
        }
      }
    }
  }
}

@Composable
private fun OneMinuteResetBreathing(
  isActive: Boolean,
  reduceMotion: Boolean,
  onToggle: () -> Unit,
  onComplete: () -> Unit
) {
  val colors = MaterialTheme.kinetiqColors
  var phaseLabel by remember { mutableStateOf("Inhale") }
  var secondsLeftInPhase by remember { mutableIntStateOf(4) }
  var totalSecondsLeft by remember { mutableIntStateOf(60) }
  val breathScale = remember { Animatable(0.72f) }

  LaunchedEffect(isActive, reduceMotion) {
    if (!isActive) {
      totalSecondsLeft = 60
      phaseLabel = "Inhale"
      secondsLeftInPhase = 4
      breathScale.snapTo(0.72f)
      return@LaunchedEffect
    }

    // 6 cycles of 4s in + 6s out = 60 seconds (1-minute reset)
    for (cycle in 0 until 6) {
      if (!isActive) break
      phaseLabel = "Inhale"
      val inhaleJob = if (!reduceMotion) {
        launch {
          breathScale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 4000, easing = FastOutSlowInEasing)
          )
        }
      } else null
      for (sec in 4 downTo 1) {
        secondsLeftInPhase = sec
        totalSecondsLeft = (5 - cycle) * 10 + 6 + sec
        delay(1000)
      }
      inhaleJob?.join()

      phaseLabel = "Exhale"
      val exhaleJob = if (!reduceMotion) {
        launch {
          breathScale.animateTo(
            targetValue = 0.72f,
            animationSpec = tween(durationMillis = 6000, easing = FastOutSlowInEasing)
          )
        }
      } else null
      for (sec in 6 downTo 1) {
        secondsLeftInPhase = sec
        totalSecondsLeft = (5 - cycle) * 10 + sec
        delay(1000)
      }
      exhaleJob?.join()
    }
    onComplete()
  }

  Surface(
    shape = RoundedCornerShape(24.dp),
    color = colors.surface2,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("resonance_breathing_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 48.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Spa,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
          }
          Column {
            Text(
              text = "1-minute reset",
              style = MaterialTheme.typography.titleMedium,
              color = colors.textPrimary
            )
            Text(
              text = if (isActive) "${totalSecondsLeft}s remaining" else "4s inhale · 6s exhale",
              style = MaterialTheme.typography.labelMedium,
              color = colors.textSecondary
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(50),
          color = if (isActive) colors.surface3 else MaterialTheme.colorScheme.primary,
          modifier = Modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onToggle)
            .testTag("breathing_play_toggle")
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 18.dp)
          ) {
            Text(
              text = if (isActive) "Stop" else "Start",
              style = MaterialTheme.typography.labelLarge,
              color = if (isActive) colors.textPrimary else MaterialTheme.colorScheme.onPrimary
            )
          }
        }
      }

      AnimatedVisibility(
        visible = isActive,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 8.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(136.dp)
          ) {
            Box(
              modifier = Modifier
                .size(132.dp)
                .scale(breathScale.value)
                .background(
                  color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                  shape = CircleShape
                )
            )
            Box(
              modifier = Modifier
                .size(86.dp)
                .scale(breathScale.value)
                .background(
                  color = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
                  shape = CircleShape
                )
            )
            Text(
              text = "$secondsLeftInPhase",
              style = KinetiqValueTextStyle.copy(fontSize = 24.sp),
              color = colors.textPrimary
            )
          }

          Text(
            text = phaseLabel,
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary
          )
        }
      }
    }
  }
}

private data class GentleReminder(
  val id: String,
  val text: String,
  val icon: ImageVector,
  val accent: Color
)

private fun buildGentleReminders(
  colors: KinetiqSemanticColors,
  waterRemainingMl: Int,
  stepsRemaining: Int,
  sleepMinutes: Int,
  isRestDay: Boolean
): List<GentleReminder> {
  return buildList {
    if (isRestDay) {
      add(
        GentleReminder(
          id = "rest",
          text = "Rest day active — lighter pace today",
          icon = Icons.Default.SelfImprovement,
          accent = colors.habits
        )
      )
    }
    if (waterRemainingMl >= 250) {
      add(
        GentleReminder(
          id = "water",
          text = "$waterRemainingMl ml left to reach today's water goal",
          icon = Icons.Default.WaterDrop,
          accent = colors.water
        )
      )
    }
    if (!isRestDay && stepsRemaining >= 500) {
      add(
        GentleReminder(
          id = "steps",
          text = "${formatStepCount(stepsRemaining)} steps to go today",
          icon = Icons.Default.DirectionsWalk,
          accent = colors.steps
        )
      )
    }
    if (sleepMinutes <= 0) {
      add(
        GentleReminder(
          id = "sleep",
          text = "Last night's sleep isn't logged yet",
          icon = Icons.Default.Bedtime,
          accent = colors.sleep
        )
      )
    }
  }
}

@Composable
private fun GentleRemindersList(
  reminders: List<GentleReminder>,
  onDismissReminder: (String) -> Unit
) {
  val colors = MaterialTheme.kinetiqColors
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    reminders.forEach { item ->
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = colors.surface2,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = item.icon,
              contentDescription = null,
              tint = item.accent,
              modifier = Modifier.size(17.dp)
            )
            Text(
              text = item.text,
              style = MaterialTheme.typography.bodyMedium,
              color = colors.textSecondary
            )
          }

          IconButton(
            onClick = { onDismissReminder(item.id) },
            modifier = Modifier.size(48.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Dismiss reminder",
              tint = colors.textSecondary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun LogPickerBottomSheet(
  onPickWater: () -> Unit,
  onPickSteps: () -> Unit,
  onPickSleep: () -> Unit,
  onPickHabit: () -> Unit,
  onDismiss: () -> Unit
) {
  val colors = MaterialTheme.kinetiqColors
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = colors.surface1,
    contentColor = colors.textPrimary
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 28.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = "Log for today",
        style = MaterialTheme.typography.titleLarge,
        color = colors.textPrimary,
        modifier = Modifier.padding(bottom = 4.dp)
      )

      LogSheetOptionRow(
        icon = Icons.Default.WaterDrop,
        title = "Water",
        subtitle = "Add custom ml or container",
        accent = colors.water,
        onClick = onPickWater
      )
      HorizontalDivider(color = colors.divider)
      LogSheetOptionRow(
        icon = Icons.Default.DirectionsWalk,
        title = "Steps",
        subtitle = "Add steps or manage live counter",
        accent = colors.steps,
        onClick = onPickSteps
      )
      HorizontalDivider(color = colors.divider)
      LogSheetOptionRow(
        icon = Icons.Default.Bedtime,
        title = "Sleep",
        subtitle = "Log hours slept and quality",
        accent = colors.sleep,
        onClick = onPickSleep
      )
      HorizontalDivider(color = colors.divider)
      LogSheetOptionRow(
        icon = Icons.Default.SelfImprovement,
        title = "Habit",
        subtitle = "Create a new daily habit",
        accent = colors.habits,
        onClick = onPickHabit
      )
    }
  }
}

@Composable
private fun LogSheetOptionRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  accent: Color,
  onClick: () -> Unit
) {
  val colors = MaterialTheme.kinetiqColors
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(min = 56.dp)
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Box(
      modifier = Modifier
        .size(38.dp)
        .background(accent.copy(alpha = 0.14f), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = accent,
        modifier = Modifier.size(19.dp)
      )
    }
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
        color = colors.textPrimary
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelMedium,
        color = colors.textSecondary
      )
    }
  }
}

@Composable
private fun CalmAddHabitDialog(
  onDismiss: () -> Unit,
  onConfirm: (String, String, String) -> Unit
) {
  val colors = MaterialTheme.kinetiqColors
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Mindfulness") }
  val categories = listOf("Mindfulness", "Movement", "Hydration", "Rest")

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = colors.surface1,
    titleContentColor = colors.textPrimary,
    textContentColor = colors.textPrimary,
    title = {
      Text("Add habit", style = MaterialTheme.typography.titleLarge)
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Habit name") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.forEach { cat ->
            val selected = cat == category
            Surface(
              shape = RoundedCornerShape(50),
              color = if (selected) colors.habits.copy(alpha = 0.2f) else colors.surface2,
              modifier = Modifier
                .weight(1f)
                .heightIn(min = 40.dp)
                .clip(RoundedCornerShape(50))
                .clickable { category = cat }
            ) {
              Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 4.dp)) {
                Text(
                  text = cat,
                  style = MaterialTheme.typography.labelSmall,
                  color = if (selected) colors.habits else colors.textSecondary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val trimmed = title.trim()
          if (trimmed.isNotEmpty()) {
            onConfirm(trimmed, category, "SelfImprovement")
          }
        },
        enabled = title.trim().isNotEmpty(),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier.heightIn(min = 48.dp)
      ) {
        Text("Add habit")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) {
        Text("Cancel", color = colors.textSecondary)
      }
    }
  )
}

