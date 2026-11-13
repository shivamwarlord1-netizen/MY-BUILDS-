package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.ActivityRecord
import com.example.data.model.Habit
import com.example.data.model.HabitLog
import com.example.data.model.HydrationRecord
import com.example.data.model.SleepRecord
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
  @Query("SELECT * FROM activity_records WHERE date = :date LIMIT 1")
  fun getActivityForDate(date: String): Flow<ActivityRecord?>

  @Query("SELECT * FROM activity_records WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC")
  fun getActivitiesInRange(startDate: String, endDate: String): Flow<List<ActivityRecord>>

  @Query("SELECT * FROM activity_records ORDER BY date DESC LIMIT 30")
  fun getRecentActivities(): Flow<List<ActivityRecord>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertActivity(record: ActivityRecord): Long

  @Query("SELECT * FROM activity_records")
  suspend fun getAllActivitiesRaw(): List<ActivityRecord>

  @Query("DELETE FROM activity_records")
  suspend fun deleteAllActivities()
}

@Dao
interface HydrationDao {
  @Query("SELECT * FROM hydration_records WHERE date = :date ORDER BY timestamp DESC")
  fun getHydrationLogsForDate(date: String): Flow<List<HydrationRecord>>

  @Query("SELECT SUM(amountMl) FROM hydration_records WHERE date = :date")
  fun getTotalHydrationForDate(date: String): Flow<Int?>

  @Query("SELECT * FROM hydration_records WHERE date >= :startDate AND date <= :endDate ORDER BY timestamp ASC")
  fun getHydrationInRange(startDate: String, endDate: String): Flow<List<HydrationRecord>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHydration(record: HydrationRecord): Long

  @Delete
  suspend fun deleteHydration(record: HydrationRecord)

  @Query("DELETE FROM hydration_records WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("SELECT * FROM hydration_records")
  suspend fun getAllHydrationRaw(): List<HydrationRecord>

  @Query("DELETE FROM hydration_records")
  suspend fun deleteAllHydration()
}

@Dao
interface SleepDao {
  @Query("SELECT * FROM sleep_records WHERE date = :date LIMIT 1")
  fun getSleepForDate(date: String): Flow<SleepRecord?>

  @Query("SELECT * FROM sleep_records WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC")
  fun getSleepInRange(startDate: String, endDate: String): Flow<List<SleepRecord>>

  @Query("SELECT * FROM sleep_records ORDER BY date DESC LIMIT 30")
  fun getRecentSleep(): Flow<List<SleepRecord>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertSleep(record: SleepRecord): Long

  @Query("SELECT * FROM sleep_records")
  suspend fun getAllSleepRaw(): List<SleepRecord>

  @Query("DELETE FROM sleep_records")
  suspend fun deleteAllSleep()
}

@Dao
interface HabitDao {
  @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY id ASC")
  fun getActiveHabits(): Flow<List<Habit>>

  @Query("SELECT * FROM habits ORDER BY id ASC")
  fun getAllHabits(): Flow<List<Habit>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHabit(habit: Habit): Long

  @Update
  suspend fun updateHabit(habit: Habit)

  @Query("DELETE FROM habits WHERE id = :habitId")
  suspend fun deleteHabit(habitId: Long)

  @Query("SELECT * FROM habit_logs WHERE date = :date")
  fun getHabitLogsForDate(date: String): Flow<List<HabitLog>>

  @Query("SELECT * FROM habit_logs WHERE date >= :startDate AND date <= :endDate")
  fun getHabitLogsInRange(startDate: String, endDate: String): Flow<List<HabitLog>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHabitLog(log: HabitLog): Long

  @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND date = :date")
  suspend fun deleteHabitLog(habitId: Long, date: String)

  @Query("SELECT * FROM habits")
  suspend fun getAllHabitsRaw(): List<Habit>

  @Query("SELECT * FROM habit_logs")
  suspend fun getAllHabitLogsRaw(): List<HabitLog>

  @Query("DELETE FROM habit_logs")
  suspend fun deleteAllHabitLogs()

  @Query("DELETE FROM habits")
  suspend fun deleteAllHabits()
}

@Dao
interface UserProfileDao {
  @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
  fun getUserProfile(): Flow<UserProfile?>

  @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
  suspend fun getUserProfileOnce(): UserProfile?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertUserProfile(profile: UserProfile)

  @Query("DELETE FROM user_profiles")
  suspend fun deleteProfile()
}
