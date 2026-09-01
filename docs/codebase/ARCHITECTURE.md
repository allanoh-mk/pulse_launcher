# Architectural Patterns

## Core Sections (Required)

### 1) Layers and System Boundaries

Pulse Launcher integrates custom Jetpack Compose-based launcher pages over the traditional Android View-based AOSP Launcher3 layout:

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose Hierarchy                       │
│                                                                        │
│   ┌───────────────┐        ┌──────────────────┐        ┌───────────┐   │
│   │   FeedPage    │        │   TileGridPage   │        │ ListPage  │   │
│   │   (Slide 1)   │        │    (Slide 2)     │        │ (Slide 3) │   │
│   └───────┬───────┘        └────────┬─────────┘        └─────┬─────┘   │
│           │                         │                        │         │
│           └─────────────────────────┼────────────────────────┘         │
│                                     ▼                                  │
│                    HorizontalPager (PulseWorkspace)                    │
└─────────────────────────────────────┬──────────────────────────────────┘
                                      │ (ComposeView.attach)
                                      ▼
┌────────────────────────────────────────────────────────────────────────┐
│                LawnchairLauncher (Android View / Activity)             │
│                                                                        │
│               dragLayer (AOSP DragLayer / ViewGroup container)        │
│                                                                        │
│   ┌────────────────────────────────────────────────────────────────┐   │
│   │  LauncherAppState / LauncherModel / LoaderTask                 │   │
│   │  (Loads apps, shortcut bindings, and SQLite databases)         │   │
│   └────────────────────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────────────────────┘
```

- **Compose Overlay Integration Layer:** `PulseWorkspaceHost` inserts a `ComposeView` into the `dragLayer` of the main `LawnchairLauncher` activity upon startup. This completely bypasses the stock Launcher3 grid rendering, layering our three-slide workspace directly on top.
- **Swipe Navigation Controller:** `WorkspaceController.kt` handles horizontal paging between the three slides, updates the system `WallpaperManager` offset to provide parallax scrolling, and emits light tactile tick haptics when page settlement occurs.
- **AOSP Core Data Layer:** Background loading is orchestrated by `LauncherModel` via `LoaderTask` which parses package installs, queries system user profiles, and prepares app info objects for presentation.

### 2) Core Data Flows

#### App Loading Data Flow
1. **Device Boot or Install Event:** AOSP listeners (`com.android.launcher3.LauncherAppState`) detect application package changes.
2. **Background Loading (`LoaderTask`):** Triggered by `LauncherModel`, runs on a dedicated loader thread, queries the system Package Manager, and populates `BgDataModel` containing `AppInfo` lists.
3. **Main Thread Callback:** `BgDataModel` update events are dispatched back to the UI thread, prompting our custom screens to refresh their lists.

#### Wallpaper Parallax Scrolling Flow
1. **Horizontal Scroll Event:** The user swipes horizontally in our Compose-based `PulseWorkspace` (defined in `WorkspaceController.kt`).
2. **Fractional Position Monitoring:** `snapshotFlow` continuously samples `pagerState.currentPage` and `pagerState.currentPageOffsetFraction`.
3. **System Dispatch:** The normalized fractional value is calculated and passed to `WallpaperManager.getInstance(context).setWallpaperOffsets(...)` using the window token of the current active view, creating smooth background wallpaper shifts.

### 3) Key Creational and Behavioral Patterns

- **Singleton Managers:** Core states like `LauncherAppState` are managed as singletons initialized lazily on the main thread via AOSP initialization providers.
- **Observer/Flow Pattern:** Compose-based states utilize Kotlin Coroutines `Flow` and Compose `snapshotFlow` to reactively stream settings modifications from Datastore/Room to the UI.
- **Composite Layout Pattern:** The `ComposeView` is composite-mounted into the traditional `DragLayer` frame layout, acting as a bridge between the new Jetpack Compose hierarchy and the legacy Android View hierarchy.

### 4) Intent vs. Reality Divergences

There is a substantial divergence between the **Intent** (described in the product documentation) and the **Reality** (what is actually implemented in the code so far):

| Feature | Stated Intent (Design Documents) | Current Reality (Codebase) |
|---------|──────────────────────────────────|───────────────────────────|
| **3-Slide Workspace** | Feed, Bento-Grid Tiles, Vertical list page | Implemented as a horizontal pager, but individual pages are currently static text placeholders (`PulseEmptyPage`). |
| **Dynamic Island Overlay** | System overlay (`TYPE_APPLICATION_OVERLAY`) with live activities, music controls, and assistant | Not yet implemented in the codebase. No service, overlay, or state machine exists. |
| **Digital Assistant** | Gemini/Ollama LLM integrated inside Dynamic Island and search results | Not yet implemented. No API connection or assistant logic is present. |
| **Icon Studio / Font Studio** | Global styling pipeline rendering dynamic drawables with custom post-processing styles | Stock Lawnchair icon pack picker and font loading exist, but the advanced unified Studio pipeline is not yet implemented. |
| **Control Center Overlay** | Custom system quick-setting panel slide-down | Not yet implemented. |
| **Unified Search** | Universal search box covering apps, contacts, calendar, files, and web results | Upstream Lawnchair / Launcher3 search-in-drawer is present, but the unified overlay is not yet implemented. |

### 5) Evidence

- [lawnchair/src/app/lawnchair/LawnchairLauncher.kt](lawnchair/src/app/lawnchair/LawnchairLauncher.kt) (calling `PulseWorkspaceHost.attach`)
- [lawnchair/src/app/lawnchair/pulse/workspace/PulseWorkspaceHost.kt](lawnchair/src/app/lawnchair/pulse/workspace/PulseWorkspaceHost.kt)
- [lawnchair/src/app/lawnchair/pulse/workspace/WorkspaceController.kt](lawnchair/src/app/lawnchair/pulse/workspace/WorkspaceController.kt)
- [lawnchair/src/app/lawnchair/pulse/workspace/FeedPage.kt](lawnchair/src/app/lawnchair/pulse/workspace/FeedPage.kt)
- [src/com/android/launcher3/LauncherModel.java](src/com/android/launcher3/LauncherModel.java)
- [extracted/PULSE_LAUNCHER_PROJECT.md](extracted/PULSE_LAUNCHER_PROJECT.md) (mission & keeper/remover guidelines)
