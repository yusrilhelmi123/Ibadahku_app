package com.apps.motivasiapp.data

import android.content.Context
import com.apps.motivasiapp.model.DailyActivity
import com.apps.motivasiapp.model.WeeklySummary
import kotlinx.coroutines.flow.Flow

class ActivityRepository(context: Context) {
    private val database = MotivasiDatabase.getDatabase(context)
    private val dailyActivityDao = database.dailyActivityDao()
    private val weeklySummaryDao = database.weeklySummaryDao()

    // Daily Activity operations
    suspend fun insertActivity(activity: DailyActivity) {
        dailyActivityDao.insert(activity)
    }

    suspend fun updateActivity(activity: DailyActivity) {
        dailyActivityDao.update(activity)
    }

    suspend fun deleteActivity(activity: DailyActivity) {
        dailyActivityDao.delete(activity)
    }

    fun getActivitiesByDate(date: String): Flow<List<DailyActivity>> {
        return dailyActivityDao.getActivitiesByDate(date)
    }

    fun getActivitiesBetweenDates(startDate: String, endDate: String): Flow<List<DailyActivity>> {
        return dailyActivityDao.getActivitiesBetweenDates(startDate, endDate)
    }

    suspend fun getActivityByTypeAndDate(type: String, date: String): DailyActivity? {
        return dailyActivityDao.getActivityByTypeAndDate(type, date)
    }

    suspend fun toggleActivityCompletion(type: String, date: String, isCompleted: Boolean) {
        val activity = getActivityByTypeAndDate(type, date) ?: return
        updateActivity(activity.copy(isCompleted = isCompleted))
    }

    suspend fun countCompletedByTypeAndDate(type: String, date: String): Int {
        return dailyActivityDao.countCompletedByTypeAndDate(type, date)
    }

    suspend fun getStreakCount(type: String): Int {
        return dailyActivityDao.getStreakCount(type)
    }

    suspend fun deleteActivitiesByDate(date: String) {
        dailyActivityDao.deleteActivitiesByDate(date)
    }

    // Weekly Summary operations
    suspend fun insertWeeklySummary(summary: WeeklySummary) {
        weeklySummaryDao.insert(summary)
    }

    suspend fun updateWeeklySummary(summary: WeeklySummary) {
        weeklySummaryDao.update(summary)
    }

    suspend fun getSummaryByWeek(weekStartDate: String): WeeklySummary? {
        return weeklySummaryDao.getSummaryByWeek(weekStartDate)
    }

    fun getLastFourWeeks(): Flow<List<WeeklySummary>> {
        return weeklySummaryDao.getLastFourWeeks()
    }

    // Initialize default activities for the day
    suspend fun initializeDailyActivities(date: String) {
        val activities = listOf(
            DailyActivity(
                activityName = "Shalat Tepat Waktu",
                activityType = "prayer",
                date = date,
                isCompleted = false
            ),
            DailyActivity(
                activityName = "Dzikir Pagi & Petang",
                activityType = "dzikir",
                date = date,
                isCompleted = false
            ),
            DailyActivity(
                activityName = "1 Halaman Al-Qur'an Setiap Hari",
                activityType = "quran",
                date = date,
                isCompleted = false
            ),
            DailyActivity(
                activityName = "5 Menit Doa yang Jujur",
                activityType = "doa",
                date = date,
                isCompleted = false
            )
        )

        activities.forEach { activity ->
            if (getActivityByTypeAndDate(activity.activityType, date) == null) {
                insertActivity(activity)
            }
        }
    }
}
