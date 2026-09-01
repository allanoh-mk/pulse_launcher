# Codebase Structure

## Core Sections (Required)

### 1) Top-Level Map

| Path | Purpose | Evidence |
|------|---------|----------|
| `src/` | Primary AOSP Launcher3 source directory | [build.gradle](build.gradle) main sourceSet |
| `lawnchair/` | Main Lawnchair custom codebase | [build.gradle](build.gradle) lawn sourceSet, [lawnchair/AndroidManifest.xml](lawnchair/AndroidManifest.xml) |
| `lawnchair/src/app/lawnchair/` | Root package for Lawnchair's additions and customizations | [LawnchairApp.kt](lawnchair/src/app/lawnchair/LawnchairApp.kt) |
| `lawnchair/src/app/lawnchair/pulse/` | Custom Pulse Launcher features (e.g. 3-slide system) | [PulseWorkspaceHost.kt](lawnchair/src/app/lawnchair/pulse/workspace/PulseWorkspaceHost.kt) |
| `quickstep/` | Quickstep integration for Recents / gestures | [build.gradle](build.gradle) withQuickstep sourceSet |
| `platform_frameworks_libs_systemui/` | Extracted AOSP libraries (e.g. `iconloaderlib`, `searchuilib`, `animationlib`) | [settings.gradle](settings.gradle) |
| `compatLib/` | QuickSwitch compat libraries for Android versions Q to U | [settings.gradle](settings.gradle) |
| `tests/` | Android instrumented / TAPL test cases and resources | [tests/src/com/android/launcher3/](tests/src/com/android/launcher3/) |
| `docs/` | Original design specs, comparison matrices, and roadmaps | [docs/README.md](docs/README.md) |
| `extracted/` | Design assets and Stitch UI screens of Pulse Launcher | [extracted/PULSE_LAUNCHER_PROJECT.md](extracted/PULSE_LAUNCHER_PROJECT.md) |

### 2) Entry Points

- **Main runtime entry:** `com.android.launcher3.Launcher` (specifically `app.lawnchair.LawnchairLauncher` which inherits from `com.android.launcher3.uioverrides.QuickstepLauncher` -> `com.android.launcher3.Launcher`).
- **How entry is selected:** Triggered when the system handles the `android.intent.action.MAIN` and `android.intent.category.HOME` intent filters defined in [quickstep/AndroidManifest-launcher.xml](quickstep/AndroidManifest-launcher.xml).
- **Secondary entry points:**
  - `app.lawnchair.preferences.PreferenceActivity` / `PreferenceActivity2` (Settings UI entry)
  - `com.android.launcher3.LauncherReceiver` (boot and package status changes)

### 3) Module Boundaries

Since the root project acts as the main application module via Android Gradle sourceSets, boundaries are defined by packages and source directories:

| Boundary | What belongs here | What must not be here |
|----------|-------------------|-----------------------|
| `com.android.launcher3.*` (`src/`) | Stock Launcher3 core activity lifecycle, drag layer, and loading mechanisms | Composed custom screens, high-level Pulse settings, or custom overlays |
| `app.lawnchair.*` (`lawnchair/src/`) | Lawnchair specific preferences, launcher integrations, and UI custom overlays | System SystemUI internal implementation details |
| `app.lawnchair.pulse.*` | The 3-slide workspace pages, Dynamic Island, and other Pulse-specific features | Upstream Launcher3/Lawnchair unmodified utilities |
| `com.android.quickstep.*` (`quickstep/src/`) | System gesture tracking, Recents view integration, and task overlays | High-level icon overriding and app list filters |

### 4) Naming and Organization Rules

- **File naming pattern:** PascalCase Kotlin files matching the primary class/type declared, e.g., `PulseWorkspaceHost.kt`, `LawnchairLauncher.kt`.
- **Directory organization pattern:** Multi-level folder structure mapping exactly to standard Java/Kotlin package layouts (e.g. `lawnchair/src/app/lawnchair/pulse/workspace/`).
- **Path conventions:** Source directories are absolute under root but dynamically compiled depending on flavor dimensions (`lawn`, `withQuickstep`, `github`).

### 5) Evidence

- [build.gradle](build.gradle)
- [settings.gradle](settings.gradle)
- [lawnchair/AndroidManifest.xml](lawnchair/AndroidManifest.xml)
- [quickstep/AndroidManifest-launcher.xml](quickstep/AndroidManifest-launcher.xml)
- [lawnchair/src/app/lawnchair/pulse/workspace/PulseWorkspaceHost.kt](lawnchair/src/app/lawnchair/pulse/workspace/PulseWorkspaceHost.kt)
