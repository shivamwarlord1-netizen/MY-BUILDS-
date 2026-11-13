package com.example.ui.viewmodel

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ActivityRecord
import com.example.data.model.Habit
import com.example.data.model.HabitLog
import com.example.data.model.HydrationRecord
import com.example.data.model.SleepRecord
import com.example.data.model.UserProfile
import com.example.data.repository.WellnessRepository
import com.example.steps.StepAnchorStore
import com.example.steps.StepPermissionHelper
import com.example.steps.StepTrackerStatus
import com.example.steps.StepTrackingService
import com.example.steps.StepTrackingStateBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
  TODAY,
  INSIGHTS,
  EXERCISES,
  SETTINGS
}

data class WellnessUiState(
  val todayDate: String = "",
  val todayActivity: ActivityRecord? = null,
  val todayWaterLogs: List<HydrationRecord> = emptyList(),
  val todayWaterTotalMl: Int = 0,
  val todaySleep: SleepRecord? = null,
  val activeHabits: List<Habit> = emptyList(),
  val todayHabitLogs: List<HabitLog> = emptyList(),
  val userProfile: UserProfile = UserProfile(),
  val pastWeekActivities: List<ActivityRecord> = emptyList(),
  val pastWeekSleep: List<SleepRecord> = emptyList(),
  val pastWeekHabitLogs: List<HabitLog> = emptyList(),
  val pastWeekDates: List<String> = emptyList(),
  val currentScreen: Screen = Screen.TODAY,
  val stepTrackerStatus: StepTrackerStatus = StepTrackerStatus.SENSOR_UNAVAILABLE,
  val bannerNotification: String? = null
)

class WellnessViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: WellnessRepository

  private val _currentScreen = MutableStateFlow(Screen.TODAY)
  private val _bannerNotification = MutableStateFlow<String?>(null)
  private val _trackerRefreshTick = MutableStateFlow(0)

  val todayDate: String
  val past7Days: List<String>

  init {
    val database = AppDatabase.getDatabase(application, viewModelScope)
    repository = WellnessRepository(
      database.activityDao(),
      database.hydrationDao(),
      database.sleepDao(),
      database.habitDao(),
      database.userProfileDao()
    )

    todayDate = repository.getTodayDateString()
    past7Days = repository.getPastDaysDateStrings(7)
  }

  private val activityFlow = repository.getActivityForDate(todayDate)
  private val waterLogsFlow = repository.getHydrationLogsForDate(todayDate)
  private val waterTotalFlow = repository.getTotalHydrationForDate(todayDate)
  private val sleepFlow = repository.getSleepForDate(todayDate)
  private val habitsFlow = repository.activeHabits
  private val habitLogsFlow = repository.getHabitLogsForDate(todayDate)
  private val profileFlow = repository.userProfile
  private val weekActivityFlow = repository.getActivitiesInRange(past7Days.first(), past7Days.last())
  private val weekSleepFlow = repository.getSleepInRange(past7Days.first(), past7Days.last())
  private val weekHabitsFlow = repository.getHabitLogsInRange(past7Days.first(), past7Days.last())

  val uiState: StateFlow<WellnessUiState> = combine(
    combine(activityFlow, waterLogsFlow, waterTotalFlow, sleepFlow) { act, wLogs, wTotal, slp ->
      Tuple4(act, wLogs, wTotal ?: 0, slp)
    },
    combine(habitsFlow, habitLogsFlow, profileFlow) { hab, hLogs, prof ->
      Triple(hab, hLogs, prof ?: UserProfile())
    },
    combine(weekActivityFlow, weekSleepFlow, weekHabitsFlow) { wAct, wSlp, wHLogs ->
      Triple(wAct, wSlp, wHLogs)
    },
    combine(
      _currentScreen,
      _bannerNotification,
      StepTrackingStateBus.isServiceRunning,
      _trackerRefreshTick
    ) { scrn, banner, serviceRunning, _ ->
      Triple(scrn, banner, computeStepTrackerStatus(serviceRunning))
    }
  ) { t1, t2, t3, t4 ->
    WellnessUiState(
      todayDate = todayDate,
      todayActivity = t1.a,
      todayWaterLogs = t1.b,
      todayWaterTotalMl = t1.c,
      todaySleep = t1.d,
      activeHabits = t2.first,
      todayHabitLogs = t2.second,
      userProfile = t2.third,
      pastWeekActivities = t3.first,
      pastWeekSleep = t3.second,
      pastWeekHabitLogs = t3.third,
      pastWeekDates = past7Days,
      currentScreen = t4.first,
      stepTrackerStatus = t4.third,
      bannerNotification = t4.second
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = WellnessUiState(
      todayDate = todayDate,
      pastWeekDates = past7Days
    )
  )

  fun setScreen(screen: Screen) {
    _currentScreen.value = screen
  }

  fun dismissBanner() {
    _bannerNotification.value = null
  }

  private fun computeStepTrackerStatus(serviceRunning: Boolean): StepTrackerStatus {
    val app = getApplication<Application>()
    if (!StepPermissionHelper.hasStepSensor(app)) {
      return StepTrackerStatus.SENSOR_UNAVAILABLE
    }
    if (!StepPermissionHelper.hasActivityRecognitionPermission(app)) {
      return StepTrackerStatus.PERMISSION_REQUIRED
    }
    return if (serviceRunning) StepTrackerStatus.ACTIVE else StepTrackerStatus.PAUSED
  }

  fun refreshStepTrackerState(autoStartIfPermitted: Boolean = false) {
    val app = getApplication<Application>()
    val hasSensor = StepPermissionHelper.hasStepSensor(app)
    val hasPerm = StepPermissionHelper.hasActivityRecognitionPermission(app)
    val enabled = StepAnchorStore.isTrackingEnabled(app)
    val isRunning = StepTrackingStateBus.isServiceRunning.value

    if (autoStartIfPermitted && hasSensor && hasPerm && enabled && !isRunning) {
      startStepTrackingInternal()
    }
    _trackerRefreshTick.value += 1
  }

  fun onStepPermissionsResult() {
    val app = getApplication<Application>()
    if (StepPermissionHelper.hasActivityRecognitionPermission(app)) {
      StepAnchorStore.setTrackingEnabled(app, true)
      startStepTrackingInternal()
      _bannerNotification.value = "Real-time background step tracking enabled!"
    } else {
      _bannerNotification.value = "Physical activity permission is needed for live step tracking."
    }
    _trackerRefreshTick.value += 1
  }

  fun startStepTracking() {
    val app = getApplication<Application>()
    if (!StepPermissionHelper.hasStepSensor(app) || !StepPermissionHelper.hasActivityRecognitionPermission(app)) {
      _trackerRefreshTick.value += 1
      return
    }
    StepAnchorStore.setTrackingEnabled(app, true)
    startStepTrackingInternal()
    _bannerNotification.value = "Live step tracking resumed."
    _trackerRefreshTick.value += 1
  }

  fun pauseStepTracking() {
    val app = getApplication<Application>()
    StepAnchorStore.setTrackingEnabled(app, false)
    StepAnchorStore.clearBootAnchor(app)
    try {
      app.startService(StepTrackingService.pauseIntent(app))
    } catch (_: Exception) {
      StepTrackingStateBus.setServiceRunning(false)
    }
    _bannerNotification.value = "Live step tracking paused."
    _trackerRefreshTick.value += 1
  }

  private fun startStepTrackingInternal() {
    val app = getApplication<Application>()
    try {
      ContextCompat.startForegroundService(app, StepTrackingService.startIntent(app))
      StepTrackingStateBus.setServiceRunning(true)
    } catch (_: Exception) {
      StepTrackingStateBus.setServiceRunning(false)
    }
  }

  fun logSteps(stepsToAdd: Int) {
    viewModelScope.launch {
      val target = uiState.value.userProfile.targetSteps
      repository.logOrAddSteps(todayDate, stepsToAdd, target)
      _bannerNotification.value = "Added $stepsToAdd steps to today's movement log!"
    }
  }

  fun setRestDay(isRest: Boolean) {
    viewModelScope.launch {
      repository.setRestDay(todayDate, isRest)
      _bannerNotification.value = if (isRest) "Rest day mode enabled. Take it easy!" else "Active tracking restored."
    }
  }

  fun logWater(amountMl: Int, containerType: String = "Glass") {
    viewModelScope.launch {
      repository.addWaterLog(amountMl, containerType)
      _bannerNotification.value = "Logged +$amountMl ml of water ($containerType)!"
    }
  }

  fun deleteWaterLog(id: Long) {
    viewModelScope.launch {
      repository.deleteWaterLog(id)
    }
  }

  fun logSleep(bedtimeMillis: Long, wakeMillis: Long, qualityRating: Int, notes: String) {
    viewModelScope.launch {
      repository.logSleep(todayDate, bedtimeMillis, wakeMillis, qualityRating, notes)
      _bannerNotification.value = "Sleep record saved for last night!"
    }
  }

  fun toggleHabit(habitId: Long) {
    viewModelScope.launch {
      val isDone = uiState.value.todayHabitLogs.any { it.habitId == habitId }
      repository.toggleHabit(habitId, todayDate, isDone)
    }
  }

  fun addCustomHabit(title: String, category: String, iconName: String) {
    viewModelScope.launch {
      repository.addNewHabit(title, category, iconName)
      _bannerNotification.value = "New habit '$title' created!"
    }
  }

  fun deleteHabit(habitId: Long) {
    viewModelScope.launch {
      repository.deleteHabit(habitId)
      _bannerNotification.value = "Habit removed."
    }
  }

  fun updateUserProfile(profile: UserProfile) {
    viewModelScope.launch {
      repository.updateUserProfile(profile)
      _bannerNotification.value = "Settings updated successfully."
    }
  }

  fun exportData(onReady: (String) -> Unit) {
    viewModelScope.launch {
      val json = repository.exportDataAsJson()
      onReady(json)
    }
  }

  fun purgeData() {
    val app = getApplication<Application>()
    if (StepTrackingStateBus.isServiceRunning.value) {
      try {
        app.startService(StepTrackingService.pauseIntent(app))
      } catch (_: Exception) {
        StepTrackingStateBus.setServiceRunning(false)
      }
    }
    StepAnchorStore.clearAll(app)
    _trackerRefreshTick.value += 1

    viewModelScope.launch {
      repository.purgeAllUserData()
      _bannerNotification.value = "All personal health logs successfully erased."
    }
  }
}

private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
