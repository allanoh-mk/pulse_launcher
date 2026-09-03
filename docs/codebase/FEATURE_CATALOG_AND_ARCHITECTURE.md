# Pulse Launcher — Master Feature Matrix & Architecture Specification

This document maps all features across the Pulse Launcher system, covering:
1. **Audited Current Code State** (Existing implementations & partial stubs).
2. **50+ Table-Stakes Launcher Features** (Home screen, grid, icons, drawer, gestures, folders, search, backup, accessibility, widgets).
3. **20 Differentiators / Personal Launcher Superpowers**.
4. **20 Architectural Upgrades to Planned Features**.

---

## 1. Deep Codebase Audit: Unfinished Logic & Stubs

| Module / Component | File Anchor | Current Implementation Status | Unfinished / Stub Logic Identified |
|---|---|---|---|
| **Search Engine** | [SearchViewModel.kt](lawnchair/src/app/lawnchair/pulse/search/SearchViewModel.kt:57) | App name substring filtering only | `// Contacts and Files would be added here`<br>- Missing `ContactsContract` resolver.<br>- Missing `MediaStore` / SAF file search.<br>- Missing Web search intent fallbacks.<br>- Missing calculator / unit conversion parser. |
| **Bento Tile Grid** | [TileGridPage.kt](lawnchair/src/app/lawnchair/pulse/workspace/TileGridPage.kt:101) | Grid cells render static text box labels | `TileCell` only draws `Text(tile.customLabel ?: tile.tileType.name)`.<br>- Missing active Clock / Weather / Media player / Calendar widget rendering inside cells.<br>- Missing tile configuration dialog / resizing handles. |
| **Feed Page** | [FeedPage.kt](lawnchair/src/app/lawnchair/pulse/workspace/FeedPage.kt:33) | Time hero + raw notification cards list | - Missing Weather summary card.<br>- Missing Calendar agenda card.<br>- Missing AI notification digest / grouped threads.<br>- Missing Smartspacer widget injection. |
| **AI Providers** | [AssistantViewModel.kt](lawnchair/src/app/lawnchair/pulse/assistant/AssistantViewModel.kt:29) | `LocalAiProvider` echo stub & partial OpenAI SSE | `LocalAiProvider` returns hardcoded dummy tokens.<br>- Missing Anthropic (Claude), Gemini, Ollama, Groq providers.<br>- Missing automatic provider fallback chain on 429/500 errors.<br>- Keystore encryption is not persisted into secure DataStore. |
| **Dynamic Island** | [IslandOverlay.kt](lawnchair/src/app/lawnchair/pulse/island/IslandOverlay.kt:103) | Morphing capsule with static placeholder boxes | Non-media activities draw empty colored boxes.<br>- Missing live Timer/Stopwatch ticking UI.<br>- Missing active Phone Call duration / hangup actions.<br>- Missing battery charging pulse animation.<br>- Missing camera/mic privacy indicator dot. |
| **Control Center** | [ControlCenterViewModel.kt](lawnchair/src/app/lawnchair/pulse/controlcenter/ControlCenterViewModel.kt:60) | Wi-Fi, BT, DND, volume, brightness | Wi-Fi toggle uses deprecated API (`wifiManager.isWifiEnabled = newState` throws or is no-op on API 29+ without panel intent).<br>- Missing Flashlight toggle (`CameraManager`).<br>- Missing Airplane mode / Hotspot / Auto-rotate toggles.<br>- Missing Media playback scrubber controls in panel. |
| **Icon Studio** | [IconRenderer.kt](lawnchair/src/app/lawnchair/pulse/iconstudio/IconRenderer.kt:58) | Liquid Glass, Neon, Embossed canvas shaders | Only 4 styles supported.<br>- Missing Duotone, Holographic, Film Grain, Material You dynamic tonal tinting.<br>- Missing per-app icon & shape override mapping in DB. |

---

## 2. 50+ Table-Stakes Launcher Features

### A. Home Screen & Grid (1–10)
1. **Adjustable Grid Density:** Independent X/Y grid density (from 3×3 up to 10×10) decoupled from icon scale.
2. **Per-Page Grid Override:** Separate density/layout modes for Feed (Slide 1), Bento (Slide 2), and App List (Slide 3).
3. **Infinite Scroll / Looping Pages Toggle:** Configurable wrap-around when swiping past the last slide.
4. **Vertical Scroll Home Screen Mode:** Optional vertical paginated workspace transition alongside standard horizontal paging.
5. **Page Indicator Styles:** Subtle pill, dot matrix, line bar, or completely hidden indicator.
6. **Desktop Grid Snapping with Visual Guides:** Real-time alignment rulers and magnet snap indicators while dragging.
7. **Multi-Select & Bulk Move/Delete:** Long-press multi-selection lasso for batch organizing icons and tiles.
8. **Orientation Lock / Per-Orientation Layouts:** Dedicated portrait vs landscape layout persistence.
9. **Dynamic Page Management:** Dragging an item to the screen boundary creates or removes workspace pages dynamically.
10. **Screen-Specific Wallpaper:** Separate static/gradient wallpapers per workspace slide with smooth cross-fading.

### B. Icons & Badges (11–20)
11. **Icon Pack Support:** Broad support for standard Nova/Apex/ADW icon packs with fallback shape masking.
12. **Per-App Icon Override:** Custom icon picker per individual application package.
13. **Icon Shape Picker:** Circle, Squircle, Teardrop, Rounded Hexagon, Pebble, and custom SVG vector masks.
14. **Adaptive Icon Shadow & Elevation:** Configurable drop shadow blur radius, offset, and ambient lighting intensity.
15. **Independent Icon Size Slider:** Continuous scaling from 32dp to 72dp without altering grid cell bounds.
16. **Notification Dots & Custom Color Badges:** Extract accent color from app icon or theme to tint unread badge dots.
17. **Numeric Unread Badges:** Count extraction from `NotificationListenerService` displayed over app icons.
18. **Themed / Monochrome Icons:** Full Android 13+ dynamic Material You monochrome icon generation for any app.
19. **Icon Label Font / Size / Color Customization:** Custom typography styling, multi-line wrapping, or color tint overrides.
20. **Hide / Disable App Labels:** Global toggle or per-icon setting to display clean icon-only surfaces.

### C. App Drawer & Navigation (21–30)
21. **Alphabetical Wave Fast-Scroll Index:** Tactile draggable A–Z vertical rail with magnification physics.
22. **Custom Drawer Background:** Real-time Gaussian blur (`RenderEffect`), solid tint, or wallpaper pass-through.
23. **Drawer Grid vs List Toggle:** Instant toggle between compact 4–5 column grid and Niagara-style vertical list.
24. **Auto-Categorization / App Tabs:** Automated grouping (Social, Media, Tools, Games, Productivity) using Package category metadata.
25. **Hidden Apps List:** Securely hide unused or bloatware apps without uninstalling.
26. **Protected / Private Space (Biometrics):** Android 15 `PrivateSpace` integration and biometric prompt gating (`BiometricPrompt`).
27. **Search-as-you-type with Fuzzy Matching:** Instant Levenshtein / trigram matching in the app drawer.
28. **Predictive App Row:** Usage-frequency and time-of-day contextual app launch suggestions.
29. **Work Profile Tab / Section:** Separate managed profile tab with quick work pause/resume toggle.
30. **Custom Sort Orders:** Alphabetical, install date, last updated, usage frequency, and manual custom drag order.

### D. Gestures & Interactions (31–36)
31. **Comprehensive Workspace Gestures:** Single/double finger swipe up, down, left, right mapped to custom launcher actions.
32. **Double-Tap to Sleep:** Lock screen via `AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN` or Device Admin.
33. **Pinch to Edit / Overview:** Two-finger pinch-in to launch tile dashboard reordering and widget manager.
34. **Two-Finger Swiping:** Quick app switcher trigger or focus mode toggle.
35. **Customizable Edge Swipes:** Configurable triggers on left/right screen borders (e.g. top-right = Control Center, left = Search).
36. **Long-Press Workspace Actions:** Customizable radial menu on home screen long-press.

### E. Folders & Dashboards (37–41)
37. **Custom Folder Shapes & Masks:** Match active Icon Studio shape (Squircle/Circle) with distinct corner radii.
38. **Folder Background Blur & Tint:** Frosted glass modal sheet with dynamic wallpaper background blur.
39. **Nested Sub-Folders:** Hierarchical categorization for power-user organization.
40. **Smart Auto-Folder Grouping:** Rule-based automatic categorization of freshly installed applications.
41. **Folder Grid vs List Previews:** 2×2 mini-grid preview, stack preview, or icon pile.

### F. Universal Search Engine (42–46)
42. **Universal Search Querying:** Concurrent async search across Apps, Contacts (`ContactsContract`), Media Files (`MediaStore`), Settings, and Web.
43. **Search Provider Switching:** Google, DuckDuckGo, Brave, Bing, Startpage, or custom HTTP search query endpoints.
44. **In-App Deep Search:** Direct shortcut querying via `LauncherApps.getShortcuts()`.
45. **Micro-Calculations & Conversions:** Offline math parsing (`eval`), currency, timezone, and unit conversion in the query bar.
46. **Search History & Recents:** Local query caching with privacy clear button.

### G. Backup, Restore & Migration (47–50)
47. **Full Settings Export / Import:** Comprehensive JSON schema export of database entities, tile configs, and icon styles.
48. **Secure Cloud / Drive Backup:** Android Backup Agent and optional self-hosted WebDAV / cloud export.
49. **Cross-Device Layout Synchronization:** Resolution-independent coordinate mapping for restoring layouts across devices.
50. **Nova Launcher Backup Compatibility:** Parser for importing `.novabackup` SQLite databases into Pulse tiles and drawers.

### H. Accessibility & System Integration (51–55)
51. **Independent Font Scaling:** Granular SP scaling independent of the Android system display size.
52. **High-Contrast & Colorblind Filters:** Protanopia, Deuteranopia, Tritanopia, and monochromatic high-legibility palettes.
53. **One-Handed Mode / Reachability:** Gesture to slide entire workspace down into the lower half of tall screens.
54. **Tactile Haptic Intensity Slider:** Amplitude control for tick, click, heavy, and transient vibration feedback.
55. **Bidirectional RTL Support:** Full mirrored layout support for Arabic, Hebrew, and Persian locales.

### I. Widgets & Custom Cards (56–60)
56. **Live Widget Resize Handles:** Real-time interactive border drag handles with immediate layout reflow.
57. **Stacked Widgets (iOS Style):** Vertical swipe pagination within a single widget container to flip between multiple widgets.
58. **Unified Themed Widget Frame:** System-wide glass/frosted background applied around standard AOSP app widgets.
59. **Context-Aware Smart Widgets:** Automatic switching between Calendar (morning), Commute (afternoon), and Media (evening).
60. **Glanceable Weather & Clock Complications:** High-res vector weather animations and customizable clock faces.

---

## 3. 20 Differentiators & Personal Launcher Superpowers

1. **Scheduled Focus Modes:** Automatic hiding of distracting apps, social tiles, and notifications based on time or location (Work / Deep Focus / Sleep).
2. **Gesture-Based App Launching:** Draw letter shapes (e.g. draw 'S' for Spotify, 'C' for Chrome) directly on the workspace.
3. **Live Activities Contract:** Internal IPC API allowing sideloaded tools to push live tracking cards (timers, food delivery, CI builds) to the Dynamic Island.
4. **Monet-Aware AI Wallpaper Generation:** Prompt-based on-device or remote AI wallpaper generation matching the active Material You palette.
5. **Contextual Workspace Pages:** Auto-switch active home screen based on Connected Wi-Fi SSID (Home vs Office vs Gym).
6. **AI Notification Digest Widget:** Local or LLM-generated bulleted summary of missed notifications grouped by topic.
7. **Battery-Aware Dynamic Throttling:** Multi-stage performance throttling (disables real-time blur below 20% battery, lowers framerate below 10%).
8. **Launcher-Level Digital Wellbeing Dashboard:** Screen time metrics, unlock counts, and app launch frequency chart directly in Slide 1 (Feed).
9. **Custom Status Bar Complications:** Injected live weather, battery ring, and network speed monitors on the home screen.
10. **Exportable Theme Packs (`.pulse-theme`):** Shareable bundles containing fonts, icon shaders, wallpaper, and tile layouts.
11. **Voice-Triggered Launcher Actions:** Offline voice hotword ("Hey Pulse") or double-tap trigger for rapid actions.
12. **Sensor-Based 3D Tilt Parallax:** Gyroscope and accelerometer-driven 3D depth effect on wallpaper layers and icon shadows.
13. **Radial Long-Press Quick Actions:** Smooth spring-animated radial context wheel for instant app uninstalls, info, and custom shortcuts.
14. **Persistent Clipboard History Card:** Secure local clipboard manager tile on the Bento grid with one-tap copy/paste.
15. **Custom Lock Screen Shortcuts Manager:** Integrated editor for lock screen left/right quick launch slots.
16. **Multi-User / Family Quick-Profile Switcher:** Instant user switching tile for shared Android tablets and family devices.
17. **App Pair Multi-Window Shortcuts:** Single icon to launch two apps simultaneously in split-screen mode.
18. **Circadian Dynamic Accent Palette:** Automatic morning-to-night temperature shift for colors and themes.
19. **Offline App Catalog Cache:** Fast local metadata querying without network latency.
20. **Extensible Plugin / Tile SDK:** Open Kotlin interface for personal mini-apps and custom Bento tiles.

---

## 4. 20 Architectural Upgrades to Planned Features

1. **Bento Tile 3D Flip Transitions:** Interactive card flip animations cycling multiple data sources (e.g., Calendar agenda flipping to To-Do items).
2. **Dynamic Island Privacy Complications:** Green/Orange camera and mic indicators embedded into the island pill, tinted with active theme accents.
3. **Resilient AI Provider Fallback Chain:** Automatic failover across Anthropic $\to$ OpenAI $\to$ Groq $\to$ Ollama on rate limits or connectivity loss.
4. **Smart Bento Auto-Layout Engine:** Dynamic algorithmic arrangement maximizing tile density based on usage metrics.
5. **Fine-Tuned HCT Color Sliders:** Direct Chroma, Tone, and Hue controls over Material You dynamic color generation.
6. **Per-Surface Liquid Glass Opacity:** Independent specular reflection, blur radius, and refraction indices for individual overlays.
7. **Physics-Driven Motion Profiles:** Configurable spring presets: *Snappy* (high stiffness), *Bouncy* (underdamped), *Smooth* (critically damped).
8. **3-Tier Adaptive GPU Render Quality:** Full (AGSL blur shaders) $\to$ Reduced (static render effects) $\to$ Minimal (flat alpha colors) adjusted by thermal state and RAM pressure.
9. **Configurable Wave Sensitivity & Haptic Curve:** Customizable scrub acceleration and vibration intensity on the alphabetical fast-scroll index.
10. **Sub-Pixel Fractional Grid Alignment:** Freeform icon and tile placement with optional micro-grid magnets.
11. **Themed Tile Dashboards:** Expanding a folder or tile opens an immersive blurred canvas with matching color accents.
12. **Configurable NLP Notification Summaries:** Verbosity sliders for notification cards (1-line digest vs structured bullet points).
13. **Per-App Custom Icon Shape Overrides:** Ability to assign a squircle to one app and a circle to another.
14. **Morph-to-Fullscreen Search Transitions:** Shared-element container transform from search bar to fullscreen results.
15. **Magnetic Snap-to-Grid Haptic Ticks:** Distinct tactile clicks when resizing tiles across 1×1, 2×1, 2×2, and 4×2 cell thresholds.
16. **Custom Overview / Recents Backdrop:** Independent transparency and blur controls for the Android Recents switcher.
17. **Per-App Contextual Gestures:** Custom swipe gestures customized according to which app was last running.
18. **Dual-Wallpaper Palette Extraction:** Merged color harmonization extracting palettes across lock screen and home wallpapers simultaneously.
19. **Multimodal Screen-Aware AI Assistant:** Screenshot context passing to LLMs for "Summarize this screen" queries.
20. **Incremental / Differential Backup Engine:** Lightweight delta change tracking for instant backups and rollback versioning.
