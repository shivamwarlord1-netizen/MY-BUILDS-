package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.exercise.ExerciseDatabase
import com.example.data.exercise.MuscleGroup
import com.example.steps.StepDeltaCalculator
import com.example.ui.components.SUPPORTED_EXERCISE_ANIMATION_IDS
import com.example.ui.viewmodel.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
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
    assertEquals(15, allExercises.size)

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
}
