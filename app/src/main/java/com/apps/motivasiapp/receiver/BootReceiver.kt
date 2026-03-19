package com.apps.motivasiapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.apps.motivasiapp.utils.NotificationManager

/**
 * Fix #2: BootReceiver
 * Dijadwalkan ulang setelah HP restart karena AlarmManager kehilangan
 * semua alarm yang sudah diset ketika perangkat dimatikan/direboot.
 *
 * Alur: BOOT_COMPLETED → baca waktu sholat dari SharedPreferences
 *       → reschedule semua 5 alarm dengan waktu yang sudah dikustomisasi user
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED &&
            intent?.action != "android.intent.action.QUICKBOOT_POWERON") return

        // Nama-nama sholat sesuai urutan id (1-5)
        val sholatNames = mapOf(
            1 to "Subuh",
            2 to "Dzuhur",
            3 to "Ashar",
            4 to "Maghrib",
            5 to "Isya"
        )

        // Waktu default sebagai fallback jika belum pernah dikustomisasi
        val defaultTimes = mapOf(
            1 to "04:30",
            2 to "12:00",
            3 to "15:00",
            4 to "18:00",
            5 to "19:30"
        )

        val sharedPreferences = context.getSharedPreferences(
            "MotivasiAppPrefs",
            Context.MODE_PRIVATE
        )

        // Baca waktu kustom yang disimpan user, fallback ke default jika belum diubah
        val customTimes = (1..5).map { id ->
            val savedTime = sharedPreferences.getString("time_$id", defaultTimes[id]) ?: defaultTimes[id]!!
            Triple(
                id - 1,               // requestCode: 0-4
                sholatNames[id]!!,    // nama sholat
                savedTime             // waktu "HH:mm"
            )
        }

        // Jadwalkan ulang semua alarm dengan waktu yang sudah dikustomisasi
        NotificationManager.scheduleAllSholatWithCustomTimes(context, customTimes)
    }
}
