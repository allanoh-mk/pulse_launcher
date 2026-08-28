# Coding Conventions

## Core Sections (Required)

### 1) Naming Rules

| Item | Rule | Example | Evidence |
|------|------|---------|----------|
| Files | PascalCase Kotlin files, usually one main type or screen per file | `PulseLauncherActivity.kt`, `ControlCenterOverlay.kt` | [PulseLauncherActivity.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/launcher/PulseLauncherActivity.kt), [ControlCenterOverlay.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/controlcenter/ControlCenterOverlay.kt) |
| Functions/methods | camelCase for functions and state helpers | `setCurrentSlide`, `getAllTiles`, `saveThemeConfig` | [PulsePreferencesRepository.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/repository/PulsePreferencesRepository.kt), [PulseRepository.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/repository/PulseRepository.kt) |
| Types/interfaces | PascalCase for classes, data classes, enums, DAOs | `PulseDatabase`, `TileConfigEntity`, `IslandState` | [PulseDatabase.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/db/PulseDatabase.kt), [IslandState.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/island/states/IslandState.kt) |
| Constants/env vars | `UPPER_SNAKE_CASE` for preference keys and constants | `CURRENT_SLIDE`, `IS_CONTROL_CENTER_OPEN` | [PulsePreferencesRepository.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/repository/PulsePreferencesRepository.kt) |

### 2) Formatting and Linting

- Formatter: [TODO] no formatter config file was detected in the repo root scan
- Linter: [TODO] no lint config file was detected in the repo root scan
- Most relevant enforced rules: Kotlin code style is set to `official`, AndroidX and non-transitive R classes are enabled, and Java 17 is the declared target
- Run commands: `./gradlew assembleDebug`, `./gradlew test`, `./gradlew lint`

### 3) Import and Module Conventions

- Import grouping/order: Kotlin imports are grouped by source package, with Android, Compose, app, and third-party imports interleaved in standard Kotlin style
- Alias vs relative import policy: no custom import aliasing policy was detected
- Public exports/barrel policy: no barrel files or module re-export pattern was detected

### 4) Error and Logging Conventions

- Error strategy by layer: [TODO] no explicit app-wide error-handling pattern was observed in the inspected files
- Logging style and required context fields: [TODO] no logging convention or logger wrapper was found
- Sensitive-data redaction rules: [TODO] no redaction policy file or logging guidance was found

### 5) Testing Conventions

- Test file naming/location rule: [TODO] no test files were detected in the scan output
- Mocking strategy norm: [TODO] no test framework or mock stack was detected
- Coverage expectation: [TODO] not defined in the inspected repository files

### 6) Evidence

- [gradle.properties](/home/nana/Documents/pulse_launcher/gradle.properties)
- [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts)
- [PulseLauncherActivity.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/launcher/PulseLauncherActivity.kt)
- [PulsePreferencesRepository.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/repository/PulsePreferencesRepository.kt)

