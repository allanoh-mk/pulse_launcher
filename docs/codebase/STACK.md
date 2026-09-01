# Technology Stack

## Core Sections (Required)

### 1) Runtime Summary

| Area | Value | Evidence |
|------|-------|----------|
| Primary language | Kotlin (with legacy Java in Launcher3) | [build.gradle](build.gradle), source files |
| Runtime + version | Android 12+ (minSdk 31, targetSdk 35, compileSdk 35), Java 17 toolchain, Kotlin 2.0.10 | [build.gradle](build.gradle) |
| Package manager | Gradle (Groovy DSL) | [build.gradle](build.gradle), [settings.gradle](settings.gradle) |
| Module/build system | Single application built at root level, pulling in source sets (`src`, `lawnchair/src`, `quickstep/src`) plus helper modules (e.g. `:compatLib`, `:systemUIShared`, `:baseline-profile`) | [build.gradle](build.gradle), [settings.gradle](settings.gradle) |

### 2) Production Frameworks and Dependencies

| Dependency | Version | Role in system | Evidence |
|------------|---------|----------------|----------|
| Jetpack Compose | BOM 2024.06.00 | UI framework for custom Pulse components | [build.gradle](build.gradle) |
| Material 3 | 1.3.0-beta05 | UI styling and components | [build.gradle](build.gradle) |
| Room | 2.6.1 | Local database persistence | [build.gradle](build.gradle) |
| DataStore Preferences | 1.1.1 | Key-value preferences storage | [build.gradle](build.gradle) |
| Opto (by patrykmichalik) | 1.0.18 | Type-safe preferences wrapper built on DataStore | [build.gradle](build.gradle) |
| Retrofit | 2.11.0 | REST API client (with Kotlinx Serialization) | [build.gradle](build.gradle) |
| Coil Compose | 2.7.0 | Image and icon loading | [build.gradle](build.gradle) |
| Lottie | 6.5.0 | Vector animation rendering | [build.gradle](build.gradle) |
| Reorderable (by sh.calvin) | 2.3.0 | Compose drag-and-drop support | [build.gradle](build.gradle) |
| Accompanist | 0.34.0 | Permissions, Adaptive, and DrawablePainter | [build.gradle](build.gradle) |
| Smartspacer SDK | 1.0.11 | Integration with Smartspacer smart widgets | [build.gradle](build.gradle) |
| Libsu | 6.0.0 | Root shell helper library | [build.gradle](build.gradle) |

### 3) Development Toolchain

| Tool | Purpose | Evidence |
|------|---------|----------|
| Android Gradle Plugin 8.5.2 | Android build orchestration | [build.gradle](build.gradle) |
| Kotlin Compiler 2.0.10 | Kotlin compilation with Compose compiler integration | [build.gradle](build.gradle) |
| KSP 2.0.10-1.0.24 | Kotlin Symbol Processing for Room compilation | [build.gradle](build.gradle) |
| Spotless 6.25.0 | Code formatting (Google Java Format for Java, KtLint for Kotlin) | [build.gradle](build.gradle) |
| Protobuf Gradle Plugin 0.9.4 | Compiles protobuf schemas | [build.gradle](build.gradle) |

### 4) Key Commands

```bash
# Build Pulse Launcher Debug APK (convention task that runs assembleLawnWithQuickstepGithubDebug)
./gradlew assembleDebug

# Build specific variants directly
./gradlew assembleLawnWithQuickstepGithubDebug

# Run linters and format checks
./gradlew lint
./gradlew spotlessCheck
```

### 5) Environment and Config

- **Config sources:** Root [build.gradle](build.gradle) holds build types, flavors, and dependencies. [AndroidManifest-common.xml](AndroidManifest-common.xml) holds common Android declarations, while [lawnchair/AndroidManifest.xml](lawnchair/AndroidManifest.xml) and [quickstep/AndroidManifest.xml](quickstep/AndroidManifest.xml) contain flavor-specific configurations.
- **Required env vars:** `CI` (optional, to agreements/build scans), `GITHUB_REF` / `GITHUB_RUN_NUMBER` (optional, used in CI builds for version names).
- **Deployment/runtime constraints:** Android device running minSdk 31 (Android 12) or above. Requires the app to be set as default launcher:
  ```bash
  adb install -r lawnchair/build/outputs/apk/lawnWithQuickstepGithub/debug/Lawnchair.14.0.0-Beta-3.lawnWithQuickstepGithub.debug.apk
  adb shell cmd package set-home-activity app.lawnchair.debug/com.android.launcher3.Launcher
  ```

### 6) Evidence

- [build.gradle](build.gradle)
- [settings.gradle](settings.gradle)
- [gradle.properties](gradle.properties)
- [lawnchair/AndroidManifest.xml](lawnchair/AndroidManifest.xml)
