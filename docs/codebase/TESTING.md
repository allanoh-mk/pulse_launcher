# Testing Patterns

## Core Sections (Required)

### 1) Test Stack and Commands

- **Primary test frameworks:** JUnit 4, Android Testing Support Library, Mockito.
- **UI Interaction testing:** **TAPL (Test Accessibility Protocol Library)** under `tests/tapl/` and Android UI Automator are used to simulate user actions (dragging, swiping, launching apps).
- **Test Runner:** `androidx.test.runner.AndroidJUnitRunner`
- **Commands:**
  ```bash
  # Run unit tests on local JVM
  ./gradlew test

  # Run instrumented UI / TAPL tests on connected emulator or physical device
  ./gradlew connectedAndroidTest
  ```

### 2) Test Layout

- **Test file placement:** All test code resides in the root `tests/src/com/android/launcher3/` directory.
- **File statistics:** Contains **119 test-related files** (90 Java files and 29 Kotlin files).
- **Naming convention:** Standard test files end with the `Test` suffix (e.g., `LauncherPrefsTest.kt`, `DisplayControllerTest.kt`, `IconCacheTest.java`).
- **Base classes:** Base test setups are orchestrated by classes like `AbstractLauncherUiTest.java` (for TAPL interactions) and `AbstractWorkspaceModelTest.kt` (for loading mock databases).

### 3) Test Scope Matrix

| Scope | Covered? | Typical target | Notes |
|-------|----------|----------------|-------|
| Unit | Yes | Specifications, preferences, helper math, string matchers | Tested on JVM using standard Mockito/JUnit (e.g. `LauncherPrefsTest.kt`, `StringMatcherUtilityTest.java`). |
| Integration | Yes | Model tasks, DB queries, grid-size migrations | Tests like `GridSizeMigrationUtilTest.kt` inspect offline SQLite migration scripts. |
| UI/E2E | Yes | Home app launcher swipes, icon placement, drag-and-drop, App drawer UI | Driven using TAPL (`tests/tapl/`) and UI Automator on physical/virtual test devices. |

### 4) Mocking and Isolation Strategy

- **Context Mocking:** Mock classes and custom providers (like `TestCommandProvider.java` / `TestCommandReceiver.java`) intercept launcher intents and isolate performance metrics.
- **Mockito helpers:** Standard Kotlin-Mockito wrappers (`KotlinMockitoHelpers.kt`) simplify mocking functions and suspending coroutines inside Kotlin unit tests.

### 5) Quality and Lint Signals

- **Lint Baseline Configurations:** The repository maintains extensive Android Lint baseline rules to manage and track warnings over time:
  - [lint-baseline.xml](lint-baseline.xml)
  - [lint-baseline-launcher3.xml](lint-baseline-launcher3.xml)
  - [lint-baseline-res-lib.xml](lint-baseline-res-lib.xml)
- **Pulse Coverage Gaps:** Currently, there are no custom test suites written specifically for the added `pulse/` package (such as verifying `PulseWorkspace` settled pages or the haptic integration).

### 6) Evidence

- [build.gradle](build.gradle)
- [tests/src/com/android/launcher3/util/KotlinMockitoHelpers.kt](tests/src/com/android/launcher3/util/KotlinMockitoHelpers.kt)
- [tests/src/com/android/launcher3/LauncherPrefsTest.kt](tests/src/com/android/launcher3/LauncherPrefsTest.kt)
- [tests/src/com/android/launcher3/model/GridSizeMigrationUtilTest.kt](tests/src/com/android/launcher3/model/GridSizeMigrationUtilTest.kt)
- [tests/tapl/](tests/tapl/)
