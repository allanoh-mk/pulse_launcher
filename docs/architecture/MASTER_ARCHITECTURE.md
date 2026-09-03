# Pulse Launcher — Master Architectural Plan (All 7 Milestones)

> **Status:** Architectural Blueprint — Pre-Implementation
> **Target:** Android 15 (API 35), minSdk 31
> **Base:** Lawnchair 14 fork (LawnchairLauncher → QuickstepLauncher → Launcher3)
> **Date:** 2026-09-02

---

## 0. Executive Summary

This document defines the **complete architectural plan** for Pulse Launcher before any new code is written. It covers:

- A canonical **package topology** that organizes all 7 milestones into clean module boundaries.
- A **dependency graph** showing which subsystems must be built first.
- **Per-milestone architectural decisions**: data models, Compose contracts, threading, persistence.
- **Cross-cutting concerns**: theming, motion, persistence, observability, security.
- **Acceptance criteria** for each milestone, mapped to runnable Gradle checks.

The plan is intentionally **read-only documentation** at this stage — no source-code rewrites until the architectural contracts are agreed upon.

---

## 1. Canonical Package Topology

All Pulse code lives under `lawnchair/src/app/lawnchair/pulse/`. New modules follow this layout:

```
app/lawnchair/pulse/
├── core/                    # Cross-cutting infra (theme, motion, prefs, capability detection)
│   ├── PulsePreferences     # Opto-backed typed prefs
│   ├── PulseTheme           # MaterialTheme wrapper, HCT palette provider
│   ├── PulseMotion          # Spring curves, transition spec
│   ├── PulseCapabilities    # Feature flags from Build.VERSION / DeviceInfo
│   └── PulseHaptics         # Tactile feedback intensity abstraction
│
├── workspace/               # 3-slide topology (Feed / Tiles / List) — M1
│   ├── PulseWorkspaceHost   # Compose root attached to LawnchairLauncher.dragLayer
│   ├── PulseWorkspace        # HorizontalPager with parallax
│   ├── FeedPage              # Slide 1
│   ├── TileGridPage          # Slide 2 (Bento)
│   ├── ListPage              # Slide 3 (Niagara-style)
│   ├── WorkspaceController   # Page index, gesture router
│   └── AlphabetIndexMath     # Pure-Kotlin scroll math
│
├── data/                    # Persistence layer (Room + DataStore)
│   ├── db/
│   │   ├── PulseDatabase            # Single Room DB
│   │   ├── IconStyleConfig / Dao    # Per-app icon overrides — M4
│   │   ├── TileConfig / Dao         # Bento cell layouts — M5
│   │   ├── FocusModeConfig / Dao    # Scheduled focus rules — M6
│   │   ├── AiProviderSetting / Dao  # Provider creds metadata — M3
│   │   └── PulseConverters
│   ├── pulseprefs/                   # EncryptedDataStore wrappers — M3
│   └── repositories/
│       ├── WeatherRepository         # Open-Meteo — M1
│       ├── CalendarRepository        # CalendarContract — M1
│       ├── ContactsRepository        # ContactsContract — M2
│       ├── MediaRepository           # MediaStore — M2
│       ├── BackupRepository          # JSON / .novabackup — M7
│       └── PrivateSpaceRepository    # Android 15 — M6
│
├── search/                  # Universal search — M2
│   ├── SearchViewModel
│   ├── SearchState / SearchResult
│   ├── UnifiedSearchOverlay  # Compose UI
│   ├── dispatch/
│   │   ├── AppsSearcher
│   │   ├── ContactsSearcher
│   │   ├── FilesSearcher
│   │   ├── ShortcutsSearcher
│   │   ├── MathSearcher     # offline eval + unit conversion
│   │   └── WebSearcher      # intent dispatch
│   └── history/             # local search history — M2
│
├── iconstudio/              # AGSL shaders + per-app overrides — M4
│   ├── IconRenderer         # Canvas + AGSL pipeline
│   ├── IconStyle            # sealed class: MATERIAL_YOU / LIQUID_GLASS / NEON / EMBOSSED / DUOTONE / HOLOGRAPHIC / FILM_GRAIN
│   └── IconStudioSettingsScreen
│
├── fontstudio/              # Custom fonts — M4
│   ├── PulseFontFamilies
│   ├── FontCatalog          # Google Fonts downloader
│   └── FontStudioSettingsScreen
│
├── theming/                 # Material You + HCT fine-tune — M4
│   ├── HctPaletteExtractor  # Wallpaper → tonal palette
│   ├── CircadianToneShifter # Day/night temperature curve
│   └── ThemeOverrides       # Chroma/Tone/Hue sliders
│
├── island/                  # Dynamic Island overlay — M3
│   ├── IslandService        # Foreground service + TYPE_APPLICATION_OVERLAY
│   ├── IslandOverlay        # Compose overlay
│   ├── IslandStateMachine   # COMPACT ↔ EXPANDED transitions
│   ├── IslandState          # Media / Timer / Call / Battery / Privacy
│   ├── MediaSessionObserver # MediaSessionManager listener
│   └── AssistantChatBubble  # Compact AI bubble mode
│
├── assistant/               # Multi-provider AI — M3
│   ├── AssistantViewModel
│   ├── AssistantState
│   ├── KeystoreHelper       # AES-GCM key wrapping
│   ├── SseReader            # OkHttp SSE parser
│   ├── providers/
│   │   ├── AiProvider       # interface
│   │   ├── AnthropicProvider
│   │   ├── OpenAiProvider
│   │   ├── GeminiProvider
│   │   ├── GroqProvider
│   │   └── OllamaProvider
│   ├── failover/            # Circuit-breaker chain
│   └── KeyStore + DataStore wiring
│
├── controlcenter/           # System toggles overlay — M1
│   ├── ControlCenterViewModel
│   ├── ControlCenterState
│   ├── ControlCenterOverlay
│   └── SystemBridge         # CameraManager flashlight, Settings.Panel, etc.
│
├── gestures/                # Custom gesture dispatcher — M6
│   ├── GestureMap           # 1/2-finger swipes, edge swipes
│   ├── LetterDrawRecognizer # "S" → Spotify
│   └── RadialLongPressMenu
│
├── focus/                   # Scheduled focus modes — M6
│   ├── FocusModeScheduler
│   ├── FocusTileHider
│   └── FocusNotifFilter
│
├── backup/                  # JSON + Nova parser — M7
│   ├── PulseBackupCodec
│   ├── WebDavUploader
│   └── NovaBackupParser
│
├── gpu/                     # Adaptive 3-tier renderer — M7
│   ├── AdaptiveGpuTier
│   ├── BatteryMonitor
│   └── RenderEffectGate
│
├── notifications/           # PulseNotifications (existing) — extended in M6
│
└── splash/
    └── PulseSplashScreen    # Boot animation

```

---

## 2. Dependency Graph (Build Order)

```
              ┌─────────────────────────────────────────┐
              │  M1: Core Stubs + Foundation (CRITICAL) │
              │  pulsecore, workspace, data/db,         │
              │  controlcenter, notifications           │
              └───────────────┬─────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌───────────────┐   ┌──────────────────┐   ┌────────────────┐
│ M2: Universal │   │ M4: Icon/Font/   │   │ M3: Island +   │
│   Search      │   │   Theming        │   │   AI Engine    │
│ (needs core,  │   │ (needs core +    │   │ (needs core +  │
│  data/db)     │   │  data/db + icon) │   │  notifications)│
└──────┬────────┘   └─────────┬────────┘   └────────┬───────┘
       │                      │                     │
       └──────────┬───────────┘                     │
                  ▼                                 │
           ┌─────────────────┐                      │
           │ M5: Bento Tiles │◀─────────────────────┘
           │   Ecosystem     │  (icons + AI feed tiles)
           └────────┬────────┘
                    ▼
           ┌─────────────────────┐    ┌──────────────────┐
           │ M6: Gestures, Focus │───▶│ M7: Backup + GPU │
           │   Private Space     │    │   Adaptive Tier  │
           └─────────────────────┘    └──────────────────┘
```

**Critical path:** M1 → M4 → M5 → M7
**Parallelizable:** M2 (after M1), M3 (after M1), M6 (after M5)

---

## 3. Per-Milestone Architectural Decisions

### M1 — Core Stubs & Foundation
**Goal:** Eliminate every placeholder, wire data to UI, modernize deprecated APIs.

| Concern | Decision |
|--------|----------|
| Data layer | Single Room DB `pulse.db` with `IconStyleConfig`, `TileConfig`, `FocusModeConfig`, `AiProviderSetting` tables. |
| Repository pattern | All platform accessors (CalendarContract, ContactsContract, MediaStore, SystemSettings) hidden behind `data/repositories/*` interfaces. ViewModels never call `context.contentResolver` directly. |
| Threading | `viewModelScope` for everything; IO via `Dispatchers.IO`; results flow through `StateFlow`. |
| Compose | One root `PulseWorkspaceHost.attach(launcher)` adds a single `ComposeView` to `dragLayer`. Three slides via `HorizontalPager` with 0.7× wallpaper parallax. |
| Weather | `WeatherRepository` hits Open-Meteo (free, no key), 30-min cache in `DataStore`. |
| Calendar | `CalendarRepository` reads `CalendarContract.Instances` for next 24h events. |
| Control Center | New `SystemBridge` class wraps `Settings.Panel` for Wi-Fi, `CameraManager` for flashlight, `Settings.System` for rotation. Deprecated `wifiManager.isWifiEnabled` removed. |

### M2 — Universal Search
**Goal:** Offline-capable, parallel async search across 6 domains.

| Concern | Decision |
|--------|----------|
| Concurrency | Each `Searcher` runs in its own `async {}` coroutine inside `viewModelScope`. Results merged via `Flow.combine`. |
| Math | Bundled `exp4j` library (pure Java, MIT) for safe arithmetic; unit conversion via hand-written factor table. NO network for math. |
| Contacts | `ContactsContract.CommonDataKinds.Phone.CONTENT_URI` with `READ_CONTACTS` runtime permission gating in `UnifiedSearchOverlay`. |
| Files | `MediaStore.Files` query, scoped storage aware. |
| Shortcuts | `LauncherApps.getShortcuts()` with `ShortcutQuery` filtered by query. |
| Web | Intent dispatch — provider chosen from settings. |
| History | Local `search_history` Room table with one-tap clear. |

### M3 — Dynamic Island & Multi-Provider AI
**Goal:** Persistent overlay with live activity state machine + resilient SSE streaming.

| Concern | Decision |
|--------|----------|
| Service | `IslandService` is `Service` (not `ForegroundService`) but uses `startForeground` with `FOREGROUND_SERVICE_TYPE_SPECIAL_USE` (API 34+). Overlay via `WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY`. |
| State machine | `IslandStateMachine` exposes `StateFlow<IslandState>`. Compact ↔ Expanded via `AnimatedContent`. |
| Privacy dots | `AppOpsManager` poll every 500ms when expanded; privacy dots render in `IslandOverlay`. |
| AI providers | `AiProvider` interface returns `Flow<AssistantChunk>`. Anthropic / OpenAI / Gemini / Groq / Ollama all implemented. |
| Failover | `AssistantViewModel` chains providers; on `429` or 5xx, transparently switches to next. Chat history preserved. |
| Security | API keys encrypted with Android KeyStore (`AES/GCM/NoPadding`) wrapping, persisted in EncryptedSharedPreferences / DataStore. |

### M4 — Icon Studio / Font Studio / Theming
**Goal:** GPU shader pipeline + typography system + HCT fine-tune.

| Concern | Decision |
|--------|----------|
| Shaders | AGSL `RuntimeShader` for styles requiring GPU (`HOLOGRAPHIC`, `FILM_GRAIN`, `LIQUID_GLASS`). Pure Canvas for `MATERIAL_YOU`, `NEON`, `EMBOSSED`, `DUOTONE`. |
| Performance | Shader compiled lazily on first use; cached in `IconRenderer` LRU map. |
| Per-app override | `IconStyleConfig` Room table keyed by `packageName`. Fallback to global style. |
| Font loader | `androidx.core.content.res.ResourcesCompat` for bundled TTFs. Google Fonts fetched to `cacheDir/pulse-fonts/` with `FontRequest`. |
| HCT | Hand-rolled port of Material Color Utilities (HCT color space). Wallpaper extraction via `WallpaperManager.getDrawable()` → scaled bitmap → palette extraction. |

### M5 — Bento Tile Ecosystem
**Goal:** Modular widget grid with 3D flip + frosted glass folders.

| Concern | Decision |
|--------|----------|
| Grid model | `TileConfig` table: `(rowSpan, colSpan, pageIndex, type, payloadJson)`. |
| Drag & drop | `Reorderable` (sh.calvin) with custom `DragAndDropTarget` for cross-cell drag. |
| Snap-to-grid | Snap during drag using `AlphabetIndexMath.snap()` algorithm. |
| 3D flip | `graphicsLayer { rotationY }` with `AnimatedContent` between front/back states. |
| Folder dashboard | Full-screen modal `ModalBottomSheet` with `blur(20.dp)` on background. |

### M6 — Gestures, Focus Modes, Private Space
**Goal:** Power-user ergonomics layer.

| Concern | Decision |
|--------|----------|
| Gesture detection | Custom `GestureDetectorCompat` chain in `PulseWorkspaceHost`. Maps registered in `GestureMap` keyed by gesture string. |
| Letter draw | `LetterDrawRecognizer` uses `$1` stroke recognition algorithm on top of `MotionEvent` stream. |
| Focus modes | `FocusModeScheduler` uses `WorkManager` periodic worker (15-min interval) checking time-of-day rules from `FocusModeConfig`. |
| Private space | Android 15 `RoleManager` + `BiometricPrompt`. Apps moved via `LauncherApps.moveToPrivateSpace()` (API 35). |

### M7 — Backups & Adaptive GPU
**Goal:** Data portability + battery-aware performance.

| Concern | Decision |
|--------|----------|
| Backup format | Single JSON schema versioned by `schemaVersion`. Contains all Room DB rows + DataStore prefs. |
| Resolution independence | Coordinates stored as fractions (0..1) of grid; restored by multiplying by target device grid. |
| Nova parser | Read `.novabackup` SQLite (when present), map `HomeItem` → Pulse tile/app entries. |
| GPU tiers | Tier 1 = full AGSL (battery > 50%, charging or AC). Tier 2 = static RenderEffect blur (20–50%). Tier 3 = flat alpha, no shader (below 20% or low-power mode). `BatteryManager` polled every 60s. |

---

## 4. Cross-Cutting Concerns

### Theming
- **Single source of truth:** `PulseTheme` Composable wraps Material 3.
- **Cascade rule:** Wallpaper-derived HCT palette → `ColorScheme` → `LocalPulseColors` CompositionLocal → all components read from it.
- **Overrides:** `ThemeOverrides` allows per-element hue/chroma/tone nudge without recompiling palette.

### Motion
- **Spring defaults:** `stiffness = 300f, dampingRatio = 0.8f` (subtle). Slider: Subtle (400/0.9) / Balanced (300/0.8) / Expressive (200/0.6).
- **Centralized spec:** `PulseMotion` object exposes pre-tuned `spring()` specs by semantic name (`enter`, `exit`, `morph`, `flip`).

### Persistence
- **Room:** structured data (tiles, icons, AI settings, focus).
- **DataStore Preferences:** simple flags (`themeMode`, `hapticsLevel`).
- **EncryptedDataStore:** secrets (API keys).
- **No SharedPreferences.** All legacy usage migrated.

### Observability
- **Logging:** `Timber` (already in use) with `PulseTree` tagging subsystem name.
- **Perf tracing:** `Choreographer` frame drops logged in dev builds.
- **Crash:** unhandled exceptions in `PulseWorkspaceHost` captured but never crash the host launcher.

### Security
- **API keys:** KeyStore AES-GCM wrapping, never written in plain.
- **Private Space:** gated by `BiometricPrompt`; no bypass.
- **Permissions:** requested at the moment of feature use, never at install time.

### Concurrency
- **Hard rule:** every ViewModel exposes `StateFlow<State>`. No `LiveData`. No `runBlocking`.
- **Dispatchers:** `viewModelScope` default = `Main.immediate`. IO via `withContext(Dispatchers.IO)`.
- **Cancellation:** every long-running job stored in a `Job?` field and cancelled on re-entry.

---

## 5. Acceptance Gates (Per Milestone)

Every gate is **runnable** — a shell command that exits non-zero on failure.

| Milestone | Gate | Command | Pass Criteria |
|-----------|------|---------|---------------|
| **M1** | G-M1-01 Code compiles | `./gradlew assembleLawnWithQuickstepGithubDebug --console=plain` | BUILD SUCCESSFUL |
| **M1** | G-M1-02 Feed wired | unit test: `FeedWeatherCardTest` asserts repository called | test passes |
| **M1** | G-M1-03 Control Center modernized | grep finds no `wifiManager.isWifiEnabled = ` | 0 occurrences |
| **M1** | G-M1-04 Tile data binding | unit test: `TileCellRendererTest` for 4 tile types | all pass |
| **M2** | G-M2-01 Search parallel | unit test: `SearchDispatcherTest` verifies 6 searchers invoked concurrently | test passes |
| **M2** | G-M2-02 Math evaluator | unit test: `MathSearcherTest` covers +, −, ×, ÷, units | all pass |
| **M2** | G-M2-03 Web intent | unit test: `WebSearcherTest` checks Intent.action = ACTION_WEB_SEARCH | test passes |
| **M3** | G-M3-01 Multi-provider chain | unit test: `AssistantFailoverTest` simulates 429, verifies secondary called | test passes |
| **M3** | G-M3-02 Island state machine | unit test: `IslandStateMachineTest` covers COMPACT↔EXPANDED transitions | test passes |
| **M3** | G-M3-03 KeyStore encryption | unit test: `KeystoreHelperTest` round-trips a fake key | test passes |
| **M4** | G-M4-01 Shaders render | unit test: `IconRendererTest` produces non-blank Bitmap for each of 7 styles | all pass |
| **M4** | G-M4-02 Font catalog | unit test: `FontCatalogTest` lists at least 4 bundled families | test passes |
| **M5** | G-M5-01 Tile resize | unit test: `TileResizeLogicTest` covers all span combos | test passes |
| **M5** | G-M5-02 3D flip | snapshot test of `FlipCard` at rotationY = 0, 90, 180 | renders match |
| **M6** | G-M6-01 Gesture dispatcher | unit test: `GestureMapTest` verifies each registered gesture | test passes |
| **M6** | G-M6-02 Focus scheduler | unit test: `FocusModeSchedulerTest` triggers tile hiding at scheduled time | test passes |
| **M7** | G-M7-01 Backup round-trip | unit test: `PulseBackupCodecTest` exports → imports → diff = empty | test passes |
| **M7** | G-M7-02 Nova parser | unit test: `NovaBackupParserTest` on fixture `.novabackup` | test passes |
| **M7** | G-M7-03 GPU tier switch | unit test: `AdaptiveGpuTierTest` with mocked battery levels | test passes |
| **ALL** | G-ALL-00 Lint clean | `./gradlew spotlessCheck` | 0 violations |
| **ALL** | G-ALL-01 APK build | `./gradlew assembleDebug` | BUILD SUCCESSFUL |

---

## 6. Risk Register

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| AGSL not supported on API 31 | Medium | Low (tier fallback) | Adaptive GPU tier (M7) handles silently |
| Compose recomposition cost in `HorizontalPager` | Medium | Medium | Stable keys on slide content; `derivedStateOf` for parallax |
| Lawnchair upstream breaking changes | High | High | Pulse lives in `app.lawnchair.pulse.*` only — zero Lawnchair core edits |
| Battery drain from Island service | Medium | High | `BatteryManager` poll; auto-disable below 20% |
| KeyStore inconsistency across devices | Low | Medium | Defensive fallback to DataStore-only if KeyStore unavailable |

---

## 7. Open Questions for Reviewer

1. **Theme inheritance:** Should Pulse Theme cascade into Lawnchair's native settings UI, or remain isolated to Pulse Compose surfaces? *(Recommendation: isolated — minimizes risk of Lawnchair breakage.)*
2. **AI defaults:** Which provider ships as default in M3? *(Recommendation: local-only mode is default; Anthropic is opt-in.)*
3. **Private Space:** Should M6 require Android 15 strictly, or feature-gate on API level? *(Recommendation: feature-gate; pre-15 users get a less-secure "hidden apps" fallback.)*
4. **GPU tier boundaries:** Are 50% / 20% the right thresholds for tier transitions, or should they be user-configurable? *(Recommendation: defaults with a dev-only override.)*

---

## 8. Next Steps (After Plan Approval)

1. Land this document + the milestone breakdown docs.
2. Begin M1 implementation following the per-milestone sections.
3. After M1 ships (gates G-M1-01..04 green), start M2 and M3 in parallel.
4. Re-evaluate dependencies after each milestone.
