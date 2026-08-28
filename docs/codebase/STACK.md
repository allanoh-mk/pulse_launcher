# Technology Stack

## Core Sections (Required)

### 1) Runtime Summary

| Area | Value | Evidence |
|------|-------|----------|
| Primary language | Kotlin | [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts) |
| Runtime + version | Android 12+ target, compileSdk 35, Java 17 bytecode, Kotlin 2.0.0 | [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts), [gradle/libs.versions.toml](/home/nana/Documents/pulse_launcher/gradle/libs.versions.toml) |
| Package manager | Gradle with Kotlin DSL | [build.gradle.kts](/home/nana/Documents/pulse_launcher/build.gradle.kts), [settings.gradle.kts](/home/nana/Documents/pulse_launcher/settings.gradle.kts), [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts) |
| Module/build system | Single Android application module (`:app`) | [settings.gradle.kts](/home/nana/Documents/pulse_launcher/settings.gradle.kts) |

### 2) Production Frameworks and Dependencies

| Dependency | Version | Role in system | Evidence |
|------------|---------|----------------|----------|
| Jetpack Compose | BOM 2024.08.00 | UI framework for the launcher screens and overlays | [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts), [gradle/libs.versions.toml](/home/nana/Documents/pulse_launcher/gradle/libs.versions.toml) |
| Hilt | 2.51.1 | Dependency injection | [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts), [app/src/main/java/app/pulse/launcher/di/AppModule.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/di/AppModule.kt) |
| Room | 2.6.1 | Local persistence for tiles, feed items, configs, and overrides | [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts), [app/src/main/java/app/pulse/launcher/data/db/PulseDatabase.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/db/PulseDatabase.kt) |
| DataStore Preferences | 1.1.1 | Small user preferences such as current slide and overlay state | [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts), [app/src/main/java/app/pulse/launcher/data/repository/PulsePreferencesRepository.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/repository/PulsePreferencesRepository.kt) |
| Haze | 0.9.0 | Blur / glass-style UI effects | [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts), [gradle/libs.versions.toml](/home/nana/Documents/pulse_launcher/gradle/libs.versions.toml) |
| Retrofit + OkHttp + Gson | 2.11.0 / 4.12.0 / 2.11.0 | Network client stack; configured but not yet wired to a real API base URL | [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts), [app/src/main/java/app/pulse/launcher/di/AppModule.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/di/AppModule.kt) |
| Coil | 2.7.0 | Image loading for Compose | [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts) |
| WorkManager | 2.9.1 | Background work support | [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts) |
| Media3 | 1.4.1 | Media playback UI / player support | [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts) |
| Palette KTX | 1.0.0 | Color extraction / theme support | [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts) |
| Accompanist permissions + drawablepainter | 0.34.0 | Compose helpers for permissions and drawable rendering | [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts) |

### 3) Development Toolchain

| Tool | Purpose | Evidence |
|------|---------|----------|
| Android Gradle Plugin 8.5.2 | Android build orchestration | [gradle/libs.versions.toml](/home/nana/Documents/pulse_launcher/gradle/libs.versions.toml) |
| Kotlin 2.0.0 | Language compiler | [gradle/libs.versions.toml](/home/nana/Documents/pulse_launcher/gradle/libs.versions.toml) |
| KSP 2.0.0-1.0.24 | Annotation processing for Hilt and Room | [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts), [gradle/libs.versions.toml](/home/nana/Documents/pulse_launcher/gradle/libs.versions.toml) |
| AndroidJUnitRunner | Instrumented test runner declaration | [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts) |

### 4) Key Commands

```bash
./gradlew assembleDebug
./gradlew test
./gradlew connectedAndroidTest
./gradlew lint
```

### 5) Environment and Config

- Config sources: [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts), [app/src/main/AndroidManifest.xml](/home/nana/Documents/pulse_launcher/app/src/main/AndroidManifest.xml), [app/src/main/java/app/pulse/launcher/di/AppModule.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/di/AppModule.kt), [app/src/main/java/app/pulse/launcher/data/repository/PulsePreferencesRepository.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/repository/PulsePreferencesRepository.kt)
- Required env vars: [TODO] no environment-variable reads were detected in the scan output or inspected files
- Deployment/runtime constraints: Android app with HOME intent handling, overlay permission, notification listener permission, and minSdk 31

### 6) Evidence

- [build.gradle.kts](/home/nana/Documents/pulse_launcher/build.gradle.kts)
- [settings.gradle.kts](/home/nana/Documents/pulse_launcher/settings.gradle.kts)
- [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts)
- [app/src/main/AndroidManifest.xml](/home/nana/Documents/pulse_launcher/app/src/main/AndroidManifest.xml)
- [app/src/main/java/app/pulse/launcher/di/AppModule.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/di/AppModule.kt)

