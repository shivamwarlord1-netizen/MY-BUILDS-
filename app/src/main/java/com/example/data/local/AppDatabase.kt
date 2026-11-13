package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ActivityRecord
import com.example.data.model.Habit
import com.example.data.model.HabitLog
import com.example.data.model.HydrationRecord
import com.example.data.model.SleepRecord
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    ActivityRecord::class,
    HydrationRecord::class,
    SleepRecord::class,
    Habit::class,
    HabitLog::class,
    UserProfile::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun activityDao(): ActivityDao
  abstract fun hydrationDao(): HydrationDao
  abstract fun sleepDao(): SleepDao
  abstract fun habitDao(): HabitDao
  abstract fun userProfileDao(): UserProfileDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "aura_wellness_database"
        )
          .addCallback(DatabaseCallback(scope))
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database)
          }
        }
      }

      suspend fun populateInitialData(database: AppDatabase) {
        // Default User Profile
        database.userProfileDao().upsertUserProfile(
          UserProfile(
            id = 1,
            displayName = "Athlete",
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

        // Seed foundational mindful habits
        val initialHabits = listOf(
          Habit(
            title = "Morning Hydration (1 glass)",
            category = "Hydration",
            iconName = "water_drop",
            targetDaysPerWeek = 7
          ),
          Habit(
            title = "Brisk 20-min Walk",
            category = "Movement",
            iconName = "directions_walk",
            targetDaysPerWeek = 5
          ),
          Habit(
            title = "Digital Sunset (No screens 30m before bed)",
            category = "Rest",
            iconName = "bedtime",
            targetDaysPerWeek = 7
          ),
          Habit(
            title = "Midday Posture & Stretch Break",
            category = "Mindfulness",
            iconName = "self_improvement",
            targetDaysPerWeek = 6
          ),
          Habit(
            title = "Mindful Deep Breathing (5 mins)",
            category = "Mindfulness",
            iconName = "air",
            targetDaysPerWeek = 7
          )
        )
        for (h in initialHabits) {
          database.habitDao().insertHabit(h)
        }
      }
    }
  }
}
