package com.apps.motivasiapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.apps.motivasiapp.model.DailyActivity
import com.apps.motivasiapp.model.WeeklySummary

@Database(
    entities = [DailyActivity::class, WeeklySummary::class],
    version = 1,
    exportSchema = false
)
abstract class MotivasiDatabase : RoomDatabase() {
    abstract fun dailyActivityDao(): DailyActivityDao
    abstract fun weeklySummaryDao(): WeeklySummaryDao

    companion object {
        @Volatile
        private var instance: MotivasiDatabase? = null

        fun getDatabase(context: Context): MotivasiDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    MotivasiDatabase::class.java,
                    "motivasi_database"
                ).build().also { instance = it }
            }
    }
}
