# Codebase Structure

## Core Sections (Required)

### 1) Top-Level Map

| Path | Purpose | Evidence |
|------|---------|----------|
| `app/` | Android application module | [settings.gradle.kts](/home/nana/Documents/pulse_launcher/settings.gradle.kts) |
| `docs/` | Product and design documentation | [docs/README.md](/home/nana/Documents/pulse_launcher/docs/README.md), [docs/12-roadmap.md](/home/nana/Documents/pulse_launcher/docs/12-roadmap.md) |
| `extracted/` | Design extraction artifacts and screenshots | [scan output](/home/nana/Documents/pulse_launcher/docs/codebase/.codebase-scan.txt) |
| `gradle/` | Version catalog and wrapper support files | [gradle/libs.versions.toml](/home/nana/Documents/pulse_launcher/gradle/libs.versions.toml) |
| `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties` | Root Gradle configuration | [scan output](/home/nana/Documents/pulse_launcher/docs/codebase/.codebase-scan.txt) |

### 2) Entry Points

- Main runtime entry: [app/src/main/java/app/pulse/launcher/launcher/PulseLauncherActivity.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/launcher/PulseLauncherActivity.kt)
- Secondary entry points: [app/src/main/java/app/pulse/launcher/settings/PulseSettingsActivity.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/settings/PulseSettingsActivity.kt), [app/src/main/java/app/pulse/launcher/island/service/IslandService.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/island/service/IslandService.kt), [app/src/main/java/app/pulse/launcher/notifications/PulseNotificationListenerService.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/notifications/PulseNotificationListenerService.kt), [app/src/main/java/app/pulse/launcher/utils/BootReceiver.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/utils/BootReceiver.kt)
- How entry is selected: HOME/LAUNCHER intent filter on `PulseLauncherActivity` in the manifest

### 3) Module Boundaries

| Boundary | What belongs here | What must not be here |
|----------|-------------------|-----------------------|
| `launcher/` | Launcher shell and home activity wiring | Business logic that belongs to reusable repositories or services |
| `workspace/` | Three-slide home surface and slide navigation | Persistence code and system services |
| `feed/`, `tiles/`, `list/` | Slide-specific UI and view models | App startup wiring |
| `search/`, `controlcenter/`, `island/` | Overlays and interaction surfaces | Database schema definitions |
| `data/` | Room entities, DAOs, repositories, DataStore access | UI composables |
| `di/` | Dependency wiring | Feature logic |
| `ui/theme/` | Shared theme tokens and styling helpers | Feature-specific state |

### 4) Naming and Organization Rules

- File naming pattern: PascalCase Kotlin files by feature, e.g. `PulseLauncherActivity.kt`, `ControlCenterOverlay.kt`
- Directory organization pattern: feature-first package layout under `app.pulse.launcher.*`
- Import aliasing or path conventions: no path aliases were detected in the inspected Kotlin/Gradle files; packages map directly to folders

### 5) Evidence

- [app/src/main/AndroidManifest.xml](/home/nana/Documents/pulse_launcher/app/src/main/AndroidManifest.xml)
- [app/src/main/java/app/pulse/launcher/launcher/PulseLauncherActivity.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/launcher/PulseLauncherActivity.kt)
- [app/src/main/java/app/pulse/launcher/workspace/WorkspaceScreen.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/workspace/WorkspaceScreen.kt)
- [docs/codebase/.codebase-scan.txt](/home/nana/Documents/pulse_launcher/docs/codebase/.codebase-scan.txt)

