# External Integrations

## Core Sections (Required)

### 1) Integration Inventory

| System | Type (API/DB/Queue/etc) | Purpose | Auth model | Criticality | Evidence |
|--------|---------------------------|---------|------------|-------------|----------|
| Room database (`pulse.db`) | DB | Stores tiles, feed items, search history, icon overrides, theme config, gesture config, and focus mode | Local app storage | High | [PulseDatabase.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/db/PulseDatabase.kt), [AppModule.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/di/AppModule.kt) |
| DataStore preferences | DB-like local storage | Stores current slide and overlay/settings flags | Local app storage | Medium | [PulsePreferencesRepository.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/repository/PulsePreferencesRepository.kt) |
| Notification listener service | Android system integration | Observes notifications for launcher surfaces | Android privileged service permission | High | [AndroidManifest.xml](/home/nana/Documents/pulse_launcher/app/src/main/AndroidManifest.xml), [PulseNotificationListenerService.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/notifications/PulseNotificationListenerService.kt) |
| Overlay services | Android system integration | Supports island and control-center style surfaces | `SYSTEM_ALERT_WINDOW` / foreground service | High | [AndroidManifest.xml](/home/nana/Documents/pulse_launcher/app/src/main/AndroidManifest.xml), [IslandService.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/island/service/IslandService.kt), [ControlCenterService.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/controlcenter/ControlCenterService.kt) |
| Retrofit/OkHttp/Gson | API client | Prepared for network-backed features | None yet; placeholder base URL in DI | Medium | [AppModule.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/di/AppModule.kt) |
| Media3 | Android media playback | Media controls and playback UI support | App-local media session usage | Medium | [app/build.gradle.kts](/home/nana/Documents/pulse_launcher/app/build.gradle.kts) |

### 2) Data Stores

| Store | Role | Access layer | Key risk | Evidence |
|-------|------|--------------|----------|----------|
| `pulse.db` (Room) | Persistent launcher state | `PulseDatabase`, DAOs, `PulseRepository` | Schema drift and destructive migration reset | [PulseDatabase.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/db/PulseDatabase.kt), [AppModule.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/di/AppModule.kt) |
| DataStore `pulse_prefs` | Small user preferences | `PulsePreferencesRepository` | Missing migration/backfill logic | [PulsePreferencesRepository.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/repository/PulsePreferencesRepository.kt) |

### 3) Secrets and Credentials Handling

- Credential sources: [TODO] no secret manager, env template, or secure credential store was found
- Hardcoding checks: the only hardcoded network target observed is a placeholder `https://api.example.com/` in `AppModule`
- Rotation or lifecycle notes: [TODO] no credential lifecycle guidance was found

### 4) Reliability and Failure Behavior

- Retry/backoff behavior: [TODO] not implemented in the inspected networking setup
- Timeout policy: OkHttp connect/read timeout is set to 15 seconds in DI
- Circuit-breaker or fallback behavior: [TODO] none was observed; the Retrofit client is configured but not yet tied to a real API

### 5) Observability for Integrations

- Logging around external calls: [TODO] no integration logging wrapper was found
- Metrics/tracing coverage: [TODO] none detected
- Missing visibility gaps: network errors, overlay permission failures, and service lifecycle issues appear undocumented and uninstrumented

### 6) Evidence

- [app/src/main/AndroidManifest.xml](/home/nana/Documents/pulse_launcher/app/src/main/AndroidManifest.xml)
- [app/src/main/java/app/pulse/launcher/data/db/PulseDatabase.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/db/PulseDatabase.kt)
- [app/src/main/java/app/pulse/launcher/data/repository/PulsePreferencesRepository.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/repository/PulsePreferencesRepository.kt)
- [app/src/main/java/app/pulse/launcher/di/AppModule.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/di/AppModule.kt)

