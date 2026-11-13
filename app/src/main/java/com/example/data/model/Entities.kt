package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "activity_records",
  indices = [Index(value = ["date"], unique = true)]
)
data class ActivityRecord(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val date: String, // Format: YYYY-MM-DD
  val stepCount: Int = 0,
  val targetSteps: Int = 8000,
  val activeMinutes: Int = 0,
  val distanceKm: Float = 0f,
  val caloriesEstimated: Int = 0,
  val isRestDay: Boolean = false,
  val exerciseType: String = "Walking",
  val source: String = "Manual" // "Manual" or "HealthConnect"
)

@Entity(
  tableName = "hydration_records",
  indices = [Index(value = ["date"])]
)
data class HydrationRecord(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val timestamp: Long = System.currentTimeMillis(),
  val date: String, // Format: YYYY-MM-DD
  val amountMl: Int,
  val containerType: String = "Glass" // "Small Glass", "Glass", "Bottle", "Mug"
)

@Entity(
  tableName = "sleep_records",
  indices = [Index(value = ["date"], unique = true)]
)
data class SleepRecord(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val date: String, // Format: YYYY-MM-DD
  val bedtimeMillis: Long,
  val wakeMillis: Long,
  val durationMinutes: Int,
  val qualityRating: Int = 4, // 1 to 5 stars
  val notes: String = "",
  val source: String = "Manual" // "Manual" or "HealthConnect"
)

@Entity(tableName = "habits")
data class Habit(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val category: String, // "Movement", "Hydration", "Rest", "Mindfulness", "Nutrition"
  val iconName: String,
  val targetDaysPerWeek: Int = 7,
  val isArchived: Boolean = false
)

@Entity(
  tableName = "habit_logs",
  foreignKeys = [
    ForeignKey(
      entity = Habit::class,
      parentColumns = ["id"],
      childColumns = ["habitId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [
    Index(value = ["habitId", "date"], unique = true),
    Index(value = ["date"])
  ]
)
data class HabitLog(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val habitId: Long,
  val date: String, // Format: YYYY-MM-DD
  val isCompleted: Boolean = true,
  val completedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profiles")
data class UserProfile(
  @PrimaryKey val id: Int = 1,
  val displayName: String = "Friend",
  val targetSteps: Int = 8000,
  val targetWaterMl: Int = 2250,
  val targetSleepHours: Float = 8.0f,
  val preferredGlassSizeMl: Int = 250,
  val language: String = "en", // "en" or "hi"
  val darkModeSetting: String = "system", // "system", "light", "dark"
  val notificationsEnabled: Boolean = true,
  val healthConnectAuthorized: Boolean = false,
  val restDayMode: Boolean = false
)
