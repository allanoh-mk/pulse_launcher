# Codebase Concerns

## Core Sections (Required)

### 1) Top Risks (Prioritized)

| Severity | Concern | Evidence | Impact | Suggested action |
|----------|---------|----------|--------|------------------|
| high | **Massive Feature / Code Gap** | Source files [pulse/workspace/](lawnchair/src/app/lawnchair/pulse/workspace/) vs [PULSE_LAUNCHER_PROJECT.md](extracted/PULSE_LAUNCHER_PROJECT.md) | The code currently consists only of horizontal pager skeletons (`FeedPage.kt`, `ListPage.kt`, `TileGridPage.kt`). Major must-have features (Dynamic Island service, LLM Assistant integration, custom Control Center, Icon Studio pipeline) are entirely missing. | Formulate a staged implementation plan starting with the Dynamic Island overlay service and the Room-backed Icon Studio pipeline. |
| high | **Broad Permissions Footprint** | [AndroidManifest-common.xml](AndroidManifest-common.xml), [lawnchair/AndroidManifest.xml](lawnchair/AndroidManifest.xml) | The merged manifest requests extremely sensitive permissions (Overlay window, Notification access, Contacts, Calendar, Location). Broad surfaces increase security vulnerability vectors. | Maintain strict runtime permission check gating and prompt the user with clear explanations before requesting permission. |
| medium | **No Custom Test Suites for Pulse** | [tests/src/](tests/src/) folder has 119 tests, but none target `app.lawnchair.pulse` | Regressions in custom horizontal workspace scrolling, haptic page settling, or page offsets may slip through undetected. | Create a modular test package under `tests/src/com/android/launcher3/pulse` and implement basic Compose UI unit tests. |
| medium | **Extensive Legacy/Translation Churn** | [strings.xml files in over 30 languages](lawnchair/res/) | Lawnchair includes translation assets that are out of scope for a single-user personal app and increase codebase complexity. | Disable / strip translation pipelines and unused `values-*` localization resource folders as outlined in `PULSE_LAUNCHER_PROJECT.md` under REMOVE guidelines. |

### 2) Technical Debt

| Debt item | Why it exists | Where | Risk if ignored | Suggested fix |
|-----------|---------------|-------|-----------------|---------------|
| Static UI Stubs | Placed to sketch the horizontal pager layout | [FeedPage.kt](lawnchair/src/app/lawnchair/pulse/workspace/FeedPage.kt), [ListPage.kt](lawnchair/src/app/lawnchair/pulse/workspace/ListPage.kt), [TileGridPage.kt](lawnchair/src/app/lawnchair/pulse/workspace/TileGridPage.kt) | Core workspace slides do not render functional app lists, bento grids, or widgets | Implement custom layout bindings inside these stub pages. |
| `AndroidManifest-common.xml` TODO | Declares a placeholder security permission | [AndroidManifest-common.xml:140](AndroidManifest-common.xml) | Security parameters for providers might be loose or misconfigured | Review required signature permissions and lock down access. |
| Unimplemented Bug Uploader | Lawnchair's default crash/bug tool was not finalized | [UploaderService.kt:26](lawnchair/src/app/lawnchair/bugreport/UploaderService.kt) | Throws `TODO("not implemented")` when executed | Replace with lightweight local file logging or remove crash uploading entirely since this is a private offline app. |

### 3) Security Concerns

- **`SYSTEM_ALERT_WINDOW` (Overlay) Permission:** To render the planned iOS-level Dynamic Island and custom slide-down Control Center, the app must request draw-over-other-apps permission. Malicious or misconfigured overlay views can intercept screen touches (tapjacking). Secure layout attributes (`FLAG_NOT_TOUCH_MODAL`, `FLAG_NOT_FOCUSABLE`) must be verified once implemented.
- **Notification Access (`NotificationListenerService`):** Crucial for feed summary and dynamic island activity notifications, but grants full visibility into all device notification payloads (potentially exposing OTPs, messages, and codes). Gating/hashing of local buffers is recommended.

### 4) Performance and Scaling Concerns

- **Wallpaper Rendering Overhead:** `setWallpaperOffsets()` is called dynamically during horizontal swiping. If horizontal pager sampling is too frequent, rapid swipes could overload the system's window manager and cause frame drops (jank) on high-refresh-rate displays.
- **Complex Blur Effects:** The design documents request extensive blurred glass/frosty overlays (One UI 9 / iOS style). Composables that use deep real-time rendering blur layers (such as Haze or custom RenderEffect) consume substantial GPU resources on older devices. Baseline profiling is needed once the control center or island is added.

### 5) Fragile/High-Churn Areas

- **`LawnchairLauncher.kt`:** The central activity handling window layout, app states, and theme initialization. It has now been modified to host our Compose overlay. This file is highly complex and any edits inside its lifecycle methods carry high regression risks.
- **`build.gradle`:** Root build config contains compile SDKs, task overrides, and flavor dimensions. Changes to dependency versions can break the Gradle build cache or introduce annotation processor (KSP) collisions.

### 6) `[ASK USER]` Questions

1. **[ASK USER]** Since the custom Pulse Launcher features (Dynamic Island, Assistant, Control Center, Icon/Font Studio) are completely unimplemented, would you like me to start by developing the core overlay foreground service for the Dynamic Island, or focus on a different feature first?
2. **[ASK USER]** Do you want to strip out the unused system localization files (`res/values-*`) and translation hooks now to declutter and speed up compilations, or keep them for now?
3. **[ASK USER]** For the digital assistant integration, should we write code that integrates with a remote Gemini/Ollama API via Retrofit, or do you prefer a local offline-only command runner first?
