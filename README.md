# MotivasiApp - Aplikasi Motivasi Spiritual

Aplikasi Android untuk membantu meningkatkan hubungan dengan Allah melalui tracking aktivitas ibadah sehari-hari.

## Fitur Utama

### 1. **Tracking Aktivitas Harian**
   -  Shalat Tepat Waktu
   -  Dzikir Pagi & Petang
   -  1 Halaman Al-Qur'an Setiap Hari
   -  5 Menit Doa yang Jujur

### 2. **Progress Tracker**
   - Visualisasi progres harian dalam bentuk progress bar
   - Menampilkan jumlah aktivitas yang telah diselesaikan

### 3. **Database Lokal**
   - Menyimpan setiap aktivitas yang dilakukan
   - Tracking historis per tanggal
   - Ringkasan mingguan

### 4. **Motivasi Harian**
   - Kutipan inspiratif untuk motivasi berkelanjutan
   - Reminder untuk menjalankan aktivitas spiritual

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Database**: Room Database
- **Architecture**: MVVM (Model-View-ViewModel)
- **Async**: Coroutines & Flow
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)

## Project Structure

```
MotivasiApp/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/apps/motivasiapp/
│   │       │   ├── MainActivity.kt                 # Screen utama
│   │       │   ├── model/
│   │       │   │   └── Models.kt                  # Data models (DailyActivity, WeeklySummary)
│   │       │   ├── data/
│   │       │   │   ├── Dao.kt                     # Database Access Objects
│   │       │   │   ├── Database.kt                # Room Database setup
│   │       │   │   └── Repository.kt              # Data layer
│   │       │   └── ui/
│   │       │       └── viewmodel/
│   │       │           └── ActivityViewModel.kt   # State management
│   │       ├── res/
│   │       │   ├── values/
│   │       │   │   ├── strings.xml
│   │       │   │   ├── colors.xml
│   │       │   │   ├── dimens.xml
│   │       │   │   └── themes.xml
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## Setup & Installation

### Requirements
- Android Studio Hedgehog atau lebih baru
- Android SDK 34
- Kotlin 1.9.0 atau lebih baru
- Gradle 8.1.0 atau lebih baru

### Langkah Instalasi

1. **Clone atau Download Project**
   ```bash
   cd MotivasiApp
   ```

2. **Buka di Android Studio**
   - File → Open → Pilih folder MotivasiApp

3. **Sync Gradle**
   - Android Studio akan otomatis sync dependencies

4. **Run Aplikasi**
   - Pilih device/emulator
   - Click "Run" atau tekan Shift + F10

## Cara Menggunakan

1. **Tampilan Utama**
   - Aplikasi menampilkan 4 aktivitas ibadah yang harus dilakukan hari ini
   - Header menampilkan title dan motivasi utama

2. **Track Aktivitas**
   - Centang checkbox setiap aktivitas saat selesai dikerjakan
   - Progress bar akan otomatis terupdate menampilkan progres harian

3. **Progress Visualization**
   - Lihat progress bar yang menunjukkan berapa aktivitas sudah diselesaikan
   - Motivasi tambahan ditampilkan di bawah

4. **Data Persisten**
   - Semua aktivitas disimpan ke database lokal
   - Data dapat diakses kapan saja

## Fitur yang Akan Datang

- [ ] Notifikasi reminder untuk setiap aktivitas
- [ ] Statistik mingguan & bulanan
- [ ] Dark mode support
- [ ] Share achievement ke social media
- [ ] Offline functionality yang lebih baik
- [ ] Multi-language support
- [ ] Custom reminder time untuk setiap aktivitas
- [ ] Achievement badges & rewards

## Dependencies

```kotlin
// Core Android
androidx.core:core-ktx:1.12.0
androidx.lifecycle:lifecycle-runtime-ktx:2.7.0

// Jetpack Compose
androidx.activity:activity-compose:1.8.1
androidx.compose.bom:2023.12.00
androidx.compose.material3:material3:1.1.2

// Room Database
androidx.room:room-runtime:2.6.1
androidx.room:room-ktx:2.6.1

// DataStore
androidx.datastore:datastore-preferences:1.0.0

// Navigation
androidx.navigation:navigation-compose:2.7.5
```

## Database Schema

### DailyActivity Table
```sql
CREATE TABLE daily_activity (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    activityName TEXT NOT NULL,
    activityType TEXT NOT NULL,
    date TEXT NOT NULL,
    isCompleted INTEGER DEFAULT 0,
    completedTime TEXT
)
```

### WeeklySummary Table
```sql
CREATE TABLE weekly_summary (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    weekStartDate TEXT NOT NULL,
    prayerCount INTEGER DEFAULT 0,
    dzkirCount INTEGER DEFAULT 0,
    quranCount INTEGER DEFAULT 0,
    doaCount INTEGER DEFAULT 0
)
```

## API Reference

### ActivityRepository
- `insertActivity(activity: DailyActivity)` - Tambah aktivitas
- `updateActivity(activity: DailyActivity)` - Update aktivitas
- `getActivitiesByDate(date: String)` - Ambil aktivitas per tanggal
- `toggleActivityCompletion(type: String, date: String, isCompleted: Boolean)` - Toggle aktivitas
- `initializeDailyActivities(date: String)` - Inisialisasi aktivitas harian

### ActivityViewModel
- `dailyActivities: StateFlow<List<DailyActivity>>` - List aktivitas hari ini
- `completedCount: StateFlow<Int>` - Jumlah aktivitas selesai
- `toggleActivityCompletion(activity: DailyActivity)` - Toggle aktivitas
- `refreshActivities()` - Refresh list aktivitas

## Coding Guidelines

### Kotlin Style
- Gunakan nullable types dengan bijak
- Prefer immutable data classes
- Use scope functions (let, apply, with) secara tepat

### Compose
- Break down UI menjadi composable functions yang kecil
- Gunakan preview untuk testing UI
- Avoid state hoisting yang berlebihan

### Database
- Selalu gunakan coroutines untuk DB operations
- Prefer Flow untuk reactive updates
- Close database connections properly

## Troubleshooting

### Build Error
```bash
# Clean dan rebuild
./gradlew clean build
```

### Gradle Sync Failed
- Pastikan Gradle version di `gradle/wrapper/gradle-wrapper.properties`
- Buka Gradle Console untuk melihat detail error

### Database Migration Error
- Tutup & buka ulang emulator
- Atau uninstall & reinstall app

## Contributing

Untuk kontribusi:
1. Fork repository
2. Buat branch fitur (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push ke branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

## License

Project ini dilisensikan di bawah MIT License - lihat file LICENSE untuk details.

## Author

**Motivasi App Team**
- Mengembangkan aplikasi untuk meningkatkan spritual quotient melalui teknologi

## Contact & Support

Untuk pertanyaan atau support:
- Email: support@motivasiapp.com
- GitHub Issues: [Report Issues](https://github.com/motivasiapp/issues)

---

**Terakhir diupdate**: Maret 2026

*"Disiplin bukan cuma soal produktif. Tapi soal taat, dan punya tempat untuk hati ini pulang."*
