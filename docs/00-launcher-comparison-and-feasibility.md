# 00 — Launcher Comparison & Feasibility

## Overview

Pulse Launcher is conceived as a focused synthesis of the strongest architectural paradigms from modern Android launchers:
- **Mur Launcher** (Resizable live tile grid, press-and-hold dashboards, universal search with inline calculations/actions, category drawers)
- **Niagara Launcher** (Ergonomic vertical scrolling list, one-handed alphabet wave, radical visual minimalism, pop-up shortcuts)
- **Nova Launcher** (Context-aware feed page, flexible theming engine, micro-results parser, subgrid granularity, color accent dynamics)
- **iOS / Samsung One UI 9** (Dynamic Island live activity mechanics, blurred glass Control Center overlay, tactile spring haptics)

---

## Launcher Comparison Matrix

| Feature / Aspect | Mur Launcher | Niagara Launcher | Nova Launcher | **Pulse Launcher (Our Synthesis)** |
| :--- | :--- | :--- | :--- | :--- |
| **Primary Layout** | Tile-based (Metro & Bento hybrid) | Single vertical scrolling list | Traditional desktop icon grid | **3-Slide System (Feed → Tiles → Vertical List)** |
| **Search Architecture** | Universal + Inline answers (Sesame-like) | Clean search (Apps, contacts, web, math) | Advanced Search + Micro Results | **Unified Search (Micro-math + Units + Apps + Contacts + Files + Gemini/Ollama LLM)** |
| **Widgets & Dashboards** | Live tiles + Embedded dashboard widgets | Stacked widgets (up to 4) | Standard AOSP widget grid | **Live Tiles (1×1 to 2×2) + Long-press expandable tile hubs** |
| **App Organization** | Auto-categorized drawers | Alphabetical list + Wave Alphabet | App drawer groups + folders | **Slide 3 Niagara-style list + Drawer filters** |
| **Dynamic Island** | None | None | None | **iOS-Level Foreground Service (`TYPE_APPLICATION_OVERLAY`) with Live Activities & AI** |
| **Control Center** | Standard system notification shade | Standard system shade | Standard system shade | **Custom iOS/One UI 9 hybrid overlay (Haze blur, spring slide-down)** |
| **Theming & Icons** | Material, Metro, Liquid presets | Minimalist Material You + Anycons | Deep color customization + packs | **Icon Studio (13 post-processing styles: Liquid Glass, Holographic, Neon, Embossed, etc.)** |
| **Business Model** | Subscription / Premium | Freemium / Pro license | Freemium / Prime license | **100% Personal, Sideloaded, Zero ads, Zero telemetry** |

---

## Technical Feasibility & Architecture Baseline

### Why Fork AOSP Launcher3 / Lawnchair?
1. **Battle-tested Core:** `Launcher3` provides robust lifecycle handling (`Launcher.java` / `QuickstepLauncher.java`), app state management (`LauncherAppState`, `LauncherModel`, `BgDataModel`), package broadcast listeners, icon caches, and system Recents integration.
2. **Lawnchair Enhancements:** Lawnchair provides clean Kotlin adaptations, modern preferences, and base Room DB schemas that save months of boilerplate.
3. **Clean Decoupling:** By stripping obsolete AOSP widget hosts, complex desktop folders, multi-display hooks, and work profile managers, we can cleanly insert Jetpack Compose overlays for the 3 slides, Icon Studio pipeline, and Dynamic Island.

---

## Detailed Feature Breakdown of Reference Launchers

### 1. Mur Launcher Features
- **Live Tiles:** Real-time information displays (music playback, unread communications, upcoming calendar, live battery metrics).
- **Press-and-Hold Dashboards:** Long-pressing a tile unfolds a dedicated hub containing unread notifications from that application, embedded widget views, and custom app shortcuts.
- **App Library:** Intelligent automatic categorization (Social, Tools, Media, Games).
- **Universal Search:** Instant indexing across applications, system settings, contacts, files, and inline calculation tools.
- **Theming Tiers:** Material, Metro, and Liquid design system treatments.

### 2. Niagara Launcher Features
- **Ergonomic Vertical List:** One-handed scrolling with the signature "Wave Alphabet" quick jump.
- **Pop-up Actions:** Swiping right on any list row expands secondary actions without navigating away.
- **Integrated Notifications:** Inline notification previews and direct quick replies directly from the main list.
- **Glanceable Widgets:** Compact weather and calendar integrations designed to avoid visual clutter.

### 3. Nova Launcher Features
- **Nova Feed:** Left-page card feed aggregating contextual cards, news, and daily agenda.
- **Micro Results Search:** Real-time computation of math queries, metric conversions, package tracking numbers, and flight status.
- **Dynamic Accent Shifting:** Theming system that subtly shifts accent colors based on time of day and wallpaper color extraction.
- **Granular Customization:** Fine-tuned icon scaling, label typography control, and dock adjustments.
