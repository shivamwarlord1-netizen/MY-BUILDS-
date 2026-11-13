package com.example.steps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class StepTrackerStatus {
  ACTIVE,
  PAUSED,
  PERMISSION_REQUIRED,
  SENSOR_UNAVAILABLE
}

data class StepDeltaResult(
  val deltaSteps: Int,
  val newAnchorDate: String,
  val newLastSensorTotal: Long
)

/**
 * Pure, deterministic step-delta calculator for hardware TYPE_STEP_COUNTER readings.
 * Handles:
 * - First-ever sensor reading or post-pause re-anchoring (sets baseline with 0 delta)
 * - Calendar midnight rollover (re-anchors to the new day with 0 carried-over delta)
 * - Midday device reboot (detects sensor counter reset < previous baseline and preserves post-reboot steps)
 * - Normal monotonically increasing readings (returns exact incremental delta)
 */
object StepDeltaCalculator {
  fun computeDelta(
    anchorDate: String?,
    lastSensorTotal: Long,
    currentDate: String,
    currentSensorTotal: Long
  ): StepDeltaResult {
    val safeReading = currentSensorTotal.coerceAtLeast(0L)

    // New day, fresh install, or re-anchoring after user paused tracking: establish baseline cleanly
    if (anchorDate == null || anchorDate != currentDate || lastSensorTotal < 0L) {
      return StepDeltaResult(
        deltaSteps = 0,
        newAnchorDate = currentDate,
        newLastSensorTotal = safeReading
      )
    }

    val deltaLong = if (safeReading >= lastSensorTotal) {
      safeReading - lastSensorTotal
    } else {
      // Hardware counter reset to 0 after device reboot
      safeReading
    }

    return StepDeltaResult(
      deltaSteps = deltaLong.coerceIn(0L, Int.MAX_VALUE.toLong()).toInt(),
      newAnchorDate = currentDate,
      newLastSensorTotal = safeReading
    )
  }
}

/**
 * Persistent lightweight anchor store backed by SharedPreferences so daily step deltas
 * survive process restarts, device reboots, and midnight transitions accurately.
 */
object StepAnchorStore {
  private const val PREFS_NAME = "kinetiq_step_tracker_prefs"
  private const val KEY_ANCHOR_DATE = "anchor_date"
  private const val KEY_LAST_SENSOR_TOTAL = "last_sensor_total"
  private const val KEY_TRACKING_ENABLED = "tracking_enabled"

  private fun prefs(context: Context) =
    context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  @Synchronized
  fun recordSensorReading(
    context: Context,
    currentDate: String,
    currentSensorTotal: Long
  ): Int {
    val p = prefs(context)
    val anchorDate = p.getString(KEY_ANCHOR_DATE, null)
    val lastTotal = p.getLong(KEY_LAST_SENSOR_TOTAL, -1L)

    val result = StepDeltaCalculator.computeDelta(
      anchorDate = anchorDate,
      lastSensorTotal = lastTotal,
      currentDate = currentDate,
      currentSensorTotal = currentSensorTotal
    )

    p.edit()
      .putString(KEY_ANCHOR_DATE, result.newAnchorDate)
      .putLong(KEY_LAST_SENSOR_TOTAL, result.newLastSensorTotal)
      .apply()

    return result.deltaSteps
  }

  @Synchronized
  fun isTrackingEnabled(context: Context): Boolean {
    return prefs(context).getBoolean(KEY_TRACKING_ENABLED, true)
  }

  @Synchronized
  fun setTrackingEnabled(context: Context, enabled: Boolean) {
    prefs(context).edit()
      .putBoolean(KEY_TRACKING_ENABLED, enabled)
      .apply()
  }

  @Synchronized
  fun clearBootAnchor(context: Context) {
    prefs(context).edit()
      .putLong(KEY_LAST_SENSOR_TOTAL, -1L)
      .apply()
  }

  @Synchronized
  fun clearAll(context: Context) {
    prefs(context).edit()
      .remove(KEY_ANCHOR_DATE)
      .remove(KEY_LAST_SENSOR_TOTAL)
      .putBoolean(KEY_TRACKING_ENABLED, true)
      .apply()
  }
}

object StepPermissionHelper {
  fun hasStepSensor(context: Context): Boolean {
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
      ?: return false
    return sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null
  }

  fun hasActivityRecognitionPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACTIVITY_RECOGNITION
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      true
    }
  }

  fun requiredRuntimePermissions(): Array<String> {
    val list = mutableListOf<String>()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      list.add(Manifest.permission.ACTIVITY_RECOGNITION)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      list.add(Manifest.permission.POST_NOTIFICATIONS)
    }
    return list.toTypedArray()
  }
}

object StepTrackingStateBus {
  private val _isServiceRunning = MutableStateFlow(false)
  val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

  fun setServiceRunning(running: Boolean) {
    _isServiceRunning.value = running
  }
}
