package com.apps.motivasiapp.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.apps.motivasiapp.receiver.SholatAlarmReceiver
import java.util.Calendar

object NotificationManager {
    // Jadwal sholat default (dalam format HH:mm)
    data class SholatTime(val name: String, val hour: Int, val minute: Int)
    
    private val sholatSchedule = listOf(
        SholatTime("Subuh", 4, 30),
        SholatTime("Dzuhur", 12, 0),
        SholatTime("Ashar", 15, 0),
        SholatTime("Maghrib", 18, 0),
        SholatTime("Isya", 19, 30)
    )
    
    fun scheduleAllSholatNotifications(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        sholatSchedule.forEachIndexed { index, sholatTime ->
            scheduleSholatNotification(context, alarmManager, sholatTime, index)
        }
    }
    
    private fun scheduleSholatNotification(
        context: Context,
        alarmManager: AlarmManager,
        sholatTime: SholatTime,
        requestCode: Int
    ) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, sholatTime.hour)
            set(Calendar.MINUTE, sholatTime.minute)
            set(Calendar.SECOND, 0)
            
            // Jika waktu sudah lewat hari ini, set untuk besok
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        
        val intent = Intent(context, SholatAlarmReceiver::class.java).apply {
            action = "com.apps.motivasiapp.SHOLAT_ALARM"
            putExtra("sholat_name", sholatTime.name)
            putExtra("sholat_time", "${sholatTime.hour}:${String.format("%02d", sholatTime.minute)}")
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        try {
            // Set alarm yang berulang setiap hari
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun cancelAllSholatNotifications(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        sholatSchedule.forEachIndexed { index, _ ->
            val intent = Intent(context, SholatAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                index,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}
