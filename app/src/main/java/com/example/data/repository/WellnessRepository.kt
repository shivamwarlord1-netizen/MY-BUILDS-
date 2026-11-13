package com.example.data.repository

import com.example.data.local.ActivityDao
import com.example.data.local.HabitDao
import com.example.data.local.HydrationDao
import com.example.data.local.SleepDao
import com.example.data.local.UserProfileDao
import com.example.data.model.ActivityRecord
import com.example.data.model.Habit
import com.example.data.model.HabitLog
import com.example.data.model.HydrationRecord
import com.example.data.model.SleepRecord
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WellnessRepository(
  private val activityDao: ActivityDao,
  private val hydrationDao: HydrationDao,
  private val sleepDao: SleepDao,
  private val habitDao: HabitDao,
  private val userProfileDao: UserProfileDao
) {
  private val activityMutex = Mutex()
  val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfile()

  fun getTodayDateString(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
  }

  fun getPastDaysDateStrings(daysBack: Int): List<String> {
    val result = mutableListOf<String>()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val cal = Calendar.getInstance()
    for (i in (daysBack - 1) downTo 0) {
      val tempCal = cal.clone() as Calendar
      tempCal.add(Calendar.DAY_OF_YEAR, -i)
      result.add(sdf.format(tempCal.time))
    }
    return result
  }

  // --- Activity ---
  fun getActivityForDate(date: String): Flow<ActivityRecord?> =
    activityDao.getActivityForDate(date)

  fun getActivitiesInRange(startDate: String, endDate: String): Flow<List<ActivityRecord>> =
    activityDao.getActivitiesInRange(startDate, endDate)

  suspend fun logOrAddSteps(date: String, stepsToAdd: Int, targetSteps: Int = 8000) {
    addStepsInternal(date = date, deltaSteps = stepsToAdd, targetSteps = targetSteps, source = "Exercise")
  }

  suspend fun recordSensorSteps(date: String, deltaSteps: Int, targetSteps: Int = 8000) {
    if (deltaSteps <= 0) return
    addStepsInternal(date = date, deltaSteps = deltaSteps, targetSteps = targetSteps, source = "StepSensor")
  }

  private suspend fun addStepsInternal(
    date: String,
    deltaSteps: Int,
    targetSteps: Int,
    source: String
  ) {
    activityMutex.withLock {
      val existing = activityDao.getActivityForDate(date).first()
      val newSteps = ((existing?.stepCount ?: 0) + deltaSteps).coerceAtLeast(0)
      val estimatedKm = newSteps * 0.00075f
      val estimatedKcal = (newSteps * 0.04f).toInt()
      val activeMinutes = (newSteps / 110).coerceAtLeast(0)

      val updated = (existing ?: ActivityRecord(date = date, targetSteps = targetSteps)).copy(
        stepCount = newSteps,
        targetSteps = targetSteps,
        distanceKm = estimatedKm,
        caloriesEstimated = estimatedKcal,
        activeMinutes = activeMinutes,
        source = source
      )
      activityDao.upsertActivity(updated)
    }
  }

  suspend fun setRestDay(date: String, isRestDay: Boolean) {
    val existing = activityDao.getActivityForDate(date).first() ?: ActivityRecord(date = date)
    activityDao.upsertActivity(existing.copy(isRestDay = isRestDay))
  }

  // --- Hydration ---
  fun getHydrationLogsForDate(date: String): Flow<List<HydrationRecord>> =
    hydrationDao.getHydrationLogsForDate(date)

  fun getTotalHydrationForDate(date: String): Flow<Int?> =
    hydrationDao.getTotalHydrationForDate(date)

  fun getHydrationInRange(startDate: String, endDate: String): Flow<List<HydrationRecord>> =
    hydrationDao.getHydrationInRange(startDate, endDate)

  suspend fun addWaterLog(amountMl: Int, containerType: String = "Glass") {
    val date = getTodayDateString()
    val record = HydrationRecord(
      date = date,
      amountMl = amountMl,
      containerType = containerType,
      timestamp = System.currentTimeMillis()
    )
    hydrationDao.insertHydration(record)
  }

  suspend fun deleteWaterLog(id: Long) {
    hydrationDao.deleteById(id)
  }

  // --- Sleep ---
  fun getSleepForDate(date: String): Flow<SleepRecord?> =
    sleepDao.getSleepForDate(date)

  fun getSleepInRange(startDate: String, endDate: String): Flow<List<SleepRecord>> =
    sleepDao.getSleepInRange(startDate, endDate)

  suspend fun logSleep(
    date: String,
    bedtimeMillis: Long,
    wakeMillis: Long,
    qualityRating: Int,
    notes: String
  ) {
    val durationMin = ((wakeMillis - bedtimeMillis) / (1000 * 60)).toInt().coerceAtLeast(0)
    val record = SleepRecord(
      date = date,
      bedtimeMillis = bedtimeMillis,
      wakeMillis = wakeMillis,
      durationMinutes = durationMin,
      qualityRating = qualityRating,
      notes = notes
    )
    sleepDao.upsertSleep(record)
  }

  // --- Habits ---
  val activeHabits: Flow<List<Habit>> = habitDao.getActiveHabits()

  fun getHabitLogsForDate(date: String): Flow<List<HabitLog>> =
    habitDao.getHabitLogsForDate(date)

  fun getHabitLogsInRange(startDate: String, endDate: String): Flow<List<HabitLog>> =
    habitDao.getHabitLogsInRange(startDate, endDate)

  suspend fun toggleHabit(habitId: Long, date: String, currentlyCompleted: Boolean) {
    if (currentlyCompleted) {
      habitDao.deleteHabitLog(habitId, date)
    } else {
      habitDao.insertHabitLog(HabitLog(habitId = habitId, date = date, isCompleted = true))
    }
  }

  suspend fun addNewHabit(title: String, category: String, iconName: String, targetDays: Int = 7) {
    habitDao.insertHabit(
      Habit(
        title = title,
        category = category,
        iconName = iconName,
        targetDaysPerWeek = targetDays
      )
    )
  }

  suspend fun deleteHabit(habitId: Long) {
    habitDao.deleteHabit(habitId)
  }

  // --- User Profile & Settings ---
  suspend fun updateUserProfile(profile: UserProfile) {
    userProfileDao.upsertUserProfile(profile)
  }

  // --- Data Sovereignty: Export & Wipe ---
  suspend fun exportDataAsJson(): String {
    val root = JSONObject()
    root.put("app", "KinetiQ")
    root.put("exportedAt", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date()))
    root.put("formatVersion", 1)

    val profile = userProfileDao.getUserProfileOnce()
    val profileObj = JSONObject().apply {
      put("displayName", profile?.displayName ?: "Friend")
      put("targetSteps", profile?.targetSteps ?: 8000)
      put("targetWaterMl", profile?.targetWaterMl ?: 2250)
      put("targetSleepHours", profile?.targetSleepHours ?: 8f)
      put("language", profile?.language ?: "en")
    }
    root.put("profile", profileObj)

    val activities = activityDao.getAllActivitiesRaw()
    val actArray = JSONArray()
    for (a in activities) {
      actArray.put(JSONObject().apply {
        put("date", a.date)
        put("steps", a.stepCount)
        put("targetSteps", a.targetSteps)
        put("activeMinutes", a.activeMinutes)
        put("distanceKm", a.distanceKm)
        put("caloriesEstimated", a.caloriesEstimated)
        put("isRestDay", a.isRestDay)
      })
    }
    root.put("activityLogs", actArray)

    val hydration = hydrationDao.getAllHydrationRaw()
    val hydArray = JSONArray()
    for (h in hydration) {
      hydArray.put(JSONObject().apply {
        put("timestamp", h.timestamp)
        put("date", h.date)
        put("amountMl", h.amountMl)
        put("containerType", h.containerType)
      })
    }
    root.put("hydrationLogs", hydArray)

    val sleep = sleepDao.getAllSleepRaw()
    val sleepArray = JSONArray()
    for (s in sleep) {
      sleepArray.put(JSONObject().apply {
        put("date", s.date)
        put("durationMinutes", s.durationMinutes)
        put("qualityRating", s.qualityRating)
        put("notes", s.notes)
      })
    }
    root.put("sleepLogs", sleepArray)

    val habits = habitDao.getAllHabitsRaw()
    val habitLogs = habitDao.getAllHabitLogsRaw()
    val habitArray = JSONArray()
    for (h in habits) {
      habitArray.put(JSONObject().apply {
        put("id", h.id)
        put("title", h.title)
        put("category", h.category)
      })
    }
    root.put("habits", habitArray)

    val habitLogArray = JSONArray()
    for (hl in habitLogs) {
      habitLogArray.put(JSONObject().apply {
        put("habitId", hl.habitId)
        put("date", hl.date)
      })
    }
    root.put("habitCompletions", habitLogArray)

    return root.toString(2)
  }

  suspend fun purgeAllUserData() {
    activityDao.deleteAllActivities()
    hydrationDao.deleteAllHydration()
    sleepDao.deleteAllSleep()
    habitDao.deleteAllHabitLogs()
    // Reset user profile to baseline
    userProfileDao.upsertUserProfile(
      UserProfile(
        id = 1,
        displayName = "Friend",
        targetSteps = 8000,
        targetWaterMl = 2250,
        targetSleepHours = 8.0f,
        preferredGlassSizeMl = 250,
        language = "en",
        darkModeSetting = "system",
        notificationsEnabled = true,
        healthConnectAuthorized = false,
        restDayMode = false
      )
    )
  }
}
