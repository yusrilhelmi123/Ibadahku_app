# Getting Started Guide

## Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 34
- Java 11 or newer
- Kotlin 1.9.0+
- Gradle 8.1.0+

## Quick Start

### 1. Open Project in Android Studio
```
File → Open → Select MotivasiApp folder
```

### 2. Wait for Gradle Sync
- Let Android Studio sync all dependencies
- This may take a few minutes on first run

### 3. Check Android SDK
- Go to Tools → SDK Manager
- Ensure you have Android 14 (API 34) installed
- Install if needed

### 4. Create/Select Emulator
- Tools → Device Manager
- Create new Android Virtual Device (AVD) with API 34
- Or connect physical device via USB

### 5. Run the Application
- Select emulator/device from device dropdown
- Click "Run" button or press Shift + F10
- App will build and install automatically

## Development Setup

### Project Structure
- **app/** - Main application module
- **app/src/main/java/** - Kotlin source code
- **app/src/main/res/** - Resources (strings, colors, layouts)
- **build.gradle.kts** - Gradle build configuration

### Important Files to Review
1. `README.md` - Project overview and features
2. `IMPLEMENTATION.md` - Technical implementation details
3. `.github/copilot-instructions.md` - Development guidelines

### Useful Gradle Commands

**Build APK**
```bash
./gradlew assembleDebug
```

**Install on Emulator**
```bash
./gradlew installDebug
```

**Clean Project**
```bash
./gradlew clean build
```

**Check Project Structure**
```bash
./gradlew tasks
```

## Code Organization

### Main Files to Understand

1. **MainActivity.kt** - UI entry point with Compose
   ```kotlin
   @Composable
   fun MotivasiApp() { ... }
   ```

2. **Models.kt** - Data classes
   ```kotlin
   data class DailyActivity(...)
   data class WeeklySummary(...)
   ```

3. **Database.kt** - Room database setup
   ```kotlin
   @Database(entities = [...], version = 1)
   abstract class MotivasiDatabase : RoomDatabase()
   ```

4. **Repository.kt** - Data access layer
   ```kotlin
   class ActivityRepository(context: Context) { ... }
   ```

5. **ActivityViewModel.kt** - State management
   ```kotlin
   class ActivityViewModel(repository: Repository) : ViewModel()
   ```

## First Development Task Ideas

### 1. Add Notification Feature
- Add WorkManager to dependencies
- Create notification reminders for each activity
- Set custom time for notifications

### 2. Add Statistics Screen
- Create new Composable for statistics
- Query database for weekly/monthly data
- Display charts using Canvas API

### 3. Implement Dark Mode
- Create dark theme in colors.xml
- Update theme.xml
- Add theme toggle in settings

### 4. Add Settings Screen
- Create new Composable for settings
- Add preferences using DataStore
- Customize notification times

### 5. Add Data Export
- Export daily activities to CSV/PDF
- Implement file saving to downloads
- Add share functionality

## Common Issues & Solutions

### Gradle Sync Fails
```bash
./gradlew clean build --refresh-dependencies
```

### App Crashes on Launch
- Check Logcat in Android Studio
- Ensure Android SDK 34 is installed
- Clear app data: Settings → Apps → MotivasiApp → Clear data

### Database Issues
- Uninstall app from emulator
- Clear app data
- Reinstall and run

### Compose Preview Not Working
- Make sure Kotlin plugin is updated
- Invalidate caches: File → Invalidate Caches
- Rebuild project

## Debugging Tips

### Enable Verbose Logging
```bash
./gradlew assembleDebug -Porg.gradle.logging.level=debug
```

### Check Logcat for Errors
- View → Tool Windows → Logcat
- Filter by app package name
- Apply appropriate log level

### Use Android Studio Debugger
- Set breakpoint in code
- Run with Debug button (Shift + F9)
- Step through code execution

## Next Steps

1. **Explore the Code**
   - Review MainActivity.kt to understand UI
   - Check Repository.kt for data layer
   - Look at ActivityViewModel.kt for state management

2. **Test the App**
   - Run on emulator
   - Track some activities
   - Verify data is saved to database

3. **Modify & Extend**
   - Change colors in colors.xml
   - Update strings in strings.xml
   - Add new features based on ideas above

4. **Document Changes**
   - Update README.md with new features
   - Add comments to complex code
   - Keep IMPLEMENTATION.md current

## Additional Resources

- [Android Developers Documentation](https://developer.android.com)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Android Architecture Components](https://developer.android.com/topic/architecture)

## Tips for Success

✅ **Do**
- Commit frequently with meaningful messages
- Write reusable composable functions
- Use StateFlow for reactive updates
- Test on multiple API levels
- Keep functions small and focused

❌ **Don't**
- Perform database operations on main thread
- Create unnecessary recompositions
- Ignore warnings from linter
- Commit build artifacts
- Hardcode strings or colors

## Getting Help

If you encounter issues:
1. Check the error message in Logcat
2. Search GitHub issues for similar problems
3. Check Android documentation
4. Review project comments and README
5. Ask in Android development communities

---

**Happy Coding! 🚀**

"Remember: Disiplin bukan cuma soal produktif. Tapi soal taat, dan punya tempat untuk hati ini pulang."
