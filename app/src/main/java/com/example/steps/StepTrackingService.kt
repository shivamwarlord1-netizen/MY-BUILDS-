package com.example.steps

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.repository.WellnessRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Locale

class StepTrackingService : Service(), SensorEventListener {

  private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  private lateinit var sensorManager: SensorManager
  private var stepCounterSensor: Sensor? = null
  private lateinit var repository: WellnessRepository
  private lateinit var notificationManager: NotificationManager

  private var observerJob: Job? = null
  private var currentObservedDate: String = ""
  private var isListenerRegistered = false

  override fun onCreate() {
    super.onCreate()
    sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val db = AppDatabase.getDatabase(applicationContext, serviceScope)
    repository = WellnessRepository(
      db.activityDao(),
      db.hydrationDao(),
      db.sleepDao(),
      db.habitDao(),
      db.userProfileDao()
    )

    createNotificationChannel()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    if (intent?.action == ACTION_PAUSE) {
      StepAnchorStore.setTrackingEnabled(this, false)
      StepAnchorStore.clearBootAnchor(this)
      unregisterStepListener()
      StepTrackingStateBus.setServiceRunning(false)
      ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
      stopSelf()
      return START_NOT_STICKY
    }

    if (stepCounterSensor == null || !StepPermissionHelper.hasActivityRecognitionPermission(this)) {
      StepTrackingStateBus.setServiceRunning(false)
      stopSelf()
      return START_NOT_STICKY
    }

    StepAnchorStore.setTrackingEnabled(this, true)
    startAsHealthForeground(stepsToday = 0, distanceKm = 0f, targetSteps = 8000)
    registerStepListener()
    StepTrackingStateBus.setServiceRunning(true)
    observeTodayStepsForNotification()

    return START_STICKY
  }

  override fun onSensorChanged(event: SensorEvent?) {
    if (event == null || event.sensor.type != Sensor.TYPE_STEP_COUNTER) return
    val stepsSinceBoot = event.values.firstOrNull()?.toLong() ?: return

    val todayDate = repository.getTodayDateString()
    if (todayDate != currentObservedDate) {
      observeTodayStepsForNotification()
    }

    val delta = StepAnchorStore.recordSensorReading(
      context = this,
      currentDate = todayDate,
      currentSensorTotal = stepsSinceBoot
    )

    if (delta > 0) {
      serviceScope.launch {
        val profile = repository.userProfile.firstOrNull()
        val target = profile?.targetSteps ?: 8000
        repository.recordSensorSteps(todayDate, delta, target)
      }
    }
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    // No action needed
  }

  override fun onDestroy() {
    unregisterStepListener()
    observerJob?.cancel()
    StepTrackingStateBus.setServiceRunning(false)
    serviceScope.cancel()
    super.onDestroy()
  }

  override fun onBind(intent: Intent?): IBinder? = null

  private fun registerStepListener() {
    val sensor = stepCounterSensor ?: return
    if (isListenerRegistered) return
    isListenerRegistered = sensorManager.registerListener(
      this,
      sensor,
      SensorManager.SENSOR_DELAY_UI
    )
  }

  private fun unregisterStepListener() {
    if (isListenerRegistered) {
      sensorManager.unregisterListener(this)
      isListenerRegistered = false
    }
  }

  private fun observeTodayStepsForNotification() {
    val todayDate = repository.getTodayDateString()
    if (todayDate == currentObservedDate && observerJob?.isActive == true) return
    currentObservedDate = todayDate

    observerJob?.cancel()
    observerJob = serviceScope.launch {
      repository.getActivityForDate(todayDate).collect { activity ->
        val steps = activity?.stepCount ?: 0
        val distance = activity?.distanceKm ?: 0f
        val target = activity?.targetSteps ?: 8000
        notificationManager.notify(
          NOTIFICATION_ID,
          buildNotification(steps, distance, target)
        )
      }
    }
  }

  private fun startAsHealthForeground(stepsToday: Int, distanceKm: Float, targetSteps: Int) {
    val notification = buildNotification(stepsToday, distanceKm, targetSteps)
    val fgsType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
    } else {
      0
    }
    ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, fgsType)
  }

  private fun buildNotification(stepsToday: Int, distanceKm: Float, targetSteps: Int): Notification {
    val openAppIntent = Intent(this, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val openPendingIntent = PendingIntent.getActivity(
      this,
      0,
      openAppIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val pauseIntent = Intent(this, StepTrackingService::class.java).apply {
      action = ACTION_PAUSE
    }
    val pausePendingIntent = PendingIntent.getService(
      this,
      1,
      pauseIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val formattedSteps = String.format(Locale.getDefault(), "%,d", stepsToday)
    val formattedDist = String.format(Locale.US, "%.2f", distanceKm)

    return NotificationCompat.Builder(this, CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_step_notification)
      .setContentTitle("KinetiQ Step Tracker")
      .setContentText("$formattedSteps / $targetSteps steps today • $formattedDist km")
      .setOngoing(true)
      .setOnlyAlertOnce(true)
      .setSilent(true)
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .setCategory(NotificationCompat.CATEGORY_SERVICE)
      .setContentIntent(openPendingIntent)
      .addAction(0, "Pause", pausePendingIntent)
      .build()
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        "Live Step Tracking",
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = "Shows real-time daily step progress while background step tracking is active."
        setShowBadge(false)
      }
      notificationManager.createNotificationChannel(channel)
    }
  }

  companion object {
    const val ACTION_START = "com.example.steps.ACTION_START"
    const val ACTION_PAUSE = "com.example.steps.ACTION_PAUSE"
    private const val CHANNEL_ID = "kinetiq_step_tracker"
    private const val NOTIFICATION_ID = 4102

    fun startIntent(context: Context): Intent =
      Intent(context, StepTrackingService::class.java).apply { action = ACTION_START }

    fun pauseIntent(context: Context): Intent =
      Intent(context, StepTrackingService::class.java).apply { action = ACTION_PAUSE }
  }
}
