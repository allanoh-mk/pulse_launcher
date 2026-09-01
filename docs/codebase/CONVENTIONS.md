# Coding Conventions

## Core Sections (Required)

### 1) Naming Rules

| Item | Rule | Example | Evidence |
|------|------|---------|----------|
| Files | PascalCase Kotlin/Java files matching the main type | `WorkspaceController.kt`, `LawnchairLauncher.kt` | [WorkspaceController.kt](lawnchair/src/app/lawnchair/pulse/workspace/WorkspaceController.kt), [LawnchairLauncher.kt](lawnchair/src/app/lawnchair/LawnchairLauncher.kt) |
| Functions | camelCase for standard methods; PascalCase for `@Composable` functions | `PulseWorkspace()`, `setWallpaperOffsets()` | [WorkspaceController.kt](lawnchair/src/app/lawnchair/pulse/workspace/WorkspaceController.kt) |
| Classes/Types | PascalCase for classes, objects, interfaces, and data classes | `PulseWorkspaceHost`, `LawnchairLauncher` | [PulseWorkspaceHost.kt](lawnchair/src/app/lawnchair/pulse/workspace/PulseWorkspaceHost.kt) |
| Constants | UPPER_SNAKE_CASE for compile-time constants | `PAGE_COUNT`, `WALLPAPER_PARALLAX` | [WorkspaceController.kt](lawnchair/src/app/lawnchair/pulse/workspace/WorkspaceController.kt) |

### 2) Formatting and Linting

- **Formatter & Linter:** The project uses the **Spotless** Gradle plugin for code formatting.
  - **Kotlin:** Enforced using **KtLint** (with customized Compose rules, e.g. `io.nlopez.compose.rules:ktlint:0.4.10`).
  - **Java:** Enforced using **Google Java Format** in AOSP style (specifically for the compatibility libraries).
- **EditorConfig:** A `.editorconfig` file is located at the root to enforce indent size (4 spaces for Kotlin/Java, 2 spaces for YAML/XML), trim trailing whitespaces, and insert a final newline.
- **Run Commands:**
  ```bash
  # Check formatting compliance
  ./gradlew spotlessCheck

  # Automatically format code according to conventions
  ./gradlew spotlessApply
  ```

### 3) Import and Module Conventions

- **Import sorting:** Standard Kotlin and Java packages are organized alphabetically, with JVM and Android framework packages usually separated from local packages.
- **Path aliases:** No TypeScript-like path aliases exist. Relative packages translate directly to the folder nested under the `src/` hierarchy (e.g. package `app.lawnchair.pulse.workspace` is in `lawnchair/src/app/lawnchair/pulse/workspace/`).

### 4) Error and Logging Conventions

- **Error Strategy:** Checked exceptions and try-catch blocks are used extensively for Android system interaction (such as querying services or resolving packages).
- **Logging:** Upstream Launcher3/Lawnchair uses custom logging wrappers like `FileLog` or system `android.util.Log`. For Compose/Pulse components, standard reactive flows (or standard logging) will be preferred.

### 5) Testing Conventions

- **Test Placement:** Unit and integration tests reside under the `tests/src` folder, matching the package structures of the tested files.
- **Naming Pattern:** Test class files are named with a suffix of `Test` (e.g. `LauncherPrefsTest.kt`, `GridSizeMigrationUtilTest.kt`).
- **Mocking Tooling:** Standard Mockito (`KotlinMockitoHelpers.kt`) and Android Instrumentation (TAPL) libraries are used.

### 6) Evidence

- [build.gradle](build.gradle) (Spotless task definitions and rulesets)
- [.editorconfig](.editorconfig)
- [lawnchair/src/app/lawnchair/pulse/workspace/WorkspaceController.kt](lawnchair/src/app/lawnchair/pulse/workspace/WorkspaceController.kt)
- [tests/src/com/android/launcher3/LauncherPrefsTest.kt](tests/src/com/android/launcher3/LauncherPrefsTest.kt)
