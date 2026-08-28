# Codebase Concerns

## Core Sections (Required)

### 1) Top Risks (Prioritized)

| Severity | Concern | Evidence | Impact | Suggested action |
|----------|---------|----------|--------|------------------|
| high | The repo has no detected test suite, which makes launcher and overlay changes risky to ship | [scan output](/home/nana/Documents/pulse_launcher/docs/codebase/.codebase-scan.txt) | Regressions in the HOME activity, overlays, and persistence could go unnoticed | Add at least unit tests for repositories/view models and one smoke instrumentation flow |
| high | `AppModule` configures Retrofit with a placeholder base URL | [AppModule.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/di/AppModule.kt) | Any network-backed feature is currently incomplete or non-functional | Replace placeholder config with a real source or remove the unused client until needed |
| high | `PulseLauncherActivity` still contains TODOs for theme and workspace extraction | [PulseLauncherActivity.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/launcher/PulseLauncherActivity.kt) | Core UI composition remains tangled at the top level | Split those composables into their intended feature files |
| medium | `PulseDatabase` uses `fallbackToDestructiveMigration()` | [PulseDatabase.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/db/PulseDatabase.kt) | Data loss is possible whenever schema changes are introduced | Add explicit migrations before storing user-important launcher state |
| medium | The manifest requests many sensitive permissions, including overlay, contacts, calendar, camera, location, Bluetooth, and notification access | [AndroidManifest.xml](/home/nana/Documents/pulse_launcher/app/src/main/AndroidManifest.xml) | Permission surface is broad, so user trust and runtime failures matter | Keep a strict permission-by-feature matrix and request only when needed |

### 2) Technical Debt

| Debt item | Why it exists | Where | Risk if ignored | Suggested fix |
|-----------|---------------|-------|-----------------|---------------|
| Placeholder UI stubs | Features are scaffolded before being fully implemented | `workspace/`, `island/`, `search/`, `controlcenter/` | Users may assume the documented experience already exists | Mark unfinished surfaces clearly and finish the core flows incrementally |
| Overloaded root activity | Fast iteration put UI bootstrapping and helper composables in one file | `PulseLauncherActivity.kt` | The startup path becomes harder to reason about | Move composables into dedicated files as the TODOs suggest |
| Destructive database migrations | Simplicity over schema safety | `PulseDatabase.kt` | Persistent config can be wiped on app update | Introduce versioned migrations and migration tests |

### 3) Security Concerns

| Risk | OWASP category (if applicable) | Evidence | Current mitigation | Gap |
|------|--------------------------------|----------|--------------------|-----|
| Broad permission request surface | A05: Security Misconfiguration | [AndroidManifest.xml](/home/nana/Documents/pulse_launcher/app/src/main/AndroidManifest.xml) | Android runtime permission gating | No clear per-feature permission strategy is documented |
| Placeholder network endpoint | A05 / N/A | [AppModule.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/di/AppModule.kt) | 15s timeouts on OkHttp | No real endpoint or transport policy is defined |
| Notification/overlay behavior without visible hardening | A01 / A05 | [AndroidManifest.xml](/home/nana/Documents/pulse_launcher/app/src/main/AndroidManifest.xml) | Uses Android service permissions | No abuse-prevention or permission rationale layer is visible |

### 4) Performance and Scaling Concerns

| Concern | Evidence | Current symptom | Scaling risk | Suggested improvement |
|---------|----------|-----------------|-------------|-----------------------|
| Home screen overlays and blur effects can be expensive on low-end devices | [SearchOverlay.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/search/SearchOverlay.kt), [ControlCenterOverlay.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/controlcenter/ControlCenterOverlay.kt) | Heavy translucent layers and animations are used | Jank on older devices or when multiple overlays stack | Profile recomposition and GPU cost, then simplify expensive effects |
| The app stores many launcher states locally without a migration strategy | [PulseDatabase.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/data/db/PulseDatabase.kt) | No non-destructive migration path is visible | Update-time resets may frustrate users | Add migrations and backup/restore validation |

### 5) Fragile/High-Churn Areas

| Area | Why fragile | Churn signal | Safe change strategy |
|------|-------------|-------------|----------------------|
| `app/src/main/java/app/pulse/launcher/launcher/PulseLauncherActivity.kt` | Startup path and helper composables are colocated | Scan shows explicit TODOs in production code | Move one concern at a time and keep the activity thin |
| `app/src/main/java/app/pulse/launcher/data/db/PulseDatabase.kt` | Schema changes affect all persisted launcher settings | Database centralizes many feature types | Add migrations and tests before changing entities |
| `app/src/main/java/app/pulse/launcher/di/AppModule.kt` | DI is the app's wiring hub | Placeholder Retrofit config suggests active churn | Replace speculative wiring with real integration only when needed |

### 6) `[ASK USER]` Questions

1. [ASK USER] Do you want this project to stay personal/private as the docs say, or should we prepare it for broader distribution and support?
2. [ASK USER] Should I treat the roadmap docs as the source of truth for future work, even when the current code has not implemented those features yet?
3. [ASK USER] Do you want me to turn the biggest gaps I found into a concrete next-step implementation plan?

### 7) Evidence

- [scan output](/home/nana/Documents/pulse_launcher/docs/codebase/.codebase-scan.txt)
- [docs/README.md](/home/nana/Documents/pulse_launcher/docs/README.md)
- [docs/PROJECT_DOCUMENT.md](/home/nana/Documents/pulse_launcher/docs/PROJECT_DOCUMENT.md)
- [app/src/main/AndroidManifest.xml](/home/nana/Documents/pulse_launcher/app/src/main/AndroidManifest.xml)
- [app/src/main/java/app/pulse/launcher/launcher/PulseLauncherActivity.kt](/home/nana/Documents/pulse_launcher/app/src/main/java/app/pulse/launcher/launcher/PulseLauncherActivity.kt)

