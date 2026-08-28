# Architecture

## Core Sections (Required)

### 1) Architectural Style

- Primary style: feature-oriented Android app with a layered data/UI split
- Why this classification: UI, repositories, persistence, dependency injection, and system components are separated into distinct packages, but the app is still organized primarily by launcher feature rather than by a formal clean architecture boundary
- Primary constraints: Android HOME activity requirements, overlay/service permissions, local-only persistence

### 2) System Flow

```text
HOME intent -> PulseLauncherActivity -> Compose workspace/overlays -> ViewModels/Repositories -> Room/DataStore/system services -> UI update
```

1. The system starts through the HOME/LAUNCHER activity declared in the manifest. [app/src/main/AndroidManifest.xml](/home/nana/Documents/pulse_launcher/app/src/main/AndroidManifest.xml)
2. `PulseLauncherActivity` installs Compose content and hands off to `WorkspaceScreen`. [app/src/main/java/app/pulse/launcher/launcher/PulseLauncherActivity.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/launcher/PulseLauncherActivity.kt)
3. `WorkspaceScreen` uses a pager to switch between feed, tiles, and list surfaces and triggers haptics/search behaviors. [app/src/main/java/app/pulse/launcher/workspace/WorkspaceScreen.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/workspace/WorkspaceScreen.kt)
4. Feature view models and repositories expose state for tiles, feed, theme, gestures, and focus mode. [app/src/main/java/app/pulse/launcher/data/repository/PulseRepository.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/repository/PulseRepository.kt), [app/src/main/java/app/pulse/launcher/data/repository/PulsePreferencesRepository.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/repository/PulsePreferencesRepository.kt)
5. Room and DataStore persist launcher configuration and recent state locally. [app/src/main/java/app/pulse/launcher/data/db/PulseDatabase.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/db/PulseDatabase.kt)
6. Overlays and services extend the same app shell for island, notification listening, control center, and boot handling. [app/src/main/AndroidManifest.xml](/home/nana/Documents/pulse_launcher/app/src/main/AndroidManifest.xml)

### 3) Layer/Module Responsibilities

| Layer or module | Owns | Must not own | Evidence |
|-----------------|------|--------------|----------|
| `launcher/` | Activity bootstrapping and home entry behavior | Persistence schema | [PulseLauncherActivity.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/launcher/PulseLauncherActivity.kt) |
| `workspace/` | The 3-slide home shell and pager navigation | DB creation and service setup | [WorkspaceScreen.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/workspace/WorkspaceScreen.kt) |
| `data/db/` | Room entities, DAOs, type converters, database | UI logic | [PulseDatabase.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/db/PulseDatabase.kt) |
| `data/repository/` | Mapping between persistence and UI-facing models | Compose rendering | [PulseRepository.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/repository/PulseRepository.kt) |
| `di/` | App-wide object provisioning | Feature business logic | [AppModule.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/di/AppModule.kt) |
| `island/`, `search/`, `controlcenter/` | Overlay UX surfaces | App startup config | [IslandOverlay.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/island/IslandOverlay.kt), [SearchOverlay.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/search/SearchOverlay.kt), [ControlCenterOverlay.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/controlcenter/ControlCenterOverlay.kt) |

### 4) Reused Patterns

| Pattern | Where found | Why it exists |
|---------|-------------|---------------|
| Repository | `data/repository/PulseRepository.kt` | Decouples UI-facing model shape from Room entities |
| DataStore preferences | `data/repository/PulsePreferencesRepository.kt` | Stores small launcher state without needing a relational table |
| Hilt injection | `di/AppModule.kt` and `@HiltAndroidApp` | Centralizes access to DB, DataStore, and network clients |
| Room | `data/db/PulseDatabase.kt` | Stores tiles, feed items, theme, gestures, focus mode, and icon overrides |
| Compose overlays | `search/`, `controlcenter/`, `island/` | Builds layered launcher surfaces in a single app shell |

### 5) Known Architectural Risks

- The current `AppModule` provisions a Retrofit client with a placeholder base URL, which suggests unfinished or speculative network integration.
- `PulseLauncherActivity` still contains TODOs for moving theme and workspace code out into separate files, which hints that the top-level composition is not fully settled yet.

### 6) Evidence

- [app/src/main/AndroidManifest.xml](/home/nana/Documents/pulse_launcher/app/src/main/AndroidManifest.xml)
- [app/src/main/java/app/pulse/launcher/launcher/PulseLauncherActivity.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/launcher/PulseLauncherActivity.kt)
- [app/src/main/java/app/pulse/launcher/workspace/WorkspaceScreen.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/workspace/WorkspaceScreen.kt)
- [app/src/main/java/app/pulse/launcher/data/db/PulseDatabase.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/db/PulseDatabase.kt)
- [app/src/main/java/app/pulse/launcher/di/AppModule.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/di/AppModule.kt)

