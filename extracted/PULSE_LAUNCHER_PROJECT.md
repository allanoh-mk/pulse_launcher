# Pulse Launcher — Project Document

## 1. Mission
Pulse Launcher is a personal, sideloaded Android launcher forked from Lawnchair/Launcher3, built for one user only — me. It strips stock Lawnchair down to a fast, minimal core and layers on a custom three-slide home (feed, tiles, vertical app list), a Dynamic Island, and a composable Icon Studio. It is not a public app, not published, and not designed for other users' devices or preferences.

## 2. Keep / Remove

**KEEP (untouched Lawnchair/Launcher3 systems):**
- `com.android.launcher3.allapps.*` — app drawer, search-in-drawer base
- `com.android.launcher3.recents` / overview/recents integration [VERIFY: Lawnchair may proxy this to Quickstep]
- `com.android.launcher3.LauncherAppState`, `LauncherModel`, `BgDataModel` — core data model
- `com.android.launcher3.icons.*` icon cache/loading pipeline (extended, not replaced)
- Home intent handling / default-launcher role request flow
- `com.android.launcher3.LauncherProvider` (workspace DB) — schema extended, not replaced
- Boot receiver / `LauncherAppsCompat` app install-uninstall listeners
- Drag-and-drop core (`DragController`, `DragLayer`) for reorder-within-slide use
- Notification dots / badge system (feeds the Feed slide and tile badges)
- Accessibility services already present in Launcher3 base

**REMOVE:**
- `com.android.launcher3.widget.*` — full AppWidget host/picker system (Slide 2 tiles replace this; no third-party widget hosting)
- Split-screen / multi-window drag targets (`com.android.launcher3.util.SplitConfigurationOptions` and related drop targets)
- Multi-display / secondary-display launcher support
- `com.android.launcher3.shortcuts.DeepShortcutManager` and deep-shortcut popup UI (replaced by custom radial quick actions, later phase)
- Work profile / multi-user support (`UserCache`, work-tab in all-apps) — single personal profile only
- Lawnchair's built-in icon-pack picker UI (replaced entirely by Icon Studio)
- Lawnchair's default gesture-settings UI (replaced by custom Gestures settings screen)
- Lawnchair's own theming/accent picker UI (replaced by Pulse accent engine)
- Any Play Store update-check / feedback / analytics hooks bundled in Lawnchair fork [VERIFY: which telemetry, if any, ships in base]
- Quick-space / at-a-glance stock weather bar (replaced by Feed slide hero card)

## 3. Target Environment
- minSdk: 26 [VERIFY against chosen Lawnchair branch — Launcher3 upstream commonly targets 26+]
- targetSdk: 34
- Device type: single personal phone, portrait only, no tablet/foldable layouts
- Build command: `./gradlew assembleLawnWithQuickstepGithubDebug` [VERIFY exact variant name against forked build.gradle flavors]

## 4. Feature List

**MUST HAVE:**
- Three-slide home: Feed, Tiles, Vertical App List
- Icon Studio: shape + style pipeline (Material, Glass, Duotone, Gradient, Embossed minimum) with per-app override
- Dynamic Island: compact + expanded states, music/timer/call live activities
- Accent engine: manual + wallpaper-derived (Material You) sources
- Unified search overlay (apps, contacts, basic inline answers)
- App drawer (inherited, restyled)

**NICE TO HAVE:**
- Time-based accent hue rotation
- Control Center overlay (toggles, brightness/volume, media)
- Font Studio (family/weight pairing picker)
- Focus modes (Work/Sleep) with per-mode theme
- Additional icon styles (Neon, Holographic, Liquid Glass, Film Grain)

**OUT OF SCOPE:**
- Third-party widget hosting
- Multi-user/work-profile support
- Split-screen/multi-window
- Theme Pack sharing/export marketplace
- Any cloud sync or account system
- Play Store distribution

## 5. UI/UX
Layout: three horizontally-swiped home slides, bento-card feed, resizable tile grid, alphabetical vertical list. Theme: dark-first, single dynamic accent color propagated through all UI states. Icon size: TBD. Spacing: 8dp base grid, 16dp card padding, 20dp screen margins. Corner radii: 12dp controls / 20dp cards / capsule for Island. Animation: spring-based motion throughout, no linear/ease-only transitions; one dominant motion at a time with staggered secondary elements.

## 6. Architecture & Conventions
Language: Kotlin only, no new Java files. UI framework: match Lawnchair's existing framework (View-based + Lawnchair's Compose usage where already present) — do not introduce a parallel UI framework. Follow existing package structure under `app/src/main/java/...`; new Pulse-specific code lives in its own `com.pulse.*` subpackage rather than scattered into `com.android.launcher3.*`. Do not add new Gradle dependencies without asking first. Do not reformat or touch files outside the current task's scope, even if style looks inconsistent.

## 7. Build & Deploy
Build: `./gradlew assembleLawnWithQuickstepGithubDebug` [VERIFY variant name]
Install: `adb install -r app/build/outputs/apk/lawnWithQuickstepGithub/debug/app-lawnWithQuickstepGithub-debug.apk` [VERIFY output path against actual build variant]
Set as default home: `adb shell cmd package set-home-activity com.pulse.launcher/com.android.launcher3.Launcher` [VERIFY actual launcher activity class/package name post-fork]

## 8. License & Iteration Rules
Lawnchair/Launcher3 base is Apache 2.0 licensed. Pulse Launcher is personal use only — no redistribution, no publishing, no public repo.
- After each change, summarize what was modified and why.
- If unsure about a design decision, ask before implementing.
- Only touch files explicitly requested for change; everything else is read-only.

---

## Appendix — Reference UI/UX Images

Pages below are visual references only, generated from the Pulse Stitch design pass. Two pages (Font Studio, and the iOS/One UI 9-hybrid Notification & Quick Panel) are not yet generated — see note at the end.

1. `images/01_home_feed.png` — Home, Slide 1 (Feed)
2. `images/02_home_tiles.png` — Home, Slide 2 (Tiles & Widgets)
3. `images/03_home_applist.png` — Home, Slide 3 (Vertical App List)
4. `images/04_island_compact.png` — Dynamic Island, compact state
5. `images/05_island_expanded.png` — Dynamic Island, expanded state
6. `images/06_search_overlay.png` — Unified Search overlay
7. `images/07_control_center.png` — Control Center overlay
8. `images/08_icon_studio.png` — Icon Studio settings
9. `images/09_settings.png` — Pulse main settings

**Not yet generated:** Font Studio page, and the Notification + Quick Panel screen styled as an iOS-notch-panel/One UI 9 hybrid (blur depth, spring-open motion, One UI's pill-toggle sizing). These need a dedicated Stitch pass — recommend running prompts for "Font Studio: family list + live pairing preview + weight slider" and "Notification & Quick Panel: iOS-style grouped notification cards at top, One UI 9-style large rounded toggle grid below, spring-open panel motion" separately, since they weren't part of the original 9-screen batch.
