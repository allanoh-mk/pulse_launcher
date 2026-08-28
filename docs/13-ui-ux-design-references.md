# 13 — UI/UX Design System & Concept Image Analysis

## Overview

This document provides a comprehensive visual analysis of the design concepts and reference interfaces collected in `docs/concept_images/` and the Pulse Launcher design system. It details the structural layout, elevation tokens, surface materials, and component architectures for every primary UI surface.

---

## 1. Primary Screens & Surface Overview

```
┌────────────────────────┐  ┌────────────────────────┐  ┌────────────────────────┐
│      SLIDE 1: FEED     │  │     SLIDE 2: TILES     │  │    SLIDE 3: APP LIST   │
│ ┌────────────────────┐ │  │ ┌──────────┬─────────┐ │  │  Search Apps...       │
│ │ Hero Weather Card  │ │  │ │ Live     │ Media   │ │  │  ───────────────────   │
│ └────────────────────┘ │  │ │ Battery  │ 1x2     │ │  │  [A] Air Quality      │
│ [News] [Cal] [Music]   │  │ ├──────────┴─────────┤ │  │  [B] Browser           │
│ ┌──────────┬─────────┐ │  │ │ Calendar 2x1       │ │  │  [C] Calendar          │
│ │ Calendar │ Music   │ │  │ ├──────────┬─────────┤ │  │  [D] Drive             │
│ │ 2x1      │ 1x2     │ │  │ │ 1x1 Tile │ 1x1 Tile│ │  │  [E] Email             │
│ └──────────┴─────────┘ │  │ └──────────┴─────────┘ │  │      (Wave Alphabet)   │
└────────────────────────┘  └────────────────────────┘  └────────────────────────┘
```

---

## 2. Detailed Screen Breakdown & Image Analysis

### 2.1 Dynamic Island (Compact, Minimal & Expanded)
- **Visual References:** `concept_images/using-dynamic-islan-to-improve-user-experience.jpg`, `concept_images/unnamed (12).webp`
- **Surface Material:** Liquid Glass floating capsule (`#1C1C1E` with 78% alpha, 32px backdrop blur, 1px white specular top-edge highlight `rgba(255,255,255,0.28)`).
- **Behaviors:**
  - **Minimal (Dual Dots):** Multi-activity state showing app badge on left and active status ring on right.
  - **Compact (Status Pill):** 36dp pill anchored below the top bezel. Displays album thumbnail, scrolling marquee title, and dynamic 3-bar waveform animation.
  - **Expanded (Live Activity Card):** Springs down to a ~280dp card (`stiffness: 300, damping: 25`). Hosts full interactive scrubbers, volume controls, incoming caller actions, or AI assistant conversation streams.

### 2.2 Slide 1: Contextual Bento Feed
- **Visual References:** `concept_images/unnamed (6).webp`, `concept_images/unnamed (19).webp`, `concept_images/hero-image.fill.size_1248x702.v1756959082.webp`
- **Component Anatomy:**
  - **Hero Weather Card:** 48dp display typography with atmospheric radial glow matching external weather condition.
  - **Quick Action Carousel:** Horizontally scrolling pill/circular shortcuts (News, Calendar, Music, Traffic) with haptic tap feedback.
  - **Bento Grid Layout:** Mixed card dimensions (2×1 calendar reminders, 1×2 tall media player with blurred background album art, 1×1 quote of the day with subtle dot-matrix texture).

### 2.3 Slide 2: Reimagined Live Tiles Grid
- **Visual References:** `concept_images/Mur-Launcher-1.webp`, `concept_images/unnamed (20).webp`, `concept_images/lZk7gw8xVfQzAfzegIU2-rzBSH_gYE7N0ykce5lyF8uV9IdvFsKqSfB2euISzXIoqQ=-rw.webp`
- **Interaction Logic:**
  - **Active Live State:** Dynamic real-time content (unread counters, battery drain rate, mini stock charts) rendered directly on the tile face.
  - **Press-and-Hold Tile Hub:** Tile smoothly unfolds into a modal dashboard containing app notifications, deep shortcuts, and embedded mini-widgets.
  - **Physical Depress Motion:** On touch down, tile translates 2dp down with reduced shadow elevation before launching or unfolding.

### 2.4 Slide 3: Minimalist Vertical List
- **Visual References:** `concept_images/niagara-launcher-1.webp`, `concept_images/niagara-launcher-theming-update-hero.webp`, `concept_images/HYCCUsWiBry1GppOlnI0QGVAKITVdaN0tpNbsGvM-M1jsZdspp83GHLBSj7H29LYVc0=-rw.webp`
- **Ergonomics & Layout:**
  - **56dp Touch Row:** Generously spaced icon + label rows optimized for single-handed navigation.
  - **Wave Alphabet Scrubbing:** Curved letter index along the right thumb zone allowing instant letter jumping with granular haptic ticks.
  - **Swipe-Right Quick Actions:** Revealing app-specific shortcuts without opening secondary menus.

### 2.5 Control Center & Quick Settings Overlay
- **Visual References:** `concept_images/what-does-your-control-center-look-like-v0-mj2p0n7a1pee1.webp`, `concept_images/ControlCenter-820x600.jpg`, `concept_images/android-quick-settings-customize-mi-control-center-options.webp`
- **Architecture:**
  - **Overlay Execution:** `TYPE_APPLICATION_OVERLAY` full-screen glass sheet triggered by swiping down from top-right.
  - **One UI 9 + iOS Hybrid Aesthetic:** Rounded connectivity pills (WiFi, Bluetooth, Airplane, Cellular), continuous horizontal brightness & volume sliders with detent haptics, and a synced media playback card.

### 2.6 Icon Studio & Post-Processing Pipeline
- **Visual References:** `concept_images/theme-blooming-v0-gr0bylkt6cgh1.webp`, `concept_images/new-nova-version-v0-336g7eurygyf1.webp`
- **13 Visual Styles:**
  1. **Glass:** Translucent backdrop blur with top-left specular gradient.
  2. **Liquid Glass (Flagship):** Physical refraction shader, chromatic aberration, and spring jiggle on touch.
  3. **Embossed:** 3D directional lighting with inner shadow carving.
  4. **Material:** Flat clean solid tokens.
  5. **Neon:** High-intensity outer glow tied to dynamic accent hue.
  6. **Holographic:** Iridescent rainbow hue-shift via AGSL time-based uniform.
  7. **Duotone:** High-contrast two-color shadow/highlight filter.
  8. **Film Grain:** Matte analog noise overlay (`BlendMode.OVERLAY`).
  9. **Gradient:** Linear/radial multi-stop color ramps.
  10. **Liquid Metal:** Dynamic specular reflection mapping.
  11. **Retro Pixel:** 16×16 / 32×32 nearest-neighbor downsampling.
  12. **Wireframe:** Clean stroke edge extraction.
  13. **Stacked:** Sequential multi-pass rendering (e.g. Holographic + Film Grain).

---

## 3. Elevation & Material Spec Sheet

| Elevation Tier | Surface Token | Alpha / Fill | Visual Treatment | Use Case |
| :--- | :--- | :--- | :--- | :--- |
| **Tier 0 (Canvas)** | `bg.primary` | `#121212` (100%) | Deep obsidian base | Root background |
| **Tier 1 (Surface)** | `bg.surface` | `#1C1C1E` (100%) | 1px `#2C2C2E` stroke border | Passive feed cards, list items |
| **Tier 2 (Raised)** | `bg.surfaceRaised`| `#242426` (100%) | 4dp ambient shadow | Interactive bento tiles, buttons |
| **Tier 3 (Floating)** | `bg.glassFill` | `rgba(36,36,38,0.78)` | 32px backdrop blur + 1px specular top highlight | Dynamic Island, Control Center overlay, Search |
