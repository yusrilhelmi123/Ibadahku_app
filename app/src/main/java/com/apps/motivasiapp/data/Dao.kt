package com.apps.motivasiapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.apps.motivasiapp.model.DailyActivity
import com.apps.motivasiapp.model.WeeklySummary
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyActivityDao {
    @Insert
    suspend fun insert(activity: DailyActivity)

    @Update
    suspend fun update(activity: DailyActivity)

    @Delete
    suspend fun delete(activity: DailyActivity)

    @Query("SELECT * FROM daily_activity WHERE date = :date ORDER BY activityType ASC")
    fun getActivitiesByDate(date: String): Flow<List<DailyActivity>>

    @Query("SELECT * FROM daily_activity WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getActivitiesBetweenDates(startDate: String, endDate: String): Flow<List<DailyActivity>>

    @Query("SELECT * FROM daily_activity WHERE activityType = :type AND date = :date")
    suspend fun getActivityByTypeAndDate(type: String, date: String): DailyActivity?

    @Query("SELECT COUNT(*) FROM daily_activity WHERE activityType = :type AND isCompleted = 1 AND date = :date")
    suspend fun countCompletedByTypeAndDate(type: String, date: String): Int

    @Query("SELECT COUNT(DISTINCT date) FROM daily_activity WHERE activityType = :type AND isCompleted = 1")
    suspend fun getStreakCount(type: String): Int

    @Query("DELETE FROM daily_activity WHERE date = :date")
    suspend fun deleteActivitiesByDate(date: String)
}

@Dao
interface WeeklySummaryDao {
    @Insert
    suspend fun insert(summary: WeeklySummary)

    @Update
    suspend fun update(summary: WeeklySummary)

    @Query("SELECT * FROM weekly_summary WHERE weekStartDate = :weekStartDate")
    suspend fun getSummaryByWeek(weekStartDate: String): WeeklySummary?

    @Query("SELECT * FROM weekly_summary ORDER BY weekStartDate DESC LIMIT 4")
    fun getLastFourWeeks(): Flow<List<WeeklySummary>>
}
