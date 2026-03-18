package com.apps.motivasiapp.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateUtils {
    private val dateFormatter = DateTimeFormatter.ISO_DATE

    fun getTodayDate(): String {
        return LocalDate.now().format(dateFormatter)
    }

    fun getDateRange(daysBack: Int): Pair<String, String> {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(daysBack.toLong())
        return Pair(
            startDate.format(dateFormatter),
            endDate.format(dateFormatter)
        )
    }

    fun getWeekStartDate(): String {
        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek.value // 1 (Monday) to 7 (Sunday)
        val startDate = today.minusDays((dayOfWeek - 1).toLong()) // Monday is start
        return startDate.format(dateFormatter)
    }

    fun formatDateForDisplay(date: String): String {
        val formatter = DateTimeFormatter.ISO_DATE
        val localDate = LocalDate.parse(date, formatter)
        val displayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        return localDate.format(displayFormatter)
    }

    fun isToday(date: String): Boolean {
        return date == getTodayDate()
    }
}

object ActivityConstants {
    const val ACTIVITY_PRAYER = "prayer"
    const val ACTIVITY_DZIKIR = "dzikir"
    const val ACTIVITY_QURAN = "quran"
    const val ACTIVITY_DOA = "doa"

    val ALL_ACTIVITIES = listOf(
        ACTIVITY_PRAYER,
        ACTIVITY_DZIKIR,
        ACTIVITY_QURAN,
        ACTIVITY_DOA
    )

    fun getActivityName(type: String): String = when (type) {
        ACTIVITY_PRAYER -> "Shalat Tepat Waktu"
        ACTIVITY_DZIKIR -> "Dzikir Pagi & Petang"
        ACTIVITY_QURAN -> "1 Halaman Al-Qur'an Setiap Hari"
        ACTIVITY_DOA -> "5 Menit Doa yang Jujur"
        else -> "Aktivitas"
    }

    fun getActivityEmoji(type: String): String = when (type) {
        ACTIVITY_PRAYER -> "🕌"
        ACTIVITY_DZIKIR -> "📿"
        ACTIVITY_QURAN -> "📖"
        ACTIVITY_DOA -> "🤲"
        else -> "✨"
    }

    fun getActivityColor(type: String): Long = when (type) {
        ACTIVITY_PRAYER -> 0xFF1F41BB
        ACTIVITY_DZIKIR -> 0xFF9C27B0
        ACTIVITY_QURAN -> 0xFF4CAF50
        ACTIVITY_DOA -> 0xFFFF9800
        else -> 0xFF1F41BB
    }
}

object QuoteConstants {
    const val MAIN_QUOTE = "Kalau dunia yang kamu rapikan, tapi hati tetap kosong, kamu akan tetap gelisah."
    const val DISCIPLINE_QUOTE = "Disiplin bukan cuma soal produktif. Tapi soal taat, dan punya tempat untuk hati ini pulang."

    val MOTIVATION_QUOTES = listOf(
        "Shalat adalah pilar agama, jangan abaikan.",
        "Dzikir adalah makanan hati yang gelisah.",
        "Al-Qur'an adalah cahaya dalam kegelapan.",
        "Doa ikhlas lebih berharga dari emas.",
        "Perjalanan seribu mil dimulai dengan satu langkah.",
        "Konsistensi adalah kunci kesuksesan spiritual.",
        "Jangan menunggu sempurna untuk memulai, mulai untuk menjadi sempurna.",
        "Setiap hari adalah kesempatan baru untuk lebih baik.",
        "Sabar adalah kunci jannah.",
        "Ingat Allah dalam setiap langkah hidupmu."
    )

    fun getRandomQuote(): String {
        return MOTIVATION_QUOTES.random()
    }
}
