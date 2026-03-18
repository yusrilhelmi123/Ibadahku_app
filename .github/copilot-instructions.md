# MotivasiApp - Copilot Instructions

## Project Overview
MotivasiApp adalah aplikasi Android yang membantu pengguna meningkatkan hubungan mereka dengan Allah melalui tracking aktivitas ibadah harian (Shalat, Dzikir, Al-Qur'an, Doa).

## Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Database**: Room Database
- **Architecture**: MVVM
- **Build System**: Gradle with Kotlin DSL
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)

## Project Structure
```
MotivasiApp/
├── app/src/main/
│   ├── java/com/apps/motivasiapp/
│   │   ├── MainActivity.kt
│   │   ├── model/Models.kt
│   │   ├── data/
│   │   │   ├── Dao.kt
│   │   │   ├── Database.kt
│   │   │   └── Repository.kt
│   │   ├── ui/
│   │   │   ├── components/CommonComponents.kt
│   │   │   └── viewmodel/ActivityViewModel.kt
│   │   └── utils/Constants.kt
│   └── res/values/
│       ├── strings.xml
│       ├── colors.xml
│       ├── dimens.xml
│       └── themes.xml
└── build.gradle.kts
```

## Key Features
1. **Daily Activity Tracking** - Track 4 spiritual activities
2. **Progress Visualization** - Real-time progress bar
3. **Local Database** - Room database for data persistence
4. **Motivational Content** - Inspirational quotes and reminders
5. **Weekly Summary** - Track performance over weeks

## Development Guidelines

### Naming Conventions
- Classes: PascalCase (e.g., ActivityViewModel)
- Functions: camelCase (e.g., toggleActivityCompletion)
- Variables: camelCase (e.g., isCompleted)
- Constants: SCREAMING_SNAKE_CASE (e.g., ACTIVITY_PRAYER)
- Resources: snake_case (e.g., main_screen)

### Code Organization
- Keep Composable functions focused and small
- Use extension functions for common operations
- Separate concerns: UI, Data, and Business Logic
- All database operations in Repository
- All state management in ViewModel

### Kotlin Best Practices
- Use data classes for models
- Prefer immutability
- Use scope functions (let, apply, with) appropriately
- Use sealed classes for sealed hierarchies
- Leverage Kotlin features (extension functions, delegated properties)

### Compose Guidelines
- Break UI into smaller composable functions
- Use preview annotations for testing
- Manage state at appropriate levels
- Avoid nested lambdas when possible
- Use modifier chaining for complex layouts

### Database Operations
- Always use suspend functions for DB operations
- Prefer Flow for reactive updates
- Use Repository pattern for data access
- Implement proper error handling

## Important Files
- `app/build.gradle.kts` - App dependencies and configuration
- `app/src/main/AndroidManifest.xml` - App manifest with permissions
- `app/src/main/java/com/apps/motivasiapp/MainActivity.kt` - Entry point
- `app/src/main/res/values/strings.xml` - String resources
- `app/src/main/res/values/colors.xml` - Color palette

## Building & Running

### Build APK
```bash
./gradlew assembleDebug
```

### Install on Device
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Run on Emulator
Use Android Studio's Run button or:
```bash
./gradlew installDebug
```

## Common Tasks

### Adding New Activity Feature
1. Define new activity in ActivityConstants
2. Add UI component in CommonComponents
3. Update Database schema if needed
4. Add logic in ActivityViewModel
5. Update MainActivity UI

### Adding Database Migration
1. Increment version in Database.kt
2. Create migration object
3. Add to Room.databaseBuilder()

### Adding New Screen
1. Create new Composable function
2. Add ViewModel if needed
3. Update navigation if using Navigation Compose
4. Add to MainActivity or separate screen file

## Testing
- Use Compose Preview for UI testing
- Implement unit tests for ViewModels
- Use Espresso for UI automation tests
- Test database operations with Room testing library

## Performance Considerations
- Use LazyColumn for long lists
- Implement proper coroutine scopes
- Avoid unnecessary recompositions
- Use remember for expensive computations
- Profile with Android Studio Profiler

## Common Issues & Solutions

### Gradle Build Fails
- Run `./gradlew clean build`
- Check JDK version (require Java 11+)
- Invalidate caches in Android Studio

### Database Schema Mismatch
- Update version number in Database.kt
- Create proper migration or clear app data

### Compose Recomposition Issues
- Check @Composable function parameters
- Use remember for state management
- Verify modifier chains

## Version Control
- Commit frequently with meaningful messages
- Follow conventional commits
- Keep build generated files in .gitignore
- Don't commit local.properties

## Deployment Checklist
- [ ] All tests passing
- [ ] ProGuard rules configured
- [ ] app/build.gradle versionCode incremented
- [ ] AndroidManifest.xml updated
- [ ] App tested on multiple API levels
- [ ] Build for release: `./gradlew bundleRelease`

## Useful Commands
```bash
# Clean build
./gradlew clean build

# Run on device
./gradlew installDebug

# Build APK
./gradlew assembleDebug

# Generate release bundle
./gradlew bundleRelease

# Run linter
./gradlew lint

# View gradle tasks
./gradlew tasks
```

## References & Resources
- [Android Developers](https://developer.android.com)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [MVVM Architecture](https://scholar.google.com/scholar?q=MVVM+architecture+Android)

## Support & Questions
For questions about the project structure or implementation details, refer to:
1. README.md for feature documentation
2. Code comments and documentation strings
3. Android Official Documentation
4. Kotlin Standard Library Documentation
