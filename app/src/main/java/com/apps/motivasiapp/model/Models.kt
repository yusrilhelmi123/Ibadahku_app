package com.apps.motivasiapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "daily_activity")
data class DailyActivity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val activityName: String,
    val activityType: String, // "prayer", "dzikir", "quran", "doa"
    val date: String, // YYYY-MM-DD format
    val isCompleted: Boolean = false,
    val completedTime: String? = null // HH:mm format
)

@Entity(tableName = "weekly_summary")
data class WeeklySummary(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val weekStartDate: String, // YYYY-MM-DD format
    val prayerCount: Int = 0,
    val dzkirCount: Int = 0,
    val quranCount: Int = 0,
    val doaCount: Int = 0
)

data class ActivityStat(
    val type: String,
    val totalHours: Int,
    val streak: Int,
    val bestDay: String?
)
