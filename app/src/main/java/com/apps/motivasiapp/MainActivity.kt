package com.apps.motivasiapp

import android.Manifest
import android.os.Bundle
import android.os.Build
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.apps.motivasiapp.utils.NotificationManager
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Notification permission granted
            NotificationManager.scheduleAllSholatNotifications(this)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request notification permission untuk Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // Untuk Android 12 dan bawah, langsung schedule
            NotificationManager.scheduleAllSholatNotifications(this)
        }
        
        setContent {
            MotivasiApp()
        }
    }
}

@Composable
fun MotivasiApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MotivasiAppPrefs", android.content.Context.MODE_PRIVATE)

    var currentTab by remember { mutableStateOf(TabType.HOME) }
    
    // Read from SharedPreferences
    var completedActivities by remember { 
        val saved = sharedPreferences.getStringSet("completedActivities", emptySet()) ?: emptySet()
        mutableStateOf(saved.mapNotNull { it.toIntOrNull() }.toSet()) 
    }
    
    var completedSholat by remember { 
        val saved = sharedPreferences.getStringSet("completedSholat", emptySet()) ?: emptySet()
        mutableStateOf(saved.mapNotNull { it.toIntOrNull() }.toSet()) 
    }
    
    var lastTrackedDate by remember { 
        val saved = sharedPreferences.getString("lastTrackedDate", LocalDate.now().toString())
        mutableStateOf(LocalDate.parse(saved))
    }
    
    // Auto-save inputan sementara jika berubah
    LaunchedEffect(completedActivities, completedSholat, lastTrackedDate) {
        sharedPreferences.edit()
            .putStringSet("completedActivities", completedActivities.map { it.toString() }.toSet())
            .putStringSet("completedSholat", completedSholat.map { it.toString() }.toSet())
            .putString("lastTrackedDate", lastTrackedDate.toString())
            .apply()
    }
    
    // Auto-clear inputan jika berubah hari
    LaunchedEffect(Unit) {
        while (true) {
            val today = LocalDate.now()
            if (today != lastTrackedDate) {
                // Hari sudah berganti, kosongkan data
                completedActivities = emptySet()
                completedSholat = emptySet()
                lastTrackedDate = today
            }
            // Check setiap 1 menit
            delay(60000)
        }
    }
    
    // Auto-check "Shalat Tepat Waktu" jika semua 5 sholat selesai
    LaunchedEffect(completedSholat) {
        completedActivities = if (completedSholat.size == 5) {
            completedActivities + 1  // 1 = id dari "Shalat Tepat Waktu"
        } else {
            completedActivities - 1
        }
    }

    // Fungsi simpan progres harian ke per-tanggal
    val saveDailyProgress: () -> Unit = {
        val today = LocalDate.now().toString()
        sharedPreferences.edit()
            .putStringSet("daily_${today}_activities", completedActivities.map { it.toString() }.toSet())
            .putStringSet("daily_${today}_sholat", completedSholat.map { it.toString() }.toSet())
            .putBoolean("daily_${today}_saved", true)
            .apply()
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Content based on selected tab
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (currentTab) {
                TabType.HOME -> HomeScreen(
                    completedActivities = completedActivities,
                    onActivitiesChange = { completedActivities = it },
                    completedSholat = completedSholat,
                    onSholatChange = { completedSholat = it },
                    onSaveProgress = saveDailyProgress
                )
                TabType.SUMMARY -> SummaryScreen(
                    completedActivities = completedActivities,
                    completedSholat = completedSholat,
                    sharedPreferences = sharedPreferences,
                    onReset = {
                        completedActivities = emptySet()
                        completedSholat = emptySet()
                    }
                )
                TabType.QURAN -> QuranScreen()
                TabType.ABOUT -> AboutScreen()
            }
        }
        
        // Tab Bar di bawah
        TabBar(
            currentTab = currentTab,
            onTabSelected = { currentTab = it },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

enum class TabType {
    HOME, SUMMARY, QURAN, ABOUT
}

// Helper function untuk menampilkan bulan dan tahun
fun getCurrentMonthDisplay(): String {
    val today = LocalDate.now()
    val month = today.monthValue
    val year = today.year
    
    val monthNames = mapOf(
        1 to "Januari", 2 to "Februari", 3 to "Maret",
        4 to "April", 5 to "Mei", 6 to "Juni",
        7 to "Juli", 8 to "Agustus", 9 to "September",
        10 to "Oktober", 11 to "November", 12 to "Desember"
    )
    
    return "${monthNames[month]} $year"
}

// Helper function untuk format tanggal
fun getCurrentDateDisplay(): String {
    val today = LocalDate.now()
    val dayOfWeek = today.dayOfWeek.toString().lowercase()
    val dayOfMonth = today.dayOfMonth
    val month = today.monthValue
    val year = today.year
    
    val dayNames = mapOf(
        "monday" to "Senin",
        "tuesday" to "Selasa",
        "wednesday" to "Rabu",
        "thursday" to "Kamis",
        "friday" to "Jumat",
        "saturday" to "Sabtu",
        "sunday" to "Minggu"
    )
    
    val monthNames = mapOf(
        1 to "Januari", 2 to "Februari", 3 to "Maret",
        4 to "April", 5 to "Mei", 6 to "Juni",
        7 to "Juli", 8 to "Agustus", 9 to "September",
        10 to "Oktober", 11 to "November", 12 to "Desember"
    )
    
    return "${dayNames[dayOfWeek]}, $dayOfMonth ${monthNames[month]} $year"
}

// Helper function untuk menghitung statistik mingguan real
data class WeeklyData(
    val day: String,
    val percentage: Int,
    val isSaved: Boolean = false
)

fun calculateWeeklyStatistics(
    completedActivities: Set<Int>,
    completedSholat: Set<Int>,
    sharedPreferences: android.content.SharedPreferences? = null
): List<WeeklyData> {
    val totalActivities = 9 // 4 main + 5 sholat
    val weekDays = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
    
    val today = java.time.LocalDate.now()
    // Get Monday as start of week
    val dayOfWeek = today.dayOfWeek.value // 1=Mon ... 7=Sun
    val weekStart = today.minusDays((dayOfWeek - 1).toLong()) // Monday
    
    return weekDays.mapIndexed { index, day ->
        // index 0=Sun, 1=Mon, ..., 6=Sat (tampilan)
        // Hitung tanggal: weekStart adalah Mon (index 1)
        val offset = if (index == 0) 6L else (index - 1).toLong()
        val dateForDay = weekStart.plusDays(offset)
        val dateStr = dateForDay.toString()
        
        val isToday = dateForDay == today
        // Cek apakah hari ini index weekday
        val todayValue = today.dayOfWeek.value
        val todayIndex = if (todayValue == 7) 0 else todayValue
        val isCurrentDay = index == todayIndex
        
        val isSaved = sharedPreferences?.getBoolean("daily_${dateStr}_saved", false) ?: false
        
        val percentage = when {
            isSaved -> {
                // Baca data tersimpan untuk hari tersebut
                val savedActivities = sharedPreferences?.getStringSet("daily_${dateStr}_activities", emptySet()) ?: emptySet()
                val savedSholat = sharedPreferences?.getStringSet("daily_${dateStr}_sholat", emptySet()) ?: emptySet()
                val totalCompleted = savedActivities.size + savedSholat.size
                minOf((totalCompleted * 100) / totalActivities, 100)
            }
            isCurrentDay -> {
                // Hari ini: gunakan data current (belum disimpan)
                val totalCompleted = completedActivities.size + completedSholat.size
                minOf((totalCompleted * 100) / totalActivities, 100)
            }
            else -> 0
        }
        
        WeeklyData(day, percentage, isSaved || isCurrentDay)
    }
}

// Logo Composable untuk IbadahKu
@Composable
fun IbadahKuLogo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.mipmap.ic_launcher_round),
                contentDescription = "Logo IbadahKu",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 12.dp)
            )
            // App Name
            Text(
                text = "IbadahKu",
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFF1F41BB),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp
            )
        }
        
        // Tagline
        Text(
            text = "Booster Untuk dekat dengan Allah",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF666666),
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun TabBar(
    currentTab: TabType,
    onTabSelected: (TabType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1F41BB))
            .height(60.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TabBarItem(
            label = "Beranda",
            icon = "🏠",
            isSelected = currentTab == TabType.HOME,
            onClick = { onTabSelected(TabType.HOME) },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        
        TabBarItem(
            label = "Rekapitulasi",
            icon = "📊",
            isSelected = currentTab == TabType.SUMMARY,
            onClick = { onTabSelected(TabType.SUMMARY) },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        
        TabBarItem(
            label = "Al-Qur'an",
            icon = "📖",
            isSelected = currentTab == TabType.QURAN,
            onClick = { onTabSelected(TabType.QURAN) },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        
        TabBarItem(
            label = "Tentang",
            icon = "ℹ️",
            isSelected = currentTab == TabType.ABOUT,
            onClick = { onTabSelected(TabType.ABOUT) },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

@Composable
fun TabBarItem(
    label: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = icon,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = label,
            color = if (isSelected) Color.White else Color(0xFFE8EFFE),
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun HomeScreen(
    completedActivities: Set<Int>,
    onActivitiesChange: (Set<Int>) -> Unit,
    completedSholat: Set<Int>,
    onSholatChange: (Set<Int>) -> Unit,
    onSaveProgress: () -> Unit = {}
) {
    val activities = listOf(
        ActivityItem(
            id = 1,
            title = "Shalat Tepat Waktu",
            color = Color(0xFF1F41BB),
            emoji = "🕌"
        ),
        ActivityItem(
            id = 2,
            title = "Dzikir Pagi & Petang",
            color = Color(0xFF9C27B0),
            emoji = "📿"
        ),
        ActivityItem(
            id = 3,
            title = "1 Halaman Al-Qur'an Setiap Hari",
            color = Color(0xFF4CAF50),
            emoji = "📖"
        ),
        ActivityItem(
            id = 4,
            title = "5 Menit Doa yang Jujur",
            color = Color(0xFFFF9800),
            emoji = "🤲"
        )
    )
    
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MotivasiAppPrefs", android.content.Context.MODE_PRIVATE)

    var sholatTimes by remember {
        mutableStateOf(
            listOf(
                SholatItem(id = 1, name = "Subuh", time = sharedPreferences.getString("time_1", "04:30") ?: "04:30", color = Color(0xFF1F41BB), icon = "🌙"),
                SholatItem(id = 2, name = "Dzuhur", time = sharedPreferences.getString("time_2", "12:00") ?: "12:00", color = Color(0xFFFFC107), icon = "☀️"),
                SholatItem(id = 3, name = "Ashar", time = sharedPreferences.getString("time_3", "15:00") ?: "15:00", color = Color(0xFFFF9800), icon = "🌤"),
                SholatItem(id = 4, name = "Maghrib", time = sharedPreferences.getString("time_4", "18:00") ?: "18:00", color = Color(0xFFE91E63), icon = "🌅"),
                SholatItem(id = 5, name = "Isya", time = sharedPreferences.getString("time_5", "19:30") ?: "19:30", color = Color(0xFF673AB7), icon = "🌙")
            )
        )
    }

    LaunchedEffect(sholatTimes) {
        val editor = sharedPreferences.edit()
        sholatTimes.forEach {
            editor.putString("time_${it.id}", it.time)
        }
        editor.apply()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Logo IbadahKu
        IbadahKuLogo(
            modifier = Modifier.padding(16.dp)
        )
        
        // Header section with gradient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1F41BB))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Aktivitas Ibadah Harian",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    lineHeight = 32.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // Bulan saat ini
                Text(
                    text = getCurrentMonthDisplay(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFE8EFFE),
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // Tanggal hari ini
                Text(
                    text = getCurrentDateDisplay(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE8EFFE),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
        
        // Jadwal Sholat 5 Waktu Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 20.dp)
        ) {
            Text(
                text = "Sholat 5 Waktu",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color(0xFF1F1F1F),
                modifier = Modifier.padding(bottom = 14.dp)
            )
            
            sholatTimes.forEach { sholat ->
                SholatCard(
                    sholat = sholat,
                    isCompleted = sholat.id in completedSholat,
                    onCheckedChange = { isChecked ->
                        onSholatChange(
                            if (isChecked) {
                                completedSholat + sholat.id
                            } else {
                                completedSholat - sholat.id
                            }
                        )
                    },
                    onTimeChange = { newTime ->
                        sholatTimes = sholatTimes.map { 
                            if (it.id == sholat.id) it.copy(time = newTime) else it 
                        }
                        // Fix #1: Reschedule alarm sesuai waktu baru yang diset user
                        val parts = newTime.split(":")
                        val hour = parts.getOrNull(0)?.toIntOrNull()
                        val minute = parts.getOrNull(1)?.toIntOrNull()
                        if (hour != null && minute != null) {
                            com.apps.motivasiapp.utils.NotificationManager.scheduleSingleSholatNotification(
                                context = context,
                                requestCode = sholat.id - 1, // id 1-5 → requestCode 0-4
                                name = sholat.name,
                                hour = hour,
                                minute = minute
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }
            
            // Sholat Progress
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .background(
                        color = Color(0xFF1F41BB).copy(alpha = 0.05f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Sholat Hari Ini: ${completedSholat.size}/${sholatTimes.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF1F41BB),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Progress bar for sholat
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(Color(0xFFE8EFFE), shape = RoundedCornerShape(3.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(if (sholatTimes.isEmpty()) 0f else completedSholat.size.toFloat() / sholatTimes.size)
                                .fillMaxHeight()
                                .background(Color(0xFF1F41BB), shape = RoundedCornerShape(3.dp))
                        )
                    }
                }
            }
        }

        // Activities Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 20.dp)
        ) {
            Text(
                text = "Aktivitas Lainnya",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color(0xFF1F1F1F),
                modifier = Modifier.padding(bottom = 14.dp)
            )

            activities.forEach { activity ->
                ActivityCard(
                    item = activity,
                    isCompleted = activity.id in completedActivities,
                    onCheckedChange = { isChecked ->
                        onActivitiesChange(
                            if (isChecked) {
                                completedActivities + activity.id
                            } else {
                                completedActivities - activity.id
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }
        }

        // Progress indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .background(
                    color = Color(0xFF1F41BB).copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Progres Hari Ini: ${completedActivities.size}/${activities.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF1F41BB),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(Color(0xFFE8EFFE), shape = RoundedCornerShape(3.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (activities.isEmpty()) 0f else completedActivities.size.toFloat() / activities.size)
                            .fillMaxHeight()
                            .background(Color(0xFF1F41BB), shape = RoundedCornerShape(3.dp))
                    )
                }
            }
        }

        // ===== TOMBOL SIMPAN PROGRES =====
        var showSaveSuccess by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Button(
                onClick = {
                    onSaveProgress()
                    showSaveSuccess = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1F41BB)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "💾  Simpan Progres Hari Ini",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Notifikasi sukses simpan
        AnimatedVisibility(
            visible = showSaveSuccess,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp)
        ) {
            LaunchedEffect(showSaveSuccess) {
                if (showSaveSuccess) {
                    delay(2500)
                    showSaveSuccess = false
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "✅ Progres berhasil disimpan!",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SummaryScreen(
    completedActivities: Set<Int>,
    completedSholat: Set<Int>,
    sharedPreferences: android.content.SharedPreferences? = null,
    onReset: () -> Unit = {}
) {
    var showResetDialog by remember { mutableStateOf(false) }
    
    // Data aktivitas yang tidak selesai
    val allActivities = listOf(
        Pair("Shalat Tepat Waktu", 1),
        Pair("Dzikir Pagi & Petang", 2),
        Pair("1 Halaman Al-Qur'an", 3),
        Pair("5 Menit Doa", 4)
    )
    
    val missedActivities = allActivities.filter { it.second !in completedActivities }
    val missedSholat = (1..5).filter { it !in completedSholat }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1F41BB))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Rekapitulasi",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    lineHeight = 32.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Text(
                    text = getCurrentMonthDisplay(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFE8EFFE),
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = getCurrentDateDisplay(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE8EFFE),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // Summary Stats
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Statistik Hari Ini",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF1F1F1F),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Sholat Stats
            SummaryCard(
                icon = "🕌",
                title = "Jadwal Sholat",
                completed = completedSholat.size,
                total = 5,
                color = Color(0xFF1F41BB)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Prayer Stats (Shalat Tepat Waktu)
            SummaryCard(
                icon = "📿",
                title = "Dzikir Pagi & Petang",
                completed = if (2 in completedActivities) 1 else 0,
                total = 1,
                color = Color(0xFF9C27B0)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quran Stats
            SummaryCard(
                icon = "📖",
                title = "Al-Qur'an",
                completed = if (3 in completedActivities) 1 else 0,
                total = 1,
                color = Color(0xFF4CAF50)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Doa Stats
            SummaryCard(
                icon = "🤲",
                title = "Doa",
                completed = if (4 in completedActivities) 1 else 0,
                total = 1,
                color = Color(0xFFFF9800)
            )
        }

        // Monthly Graph
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
        ) {
            Text(
                text = "Grafik Mingguan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color(0xFF1F1F1F),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F41BB).copy(alpha = 0.05f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Calculate real weekly statistics
                    val weeklyData = calculateWeeklyStatistics(completedActivities, completedSholat, sharedPreferences)
                    val maxValue = weeklyData.maxOfOrNull { it.percentage } ?: 100
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        weeklyData.forEach { data ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                // Persentase value
                                Text(
                                    text = "${data.percentage}%",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F41BB),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                
                                // Bar with height based on percentage
                                val barHeight = if (maxValue > 0 && data.percentage > 0) {
                                    ((data.percentage.toFloat() / maxValue.toFloat()) * 150).toInt().dp
                                } else {
                                    8.dp
                                }

                                val barColor = when {
                                    !data.isSaved -> Color(0xFFE0E0E0) // Abu-abu: belum ada data
                                    data.percentage >= 80 -> Color(0xFF4CAF50)
                                    data.percentage >= 60 -> Color(0xFFFFC107)
                                    data.percentage >= 40 -> Color(0xFFFF9800)
                                    else -> Color(0xFFE53935)
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height(barHeight)
                                        .background(
                                            color = barColor,
                                            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                        )
                                )
                                
                                // Day label
                                Text(
                                    text = data.day,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 6.dp),
                                    color = Color(0xFF666666)
                                )
                            }
                        }
                    }
                    
                    // Legend dan keterangan
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(
                            text = "Legenda Performa:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F1F1F),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Green legend
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(Color(0xFF4CAF50), RoundedCornerShape(2.dp))
                                )
                                Text(
                                    text = "≥80%",
                                    fontSize = 9.sp,
                                    color = Color(0xFF666666)
                                )
                            }
                            
                            // Yellow legend
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(Color(0xFFFFC107), RoundedCornerShape(2.dp))
                                )
                                Text(
                                    text = "60-79%",
                                    fontSize = 9.sp,
                                    color = Color(0xFF666666)
                                )
                            }
                            
                            // Orange legend
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(Color(0xFFFF9800), RoundedCornerShape(2.dp))
                                )
                                Text(
                                    text = "40-59%",
                                    fontSize = 9.sp,
                                    color = Color(0xFF666666)
                                )
                            }
                        }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Red legend
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(Color(0xFFE53935), RoundedCornerShape(2.dp))
                                )
                                Text(
                                    text = "<40%",
                                    fontSize = 9.sp,
                                    color = Color(0xFF666666)
                                )
                            }

                            // Gray legend
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
                                )
                                Text(
                                    text = "Belum disimpan",
                                    fontSize = 9.sp,
                                    color = Color(0xFF666666)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Aktivitas Tidak Terlaksana (Introspeksi)
        if (missedActivities.isNotEmpty() || missedSholat.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
            ) {
                Text(
                    text = "⚠️ Aktivitas Belum Terlaksana",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFFD32F2F),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Missed Sholat
                if (missedSholat.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Sholat Belum Dilakukan:",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color(0xFFD32F2F),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            val sholatNames = mapOf(
                                1 to "Subuh 🌙",
                                2 to "Dzuhur ☀️",
                                3 to "Ashar 🌤",
                                4 to "Maghrib 🌅",
                                5 to "Isya 🌙"
                            )
                            
                            missedSholat.forEach { id ->
                                Text(
                                    text = "• ${sholatNames[id]}",
                                    fontSize = 13.sp,
                                    color = Color(0xFF666666),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Missed Activities
                if (missedActivities.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Aktivitas Lainnya Belum Dilakukan:",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color(0xFFD32F2F),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            missedActivities.forEach { (name, _) ->
                                Text(
                                    text = "• $name",
                                    fontSize = 13.sp,
                                    color = Color(0xFF666666),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "💭 Gunakan ini sebagai introspeksi diri untuk meningkatkan disiplin ibadah Anda",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF999999),
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        // Reset Button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clickable { showResetDialog = true },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0B2)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🔄 Reset untuk Bulan Baru",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Tekan untuk memulai tracking baru bulan ini",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF999999)
                )
            }
        }

        // Motivational Message
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF9C4)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Terus berjuang! Setiap kegiatan ibadah yang Anda lakukan adalah investasi untuk masa depan yang lebih baik. 💪",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Medium,
                lineHeight = 22.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Reset Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    "Reset Aktivitas Bulanan",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Apakah Anda yakin ingin mereset semua aktivitas untuk memulai bulan baru? Riwayat akan disimpan otomatis.")
            },
            confirmButton = {
                Text(
                    "Ya, Reset",
                    modifier = Modifier.clickable {
                        onReset()
                        showResetDialog = false
                    },
                    color = Color(0xFFD32F2F),
                    fontWeight = FontWeight.Bold
                )
            },
            dismissButton = {
                Text(
                    "Batal",
                    modifier = Modifier.clickable { showResetDialog = false },
                    color = Color(0xFF666666)
                )
            }
        )
    }
}

@Composable
fun SummaryCard(
    icon: String,
    title: String,
    completed: Int,
    total: Int,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        fontSize = 24.sp
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF1F1F1F),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )

                    Text(
                        text = "$completed / $total",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF666666),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Progress percentage
            Text(
                text = "${(completed * 100) / maxOf(total, 1)}%",
                style = MaterialTheme.typography.labelLarge,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ActivityCard(
    item: ActivityItem,
    isCompleted: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        border = androidx.compose.material3.CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = isCompleted,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.padding(end = 12.dp)
            )

            // Title and emoji
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.emoji,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isCompleted) Color(0xFF999999) else Color(0xFF1F1F1F),
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

data class ActivityItem(
    val id: Int,
    val title: String,
    val color: Color,
    val emoji: String
)

@Composable
fun SholatCard(
    sholat: SholatItem,
    isCompleted: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onTimeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val calendar = java.util.Calendar.getInstance()
    
    val timePickerDialog = android.app.TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
            onTimeChange(formattedTime)
        },
        calendar.get(java.util.Calendar.HOUR_OF_DAY),
        calendar.get(java.util.Calendar.MINUTE),
        true
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        border = androidx.compose.material3.CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Checkbox
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = onCheckedChange,
                    modifier = Modifier.padding(end = 8.dp)
                )

                // Icon and details
                Text(
                    text = sholat.icon,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(end = 10.dp)
                )
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = sholat.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isCompleted) Color(0xFF999999) else Color(0xFF1F1F1F),
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )

                    Row(
                        modifier = Modifier
                            .clickable { timePickerDialog.show() }
                            .padding(top = 2.dp, bottom = 2.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = sholat.time,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF1F41BB),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = " ✎",
                            color = Color(0xFF1F41BB),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

data class SholatItem(
    val id: Int,
    val name: String,
    val time: String,
    val color: Color,
    val icon: String
)

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Logo
        IbadahKuLogo(
            modifier = Modifier.padding(16.dp)
        )
        
        // Header Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1F41BB))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Tentang IbadahKu",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    lineHeight = 32.sp
                )
            }
        }
        
        // Narrative Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F41BB).copy(alpha = 0.05f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Lebih Dekat dengan Pencipta",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF1F41BB),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Text(
                        text = "IbadahKu dirancang khusus sebagai asisten pintar dan pengingat setia dalam perjalanan spiritual Anda. Dengan desain elegan yang menenangkan, aplikasi ini hadir untuk memotivasi, mencatat, dan mendampingi amal ibadah harian Anda secara terstruktur.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF333333),
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Text(
                        text = "Biarkan IbadahKu menjadi pengingat lembut di sela-sela kesibukan duniawi Anda, memastikan Anda selalu memiliki waktu yang bermakna untuk Sang Maha Pencipta. Setiap rekapan adalah saksi dari konsistensi Anda.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF333333),
                        lineHeight = 22.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Developer Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.material3.CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF4CAF50).copy(alpha = 0.15f), shape = RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "👨‍💻",
                            fontSize = 24.sp
                        )
                    }
                    
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = "Dikembangkan Oleh",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF666666),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "M. Yusril Helmi Setyawan",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF1F1F1F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Version Info
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Versi 1.0.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF999999)
                )
            }
        }
    }
}

@Composable
fun QuranScreen() {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    BackHandler(enabled = webView?.canGoBack() == true) {
        webView?.goBack()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1F41BB)
            )
        }
        
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                        }
                    }
                    webChromeClient = WebChromeClient()
                    
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = true
                        displayZoomControls = false
                    }
                    
                    loadUrl("https://quran.kemenag.go.id/")
                    webView = this
                }
            },
            update = {
                webView = it
            }
        )
    }
}
