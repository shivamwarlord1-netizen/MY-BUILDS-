package com.example.steps

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import java.util.Locale

enum class StepMilestone(
  val percentage: Int,
  val bitFlag: Int,
  val notificationId: Int
) {
  HALF_WAY(percentage = 50, bitFlag = 1 shl 0, notificationId = 4201),
  THREE_QUARTERS(percentage = 75, bitFlag = 1 shl 1, notificationId = 4202),
  GOAL_COMPLETE(percentage = 100, bitFlag = 1 shl 2, notificationId = 4203)
}

data class MilestoneEvaluationResult(
  val milestoneToNotify: StepMilestone?,
  val updatedMask: Int
)

/**
 * Pure threshold evaluator for 50%, 75%, and 100% daily step milestones.
 * Guarantees each tier is triggered at most once per day and suppresses
 * lower stale milestones if a single step batch crosses multiple tiers at once.
 */
object StepMilestoneEvaluator {
  fun evaluate(
    steps: Int,
    targetSteps: Int,
    alreadyNotifiedMask: Int
  ): MilestoneEvaluationResult {
    if (steps <= 0 || targetSteps <= 0) {
      return MilestoneEvaluationResult(
        milestoneToNotify = null,
        updatedMask = alreadyNotifiedMask
      )
    }

    val percent = ((steps.toLong() * 100L) / targetSteps.toLong()).toInt()
    val reachedTiers = StepMilestone.entries.filter { percent >= it.percentage }
    if (reachedTiers.isEmpty()) {
      return MilestoneEvaluationResult(
        milestoneToNotify = null,
        updatedMask = alreadyNotifiedMask
      )
    }

    val reachedMask = reachedTiers.fold(0) { acc, tier -> acc or tier.bitFlag }
    val newlyCrossed = reachedTiers.filter { (alreadyNotifiedMask and it.bitFlag) == 0 }
    val highestNew = newlyCrossed.maxByOrNull { it.percentage }

    return MilestoneEvaluationResult(
      milestoneToNotify = highestNew,
      updatedMask = alreadyNotifiedMask or reachedMask
    )
  }
}

/**
 * Coordinates local daily step milestone notifications:
 * - 50% & 75%: Quiet progress notification (no sound or vibration)
 * - 100%: Celebratory goal notification with default sound and vibration
 */
object StepMilestoneNotifier {
  private const val PREFS_NAME = "kinetiq_step_milestones_prefs"
  private const val KEY_DATE = "milestone_date"
  private const val KEY_MASK = "milestone_mask"

  const val CHANNEL_QUIET_ID = "kinetiq_step_milestones_quiet"
  const val CHANNEL_GOAL_ID = "kinetiq_step_milestones_goal"

  private fun prefs(context: Context) =
    context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  @Synchronized
  fun checkAndNotify(
    context: Context,
    date: String,
    steps: Int,
    targetSteps: Int
  ): StepMilestone? {
    if (date.isBlank() || steps <= 0 || targetSteps <= 0) return null

    val p = prefs(context)
    val storedDate = p.getString(KEY_DATE, null)
    val effectiveMask = if (storedDate == date) p.getInt(KEY_MASK, 0) else 0

    val evaluation = StepMilestoneEvaluator.evaluate(
      steps = steps,
      targetSteps = targetSteps,
      alreadyNotifiedMask = effectiveMask
    )

    if (storedDate != date || evaluation.updatedMask != effectiveMask) {
      p.edit()
        .putString(KEY_DATE, date)
        .putInt(KEY_MASK, evaluation.updatedMask)
        .apply()
    }

    val milestone = evaluation.milestoneToNotify ?: return null
    postMilestoneNotification(
      context = context.applicationContext,
      milestone = milestone,
      steps = steps,
      targetSteps = targetSteps
    )
    return milestone
  }

  @Synchronized
  fun clearAll(context: Context) {
    prefs(context).edit()
      .remove(KEY_DATE)
      .remove(KEY_MASK)
      .apply()
  }

  private fun postMilestoneNotification(
    context: Context,
    milestone: StepMilestone,
    steps: Int,
    targetSteps: Int
  ) {
    if (!StepPermissionHelper.hasPostNotificationsPermission(context)) return

    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

    ensureNotificationChannels(notificationManager)

    val formattedSteps = String.format(Locale.getDefault(), "%,d", steps)
    val formattedTarget = String.format(Locale.getDefault(), "%,d", targetSteps)
    val remainingSteps = (targetSteps - steps).coerceAtLeast(0)
    val formattedRemaining = String.format(Locale.getDefault(), "%,d", remainingSteps)

    val title: String
    val body: String
    val channelId: String

    when (milestone) {
      StepMilestone.HALF_WAY -> {
        channelId = CHANNEL_QUIET_ID
        title = "50% of Daily Step Goal Reached"
        body = "Halfway there with $formattedSteps steps today! $formattedRemaining steps left to hit $formattedTarget."
      }
      StepMilestone.THREE_QUARTERS -> {
        channelId = CHANNEL_QUIET_ID
        title = "75% There — Final Push!"
        body = "Strong momentum at $formattedSteps steps. Just $formattedRemaining more steps to crush your $formattedTarget goal!"
      }
      StepMilestone.GOAL_COMPLETE -> {
        channelId = CHANNEL_GOAL_ID
        title = "100% Daily Step Goal Achieved!"
        body = "Way to go! You reached $formattedSteps of your $formattedTarget daily step goal."
      }
    }

    val openIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      milestone.notificationId,
      openIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(context, channelId)
      .setSmallIcon(R.drawable.ic_step_notification)
      .setContentTitle(title)
      .setContentText(body)
      .setStyle(NotificationCompat.BigTextStyle().bigText(body))
      .setAutoCancel(true)
      .setCategory(NotificationCompat.CATEGORY_PROGRESS)
      .setContentIntent(pendingIntent)

    if (milestone == StepMilestone.GOAL_COMPLETE) {
      builder
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
    } else {
      builder
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSilent(true)
    }

    try {
      notificationManager.notify(milestone.notificationId, builder.build())
    } catch (_: SecurityException) {
      // Ignore gracefully if notification permission was revoked concurrently
    }
  }

  private fun ensureNotificationChannels(notificationManager: NotificationManager) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    if (notificationManager.getNotificationChannel(CHANNEL_QUIET_ID) == null) {
      val quietChannel = NotificationChannel(
        CHANNEL_QUIET_ID,
        "Step Progress Milestones (50% & 75%)",
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "Quiet motivational banners when you pass 50% and 75% of your daily step goal."
        setSound(null, null)
        enableVibration(false)
      }
      notificationManager.createNotificationChannel(quietChannel)
    }

    if (notificationManager.getNotificationChannel(CHANNEL_GOAL_ID) == null) {
      val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
      val audioAttrs = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

      val goalChannel = NotificationChannel(
        CHANNEL_GOAL_ID,
        "Daily Step Goal Achieved (100%)",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Celebratory alert with sound and vibration when you complete 100% of your daily step goal."
        setSound(soundUri, audioAttrs)
        enableVibration(true)
      }
      notificationManager.createNotificationChannel(goalChannel)
    }
  }
}
