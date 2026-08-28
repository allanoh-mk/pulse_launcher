# Pulse Launcher — Project Document

## 1. Mission
Pulse Launcher is a personal, sideloaded Android home app built from a Lawnchair fork. It replaces the traditional grid workspace with a 3-slide system (Feed / Tile Grid / Vertical List), adds an iOS-level Dynamic Island with a built-in digital assistant, and introduces a unified style pipeline (Icon Studio, Font Studio, theming) that controls every visual element from one settings screen. It is for one user on one phone. It is not a public app.

## 2. Keep / Remove

### KEEP (do not touch):
- `com.android.launcher3.Launcher` — core activity lifecycle
- `com.android.launcher3.workspace.Workspace` — page infrastructure (reconfigured to 3 slides)
- `com.android.launcher3.allapps.AllAppsContainerView` — app drawer (Slide 3 base)
- `com.android.launcher3.hotseat.Hotseat` — dock (kept minimal, 4–5 icons)
- `com.android.quickstep.*` — Recents view (unchanged)
- `com.android.launcher3.LauncherModel` — data loading, app info cache
- `com.android.launcher3.model.*` — `LoaderTask`, `AppInfo`, `WorkspaceItem`
- `com.android.launcher3.graphics.*` — icon cache, adaptive icon handling
- `com.android.launcher3.anim.*` — animation utilities
- `com.android.launcher3.dragndrop.*` — drag infrastructure (icons only)
- `com.android.launcher3.search.*` — QSB / search bar base
- `com.android.launcher3.preferences.WallpaperPicker` — wallpaper selection
- `com.android.launcher3.uioverrides.QuickstepLauncher` — main launcher activity
- `app.lawnchair.preferences.PreferenceActivity` — settings shell (extended)
- `app.lawnchair.data.AppDatabase` — Room DB (extended with new tables)
- Boot receiver (`com.android.launcher3.LauncherReceiver`)
- Home intent handling (`android.intent.category.HOME`)
- `com.android.launcher3.statemanager.*` — state machine (extended for island states)

### REMOVE / DISABLE:
- `com.android.launcher3.widget.*` — entire widget system (replaced by custom tile grid)
- `modules/widgetpicker` — widget picker module (delete)
- `com.android.launcher3.folder.*` — folder system (replaced by tile dashboards)
- `com.android.launcher3.popout.*` — popup folders
- `com.android.launcher3.shortcuts.*` — deep shortcuts / long-press menu (replaced by quick actions)
- `com.android.launcher3.views.*` — SmartSpace / At a Glance (replaced by Feed)
- `com.android.launcher3.search.QuickSearchBar` — default QSB (replaced by unified search)
- `com.android.launcher3.uioverrides.TiltEffect` — icon tilt on drag
- `com.android.launcher3.anim.SpringAnim` — [VERIFY: may be reused for island springs]
- Multi-display / split-screen launcher support (`com.android.launcher3.multidisplay.*`)
- `com.android.launcher3.folder.FolderView` — [VERIFY: confirm exact path in current Lawnchair 15]
- Lawnchair "At a Glance" / SmartSpace integration (`app.lawnchair.smartspace.*`)
- Lawnchair "Rich grid" mode (`app.lawnchair.richgrid.*`)
- Lawnchair "App categorization" in drawer (`app.lawnchair.allapps.*` categorization logic)
- `com.android.launcher3.widget.InsettableAppWidgetHostView` — widget rendering
- `com.android.launcher3.widget.LauncherAppWidgetHost` — widget host
- Backup/restore UI (`com.android.launcher3.preferences.BackupRestore`) — [VERIFY: path may differ]
- Lawnchair "Desktop lock" feature
- Lawnchair "Icon from gallery" feature
- Translation / Crowdin integration (`crowdin.yml`, `res/values-*`)
- Play Store metadata (`fastlane/`)
- CI / GitHub Actions workflows (`.github/`)

## 3. Target Environment
- **minSdk:** 31 (Android 12)
- **targetSdk:** 35 (Android 15)
- **compileSdk:** 35
- **Device:** phone (single display, 1080×2400 baseline)
- **Build:** `./gradlew assembleDebug`
- **Output:** `build/outputs/apk/debug/app-debug.apk`

## 4. Feature List

### MUST HAVE:
- 3-slide workspace (Feed / Tile Grid / Vertical List) with horizontal swipe
- Icon Studio (shape, style, size, label, icon pack, per-app override, live preview)
- Font Studio (type scale, font family, weight, pairing presets)
- Unified search (apps, contacts, calendar, files, settings, web, micro results, assistant)
- Dynamic Island overlay (compact / minimal / expanded; music, timer, call, charging, nav)
- Custom Control Center (swipe down top-right; connectivity, brightness, volume, media, toggles)
- Material You theming (wallpaper-derived colors, dark/light, accent color)
- Per-slide wallpapers (static, gradient, shader)
- Haptic feedback on all interactions
- Spring-based animation system with global personality slider
- Digital assistant (Gemini API + rule-based commands, context-aware)
- Gesture system (customizable, default map as specified)
- Focus modes (Work / Sleep / DND / Custom)
- Local backup/restore (JSON export of all configs)

### NICE TO HAVE:
- Shader wallpapers (aurora, fluid, mesh gradient)
- Stacked icon styles (e.g., Holographic + Film Grain)
- AI-curated feed (time-aware content selection)
- Voice wake word (Porcupine)
- Island Activity Contract (personal apps register live activities)
- Theme packs (exportable/importable `.pulse-theme` files)
- Music-reactive wallpaper
- Per-app quick actions (long-press radial menu)
- Widget gallery (10+ custom themed widgets)
- Macro system (multi-step gesture sequences)

### OUT OF SCOPE:
- Multi-user / work profile
- Cloud sync
- Tablet / foldable layouts
- Accessibility (TalkBack, screen reader)
- Public release / Play Store
- Lockscreen customization
- AOD customization
- Root / system app installation
- Third-party plugin API
- i18n / translations
- Split-screen / multi-window launcher support
- Standard Android widget system (`AppWidgetProvider`)

## 5. UI/UX
- **Layout:** 3 fixed slides, no grid workspace. Slide 1 = scrollable card feed. Slide 2 = resizable tile grid (Mur-style). Slide 3 = vertical app list (Niagara-style).
- **Color:** Material You dynamic palette derived from wallpaper. Single accent color tints all interactive elements. Dark/light auto or manual.
- **Icon size:** 48dp default (configurable 36–64dp via Icon Studio).
- **Spacing:** 8dp grid system. 16dp card padding. 24dp section margins.
- **Animation:** Spring-based (`stiffness: 300, damping: 28` default). Global "personality" slider (Subtle / Balanced / Expressive). Slide transition = horizontal slide + parallax wallpaper at 0.7×.
- **Island:** top-anchored, full-width, spring-animated height. Compact = 36dp pill. Expanded = ~280dp card.
- **Typography:** Inter Variable (default). Configurable via Font Studio.
- **TBD:** exact corner radius values, exact feed card aspect ratios, exact tile grid column count.

## 6. Architecture & Conventions
- **Language:** Kotlin (100%). No Java in new code.
- **UI:** Jetpack Compose for all new screens (Icon Studio, Font Studio, Island, Control Center, Search, Feed). Keep View-based Launcher3 internals untouched.
- **Package structure:** follow existing Lawnchair layout. New code goes in `app.lawnchair.pulse.*` subpackages.
- Do not add new Gradle dependencies without explicit approval.
- Do not reformat, rename, or move files you were not asked to change.
- Do not modify `com.android.launcher3.*` files except where explicitly specified.
- All new Compose screens: single file per screen, `@Composable` functions, no ViewModels unless state exceeds one screen.
- **Database:** extend existing `AppDatabase` (Room). New entities in `app.lawnchair.pulse.data.*`.
- **State:** `StateFlow` / `MutableStateFlow`. No RxJava, no Coroutines beyond what Lawnchair already uses.
- **Shaders:** AGSL (`.age` files) in `app/src/main/assets/shaders/`.
- **Haptics:** `VibrationEffect.Composition` only. No third-party haptic libraries.

## 7. Build & Deploy
```bash
# Build
./gradlew assembleDebug

# Install
adb install -r build/outputs/apk/debug/app-debug.apk

# Set as default home (one-liner)
adb shell cmd package set-home-activity app.lawnchair/.Launcher
```

If `set-home-activity` fails (some OEMs block it):
```bash
adb shell am start -a android.intent.action.MAIN -c android.intent.category.HOME
```
Then select Pulse Launcher from the chooser and tap "Always".

## 8. License & Iteration Rules
Lawnchair is licensed under Apache 2.0. This fork is for personal use only. No redistribution, no publishing, no sharing of the built APK.

**Rules for every change:**
- After each modification, summarize: what file(s) changed, what was added/removed, and why.
- If a design decision is ambiguous (e.g., "should this be a Compose screen or a View?"), ask before implementing.
- Only touch files explicitly requested. Everything else is read-only.
- Do not "improve" or "refactor" code that wasn't asked to change.
- If a Lawnchair upstream update breaks something, report it — do not silently patch.
- **Commit messages:** `pulse: <short description>` (e.g., `pulse: add glass icon style to IconRenderer`).
