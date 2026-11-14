package com.example

import android.Manifest
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.exercise.ExerciseDatabase
import com.example.data.exercise.ExerciseTier
import com.example.data.exercise.MuscleGroup
import com.example.data.exercise.resolveTier
import com.example.steps.StepDeltaCalculator
import com.example.steps.StepMilestone
import com.example.steps.StepMilestoneEvaluator
import com.example.steps.StepMilestoneNotifier
import com.example.ui.components.SUPPORTED_EXERCISE_ANIMATION_IDS
import com.example.ui.viewmodel.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("KinetiQ", appName)
  }

  @Test
  fun `navigation screens exclude removed AI tab`() {
    val screenNames = Screen.entries.map { it.name }
    assertEquals(listOf("TODAY", "INSIGHTS", "EXERCISES", "SETTINGS"), screenNames)
    assertFalse("AI_GUIDE tab must be removed", screenNames.contains("AI_GUIDE"))
  }

  @Test
  fun `header date format excludes weekday name and keeps month and day`() {
    val sdf = SimpleDateFormat("MMMM d", Locale.US)
    val formatted = sdf.format(Date(1700000000000L))
    assertFalse("Formatted date should not contain comma or weekday", formatted.contains(","))
    assertTrue("Formatted date should contain month and day", formatted.matches(Regex("^[A-Za-z]+ \\d{1,2}$")))
  }

  @Test
  fun `exercises database contains full body muscle groups`() {
    val shoulders = ExerciseDatabase.getExercisesByGroup(MuscleGroup.SHOULDERS)
    val chest = ExerciseDatabase.getExercisesByGroup(MuscleGroup.CHEST)
    val back = ExerciseDatabase.getExercisesByGroup(MuscleGroup.BACK)
    val arms = ExerciseDatabase.getExercisesByGroup(MuscleGroup.ARMS)
    val legs = ExerciseDatabase.getExercisesByGroup(MuscleGroup.LEGS)
    val core = ExerciseDatabase.getExercisesByGroup(MuscleGroup.CORE)

    assertTrue("Shoulders exercises should be present", shoulders.isNotEmpty())
    assertTrue("Chest exercises should be present", chest.isNotEmpty())
    assertTrue("Back exercises should be present", back.isNotEmpty())
    assertTrue("Arms exercises should be present", arms.isNotEmpty())
    assertTrue("Legs exercises should be present", legs.isNotEmpty())
    assertTrue("Core exercises should be present", core.isNotEmpty())

    val first = shoulders.first()
    assertNotNull(first.primaryMuscle)
    assertTrue(first.setupSteps.isNotEmpty())
    assertTrue(first.executionSteps.isNotEmpty())
  }

  @Test
  fun `every exercise has a dedicated animation and complete guide metadata`() {
    val allExercises = ExerciseDatabase.exercises
    assertEquals(16, allExercises.size)

    val uniqueIds = allExercises.map { it.id }.toSet()
    assertEquals("Exercise IDs must be unique", allExercises.size, uniqueIds.size)

    allExercises.forEach { exercise ->
      assertTrue(
        "Missing dedicated animation for ${exercise.id}",
        exercise.id in SUPPORTED_EXERCISE_ANIMATION_IDS
      )
      assertTrue("Missing primaryMuscle for ${exercise.id}", exercise.primaryMuscle.isNotBlank())
      assertTrue("Missing setupSteps for ${exercise.id}", exercise.setupSteps.isNotEmpty())
      assertTrue("Missing executionSteps for ${exercise.id}", exercise.executionSteps.isNotEmpty())
      assertTrue("Missing breathingPattern for ${exercise.id}", exercise.breathingPattern.isNotBlank())
      assertTrue("Missing recommendedSetsReps for ${exercise.id}", exercise.recommendedSetsReps.isNotBlank())
    }
  }

  @Test
  fun `step delta calculator handles baseline, increments, reboot, and midnight rollover`() {
    // 1. Fresh baseline returns 0 delta and anchors current boot count
    val baseline = StepDeltaCalculator.computeDelta(
      anchorDate = null,
      lastSensorTotal = -1L,
      currentDate = "2026-11-13",
      currentSensorTotal = 12000L
    )
    assertEquals(0, baseline.deltaSteps)
    assertEquals("2026-11-13", baseline.newAnchorDate)
    assertEquals(12000L, baseline.newLastSensorTotal)

    // 2. Normal same-day walk returns exact incremental steps
    val walk = StepDeltaCalculator.computeDelta(
      anchorDate = baseline.newAnchorDate,
      lastSensorTotal = baseline.newLastSensorTotal,
      currentDate = "2026-11-13",
      currentSensorTotal = 12450L
    )
    assertEquals(450, walk.deltaSteps)
    assertEquals(12450L, walk.newLastSensorTotal)

    // 3. Midday device reboot resets sensor counter < lastSensorTotal; keeps post-reboot steps
    val reboot = StepDeltaCalculator.computeDelta(
      anchorDate = walk.newAnchorDate,
      lastSensorTotal = walk.newLastSensorTotal,
      currentDate = "2026-11-13",
      currentSensorTotal = 180L
    )
    assertEquals(180, reboot.deltaSteps)
    assertEquals(180L, reboot.newLastSensorTotal)

    // 4. Midnight rollover resets daily anchor cleanly with 0 carried-over delta
    val midnight = StepDeltaCalculator.computeDelta(
      anchorDate = reboot.newAnchorDate,
      lastSensorTotal = reboot.newLastSensorTotal,
      currentDate = "2026-11-14",
      currentSensorTotal = 250L
    )
    assertEquals(0, midnight.deltaSteps)
    assertEquals("2026-11-14", midnight.newAnchorDate)
    assertEquals(250L, midnight.newLastSensorTotal)
  }

  @Test
  fun `weekly step points map 7 day history and keep live today steps synced`() {
    val dates = listOf(
      "2026-11-07",
      "2026-11-08",
      "2026-11-09",
      "2026-11-10",
      "2026-11-11",
      "2026-11-12",
      "2026-11-13"
    )
    val activities = listOf(
      com.example.data.model.ActivityRecord(date = "2026-11-08", stepCount = 6200),
      com.example.data.model.ActivityRecord(date = "2026-11-11", stepCount = 9400),
      com.example.data.model.ActivityRecord(date = "2026-11-13", stepCount = 3100)
    )

    val points = com.example.ui.screens.buildWeeklyStepPoints(
      pastWeekDates = dates,
      pastWeekActivities = activities,
      todayDate = "2026-11-13",
      currentSteps = 4250
    )

    assertEquals(7, points.size)
    assertEquals(0, points[0].steps)
    assertEquals(6200, points[1].steps)
    assertEquals(9400, points[4].steps)
    assertEquals(4250, points[6].steps)
    assertEquals("Today", points[6].dayLabel)
    assertTrue(points[6].isToday)
    assertFalse(points[0].isToday)
  }

  @Test
  fun `step milestone evaluator triggers 50, 75, and 100 percent once and skips lower tiers on jump`() {
    val below50 = StepMilestoneEvaluator.evaluate(steps = 3999, targetSteps = 8000, alreadyNotifiedMask = 0)
    assertNull(below50.milestoneToNotify)
    assertEquals(0, below50.updatedMask)

    val hit50 = StepMilestoneEvaluator.evaluate(steps = 4000, targetSteps = 8000, alreadyNotifiedMask = below50.updatedMask)
    assertEquals(StepMilestone.HALF_WAY, hit50.milestoneToNotify)

    val repeat50 = StepMilestoneEvaluator.evaluate(steps = 5000, targetSteps = 8000, alreadyNotifiedMask = hit50.updatedMask)
    assertNull(repeat50.milestoneToNotify)

    val hit75 = StepMilestoneEvaluator.evaluate(steps = 6000, targetSteps = 8000, alreadyNotifiedMask = repeat50.updatedMask)
    assertEquals(StepMilestone.THREE_QUARTERS, hit75.milestoneToNotify)

    val hit100 = StepMilestoneEvaluator.evaluate(steps = 8000, targetSteps = 8000, alreadyNotifiedMask = hit75.updatedMask)
    assertEquals(StepMilestone.GOAL_COMPLETE, hit100.milestoneToNotify)

    val repeat100 = StepMilestoneEvaluator.evaluate(steps = 11000, targetSteps = 8000, alreadyNotifiedMask = hit100.updatedMask)
    assertNull(repeat100.milestoneToNotify)

    // Large single batch jumping from 0% directly to 105% announces only 100% and suppresses stale 50%/75%
    val bigJump = StepMilestoneEvaluator.evaluate(steps = 8400, targetSteps = 8000, alreadyNotifiedMask = 0)
    assertEquals(StepMilestone.GOAL_COMPLETE, bigJump.milestoneToNotify)
    val afterJump = StepMilestoneEvaluator.evaluate(steps = 8500, targetSteps = 8000, alreadyNotifiedMask = bigJump.updatedMask)
    assertNull(afterJump.milestoneToNotify)
  }

  @Test
  fun `step milestone notifier dispatches quiet vs goal channels and resets on new date`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    Shadows.shadowOf(app).grantPermissions(Manifest.permission.POST_NOTIFICATIONS)
    val notificationManager = app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val shadowNm = Shadows.shadowOf(notificationManager)

    StepMilestoneNotifier.clearAll(app)

    // 50% fires on quiet channel
    val m50 = StepMilestoneNotifier.checkAndNotify(app, date = "2026-11-13", steps = 4000, targetSteps = 8000)
    assertEquals(StepMilestone.HALF_WAY, m50)
    assertEquals(1, shadowNm.allNotifications.size)
    assertEquals(StepMilestoneNotifier.CHANNEL_QUIET_ID, shadowNm.allNotifications[0].channelId)

    // Duplicate 50% on same day does not post again
    val dup50 = StepMilestoneNotifier.checkAndNotify(app, date = "2026-11-13", steps = 4500, targetSteps = 8000)
    assertNull(dup50)
    assertEquals(1, shadowNm.allNotifications.size)

    // 75% fires on quiet channel
    val m75 = StepMilestoneNotifier.checkAndNotify(app, date = "2026-11-13", steps = 6000, targetSteps = 8000)
    assertEquals(StepMilestone.THREE_QUARTERS, m75)
    assertEquals(2, shadowNm.allNotifications.size)
    assertEquals(
      StepMilestoneNotifier.CHANNEL_QUIET_ID,
      shadowNm.getNotification(StepMilestone.THREE_QUARTERS.notificationId).channelId
    )

    // 100% fires on goal achievement channel (with sound & vibration)
    val m100 = StepMilestoneNotifier.checkAndNotify(app, date = "2026-11-13", steps = 8100, targetSteps = 8000)
    assertEquals(StepMilestone.GOAL_COMPLETE, m100)
    assertEquals(3, shadowNm.allNotifications.size)
    assertEquals(
      StepMilestoneNotifier.CHANNEL_GOAL_ID,
      shadowNm.getNotification(StepMilestone.GOAL_COMPLETE.notificationId).channelId
    )

    // New calendar date resets milestone tracking cleanly
    val nextDay50 = StepMilestoneNotifier.checkAndNotify(app, date = "2026-11-14", steps = 4200, targetSteps = 8000)
    assertEquals(StepMilestone.HALF_WAY, nextDay50)
  }

  @Test
  fun `exercises partition cleanly into Beginner and Intermediate spaces`() {
    val beginnerMoves = ExerciseDatabase.getExercisesByTier(ExerciseTier.BEGINNER)
    val intermediateMoves = ExerciseDatabase.getExercisesByTier(ExerciseTier.INTERMEDIATE)

    assertEquals(7, beginnerMoves.size)
    assertEquals(9, intermediateMoves.size)
    assertTrue(beginnerMoves.all { it.difficulty.equals("Beginner", ignoreCase = true) })
    assertTrue(intermediateMoves.none { it.difficulty.equals("Beginner", ignoreCase = true) })
    assertTrue(beginnerMoves.any { it.id == "push-up" && it.muscleGroup == MuscleGroup.CHEST })
    assertEquals(
      ExerciseDatabase.exercises.size,
      (beginnerMoves.map { it.id } + intermediateMoves.map { it.id }).toSet().size
    )

    val beginnerChest = ExerciseDatabase.getExercisesByTierAndGroup(ExerciseTier.BEGINNER, MuscleGroup.CHEST)
    assertEquals(2, beginnerChest.size)

    val beginnerShoulders = ExerciseDatabase.getExercisesByTierAndGroup(ExerciseTier.BEGINNER, MuscleGroup.SHOULDERS)
    assertEquals(2, beginnerShoulders.size)

    val intermediateCore = ExerciseDatabase.getExercisesByTierAndGroup(ExerciseTier.INTERMEDIATE, MuscleGroup.CORE)
    assertEquals(2, intermediateCore.size)
  }

  @Test
  fun `today readiness and 7-day calendar strip compute accurate scores and streaks`() {
    val dates = listOf(
      "2026-11-08", "2026-11-09", "2026-11-10", "2026-11-11",
      "2026-11-12", "2026-11-13", "2026-11-14"
    )
    val activities = listOf(
      com.example.data.model.ActivityRecord(date = "2026-11-12", stepCount = 8500),
      com.example.data.model.ActivityRecord(date = "2026-11-13", stepCount = 9200),
      com.example.data.model.ActivityRecord(date = "2026-11-14", stepCount = 4000)
    )
    val strip = com.example.ui.screens.buildSevenDayStrip(
      pastWeekDates = dates,
      todayDate = "2026-11-14",
      targetSteps = 8000,
      targetWaterMl = 2000,
      targetSleepHours = 8f,
      totalHabits = 2,
      todaySteps = 4000,
      todayWaterMl = 1000,
      todaySleepMinutes = 480,
      todayHabitCount = 1,
      pastWeekActivities = activities,
      pastWeekHydration = emptyList(),
      pastWeekSleep = emptyList(),
      pastWeekHabitLogs = emptyList()
    )
    assertEquals(7, strip.size)
    assertTrue(strip.last().isToday)
    assertFalse(strip.first().hasAnyRecord)

    val readiness = com.example.ui.screens.computeTodayReadiness(
      stepProgress = 0.5f,
      waterProgress = 0.5f,
      sleepProgress = 1.0f,
      habitProgress = 0.5f,
      isRestDay = false,
      waterRemainingMl = 1000,
      stepsRemaining = 4000,
      sleepMinutes = 480,
      stripItems = strip
    )
    assertEquals(63, readiness.scorePercent)
    assertEquals(3, readiness.streakDays)
    assertEquals("STEADY PACE", readiness.statusLabel)

    val restReadiness = com.example.ui.screens.computeTodayReadiness(
      stepProgress = 0.2f,
      waterProgress = 1.0f,
      sleepProgress = 1.0f,
      habitProgress = 1.0f,
      isRestDay = true,
      waterRemainingMl = 0,
      stepsRemaining = 6400,
      sleepMinutes = 480,
      stripItems = strip
    )
    assertEquals("REST DAY ACTIVE", restReadiness.statusLabel)
    assertTrue(restReadiness.isHighReadiness)

    val restExercise = com.example.ui.screens.pickFeaturedExercise(isRestDay = true, rotationOffset = 0)
    assertEquals(ExerciseTier.BEGINNER, restExercise.resolveTier())
  }

  @Test
  fun `simple theme tokens provide white and neutral grey monochrome palette`() {
    val whiteAndGreyColors = listOf(
      com.example.ui.theme.SimpleBackground,
      com.example.ui.theme.SimpleSurface1,
      com.example.ui.theme.SimpleSurface2,
      com.example.ui.theme.SimpleSurface3,
      com.example.ui.theme.SimplePrimary,
      com.example.ui.theme.SimpleOnPrimary,
      com.example.ui.theme.SimpleSecondary,
      com.example.ui.theme.SimpleTertiary,
      com.example.ui.theme.SimpleTextPrimary,
      com.example.ui.theme.SimpleTextSecondary,
      com.example.ui.theme.PillarStepsSimple,
      com.example.ui.theme.PillarWaterSimple,
      com.example.ui.theme.PillarSleepSimple,
      com.example.ui.theme.PillarHabitsSimple
    )

    whiteAndGreyColors.forEach { color ->
      val maxChannel = maxOf(color.red, color.green, color.blue)
      val minChannel = minOf(color.red, color.green, color.blue)
      assertTrue(
        "Simple theme color $color should be neutral white/grey (channel spread <= 0.04)",
        (maxChannel - minChannel) <= 0.04f
      )
    }
    assertEquals(androidx.compose.ui.graphics.Color.White, com.example.ui.theme.SimpleSurface1)
  }
}
