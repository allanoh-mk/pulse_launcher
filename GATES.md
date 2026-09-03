# Gates: Pulse Launcher — Master Verification Ledger

OWNS: lawnchair/src/app/lawnchair/pulse/**

Scope: Execute and implement all Pulse Launcher architectural milestones 1 by 1 by 1 with unlazy verification discipline.

## Milestone 1: Core Stubs & Foundation
- [x] G-M1-01 Code compiles
  CHECK: ./gradlew compileDebugKotlin
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

- [x] G-M1-02 Feed wired
  CHECK: ./gradlew testDebugUnitTest --tests "FeedWeatherCardTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

- [x] G-M1-03 Control Center modernized
  CHECK: grep -r "wifiManager.isWifiEnabled" lawnchair/src/app/lawnchair/pulse/controlcenter/ || echo "0 occurrences"
  EXPECT: 0 occurrences
  EVIDENCE: pass

- [x] G-M1-04 Tile data binding
  CHECK: ./gradlew testDebugUnitTest --tests "TileCellRendererTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

- [x] G-M1-05 3-slide workspace structure
  CHECK: ./gradlew testDebugUnitTest --tests "PulseWorkspaceTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

- [x] G-M1-06 Weather repository caching
  CHECK: ./gradlew testDebugUnitTest --tests "WeatherRepositoryTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

- [x] G-M1-07 Calendar repository integration
  CHECK: ./gradlew testDebugUnitTest --tests "CalendarRepositoryTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

- [x] G-M1-08 System bridge implementation
  CHECK: ./gradlew testDebugUnitTest --tests "SystemBridgeTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

## Milestone 2: Universal Search & Offline Math Evaluator
- [x] G-M2-01 Search query dispatcher & math evaluator
  CHECK: ./gradlew testDebugUnitTest --tests "app.lawnchair.pulse.search.UniversalSearchTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

- [x] G-M2-02 Math calculation inline results
  CHECK: ./gradlew testDebugUnitTest --tests "app.lawnchair.pulse.search.MathEvaluatorTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

- [x] G-M2-03 Multi-domain result aggregation (Apps, Contacts, Files, Web, Math)
  CHECK: ./gradlew testDebugUnitTest --tests "app.lawnchair.pulse.search.SearchResultAggregatorTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

## Milestone 3: Dynamic Island & Multi-Provider AI Engine
- [x] G-M3-01 9router AI provider with endpoint failover
  CHECK: ./gradlew testDebugUnitTest --tests "app.lawnchair.pulse.assistant.NineRouterProviderTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

- [x] G-M3-02 Embedded Pulse Music audio engine
  CHECK: ./gradlew testDebugUnitTest --tests "app.lawnchair.pulse.music.PulseMusicTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

- [x] G-M3-03 Multi-provider AI client chain (Claude, OpenAI, Gemini, Groq, Ollama)
  CHECK: ./gradlew testDebugUnitTest --tests "app.lawnchair.pulse.assistant.MultiProviderAiTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

- [x] G-M3-04 Dynamic Island state machine & Live Activity
  CHECK: ./gradlew testDebugUnitTest --tests "app.lawnchair.pulse.island.IslandStateMachineTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

## Milestone 4: Icon Studio & Shaders Pipeline
- [x] G-M4-01 AGSL Shader styles (Duotone, Holographic, Film Grain, Material You)
  CHECK: ./gradlew testDebugUnitTest --tests "app.lawnchair.pulse.iconstudio.IconShaderTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

- [x] G-M4-02 Room database per-app icon styling table
  CHECK: ./gradlew testDebugUnitTest --tests "app.lawnchair.pulse.data.db.IconStyleConfigTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

## Milestone 5: Bento Tiles Ecosystem & Dashboards
- [x] G-M5-01 3D Flip Card state and complication swapping
  CHECK: ./gradlew testDebugUnitTest --tests "app.lawnchair.pulse.workspace.BentoFlipCardTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

- [x] G-M5-02 Tile grid layout packing & collision resolution
  CHECK: ./gradlew testDebugUnitTest --tests "app.lawnchair.pulse.data.db.TileGridLayoutTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

## Milestone 6: Power Gestures & Focus Modes
- [x] G-M6-01 Focus mode scheduler & tile filtering
  CHECK: ./gradlew testDebugUnitTest --tests "app.lawnchair.pulse.workspace.FocusModeSchedulerTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

- [x] G-M6-02 Gesture action dispatcher
  CHECK: ./gradlew testDebugUnitTest --tests "app.lawnchair.pulse.core.GestureDispatcherTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

## Milestone 7: Backups, Migration & Lint Integrity
- [x] G-M7-01 JSON Settings & Layout Export/Import
  CHECK: ./gradlew testDebugUnitTest --tests "app.lawnchair.pulse.data.backup.BackupManagerTest"
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pass

- [x] G-M7-02 Codebase Integrity & Unit Test Suite
  CHECK: ./gradlew testLawnWithQuickstepGithubDebugUnitTest
  EXPECT: BUILD SUCCESSFUL (66+ tests passing)
  EVIDENCE: pass

- [x] G-M7-03 Spotless / KtLint Cleanliness
  CHECK: ./gradlew spotlessCheck --no-configuration-cache
  EXPECT: BUILD SUCCESSFUL with 0 violations
  EVIDENCE: pass

- [x] G-M7-04 Assemble Debug APK
  CHECK: ./gradlew assembleLawnWithQuickstepGithubDebug --console=plain
  EXPECT: BUILD SUCCESSFUL and APK generated
  EVIDENCE: pass (Lawnchair.14.Dev.(b9d1b45).github.debug.apk, 91MB)
