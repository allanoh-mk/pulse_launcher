# External Integrations

## Core Sections (Required)

### 1) Integration Inventory

| System | Type | Purpose | Auth model | Criticality | Evidence |
|--------|------|---------|------------|-------------|----------|
| Room Database (`preferences`) | Database | Stores user preferences such as custom icon overrides | Local app storage | High | [AppDatabase.kt](lawnchair/src/app/lawnchair/data/AppDatabase.kt) |
| DataStore Preferences | Local key-value store | Stores settings flags and custom configurations | Local app storage | High | [build.gradle](build.gradle) |
| Android `WallpaperManager` | System Service | Updates wallpaper offsets to provide parallax swipe movement | Android window token permission | Medium | [WorkspaceController.kt](lawnchair/src/app/lawnchair/pulse/workspace/WorkspaceController.kt) |
| Android `VibratorManager` | System Service | Fires standard tactile ticks when the workspace settles on a page | Local device framework | Low | [WorkspaceController.kt](lawnchair/src/app/lawnchair/pulse/workspace/WorkspaceController.kt) |
| Smartspacer Client SDK | API / IPC | Communicates with Smartspacer app to populate custom dashboard widgets | Android Content Provider / Intents | Medium | [build.gradle](build.gradle) |
| Retrofit + OkHttp | Network Stack | Outlines structure for future REST API calls (such as Gemini/Ollama assistant integration) | None yet (no client class is written) | Low (currently unused) | [build.gradle](build.gradle) |

### 2) Data Stores

- **Lawnchair Room Database (`preferences`):** Defines table entities like `IconOverride` representing custom icon style maps. Accessed through `AppDatabase.INSTANCE.get(context)` on main thread initialization.
- **Opto DataStore Preferences:** An elegant, type-safe settings wrapper built on top of Jetpack DataStore, managing options like grid columns, dark mode, or gestures.

### 3) Secrets and Credentials Handling

- **Credential Storage:** No web credentials or API keys (e.g. Gemini API secrets) are currently stored in the repository. If introduced, they should be gated behind Gradle properties or safe runtime entries and never committed.
- **Hardcoded Targets:** No hardcoded endpoints or staging URLs exist in the codebase.

### 4) Reliability and Failure Behavior

- **Tactile Vibrations:** Wrapped with null-safe operators (`context.getSystemService(VibratorManager::class.java)?.defaultVibrator?.vibrate(...)`) so that devices without a vibration engine (or older Android versions) fail silently.
- **Wallpaper Offset Shifts:** Guarded with window token checks and standard floating-point clamps (`normalizedOffset.coerceIn(0f, 1f)`) to prevent throwing errors to the window manager on quick rapid swipes.

### 5) Observability for Integrations

- Upstream Launcher3 logging (`android.util.Log` or `FileLog`) tracks binding failures and database helper initialization.
- **Missing visibility gaps:** Network connection statuses, LLM rate-limiting metrics, and overlay display errors will need explicit logging or fallback layers once the Dynamic Island overlay and digital assistant services are implemented.

### 6) Evidence

- [lawnchair/src/app/lawnchair/pulse/workspace/WorkspaceController.kt](lawnchair/src/app/lawnchair/pulse/workspace/WorkspaceController.kt)
- [lawnchair/src/app/lawnchair/data/AppDatabase.kt](lawnchair/src/app/lawnchair/data/AppDatabase.kt)
- [build.gradle](build.gradle)
