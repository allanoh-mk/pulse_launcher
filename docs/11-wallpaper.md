# 11 — Wallpaper System

## Overview

Wallpapers are per-slide and deeply integrated with the theming system.
Changing the wallpaper auto-updates Material You colors,
glass icon tints, and feed card backgrounds.

## Wallpaper Types

| Type | Description | Performance |
|------|-------------|-------------|
| **Static** | Single image | None |
| **Gradient** | 2–4 color gradient (linear/radial) | None |
| **Animated (Lottie)** | Vector animation | Low |
| **Shader (AGSL)** | Fluid, aurora, plasma, starfield | Medium (30fps) |
| **Layered (Parallax)** | 2–3 images, gyroscope-driven | Low |
| **Music-reactive** | Gradient pulses to audio | Low |
| **Weather-reactive** | Color shifts with real weather | None |
| **Time-based** | Hue rotates across 24h | None |
| **Screenshot-derived** | Generated from user photo (palette extraction) | None |

## Per-Slide Assignment

- Slide 1 (Feed): e.g., "Aurora shader" (subtle, doesn't distract from content)
- Slide 2 (Tiles): e.g., "Gradient" (clean, lets tiles pop)
- Slide 3 (List): e.g., "Static" (minimal, text readability)

## Features

- **Parallax on slide swipe:** Each slide's wallpaper moves at 0.7× speed
- **Gyroscope parallax:** Tilt phone → layered wallpapers shift
- **Wallpaper → Color extraction:**
  - Dominant color → accent
  - Secondary color → secondary accent
  - Background tone → surface color
  - Auto-applied to Material You palette
- **Glass icon sync:** Glass icons use wallpaper colors for their tint
- **Focus mode override:** Each focus mode can force a specific wallpaper
- **Gallery:** Built-in collection of 20+ curated wallpapers (shaders + static)
- **Import:** Any image from gallery / screenshots
- **Randomize:** "Surprise me" button (picks from gallery + generates gradient)

## Shader Wallpapers (Built-in)

| Name | Look | Parameters |
|------|------|-----------|
| **Aurora** | Drifting green/purple bands | Speed, color palette, intensity |
| **Plasma** | Classic 80s plasma | Speed, color shift, scale |
| **Starfield** | Stars moving toward viewer | Speed, star count, color |
| **Fluid** | Liquid color blobs | Viscosity, color palette, turbulence |
| **Mesh Gradient** | Smooth color mesh (iOS 18 style) | Color stops, animation speed |
| **Noise** | Subtle animated grain | Intensity, speed |
| **Waves** | Gentle sine waves | Amplitude, frequency, color |
