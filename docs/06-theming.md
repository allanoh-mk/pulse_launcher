# 06 — Theming & Material You

## Overview

One theme system that controls colors, fonts, and visual style
across the entire launcher + island + control center.

## Color System

### Sources (priority order)
1. **Manual** — user picks accent + background colors
2. **Material You** — derived from wallpaper (Android 12+ `DynamicColor`)
3. **Auto (time-based)** — warm tones in morning, cool in evening
4. **Focus mode** — each mode has its own palette

### Color Tokens

```
accent_primary      ← main accent (buttons, highlights, active states)
accent_secondary    ← secondary accent (subtle highlights)
background_primary  ← main background (slides, panels)
background_secondary← card/panel background
surface             ← elevated surfaces (island, control center)
on_surface          ← text on surfaces
on_background       ← text on backgrounds
outline             ← borders, dividers
```

### Accent Color (Nova-style)
- Single accent color that tints:
  - Active toggle states
  - Selection highlights
  - Progress bars
  - Island active states
  - Search bar focus
- Auto-derived from wallpaper OR manual pick
- "Color accent changing": accent shifts subtly based on time of day
  (warm → cool over 24h cycle)

## Dark / Light Mode

- Follow system
- Auto (sunset → dark, sunrise → light)
- Manual
- Per-slide override (optional)
- "Dim" mode (reduced brightness for night, not full dark)

## Font System (see 07-fonts.md)

## Style Presets

Pre-built combinations:

| Preset | Icon Style | Wallpaper | Font | Accent | Haptics |
|--------|-----------|-----------|------|--------|---------|
| **Clean** | Material | Solid color | Grotesk | Blue | Subtle |
| **Glass** | Glass | Gradient | Geometric | Auto | Balanced |
| **Neon** | Neon | Dark solid | Monospace | Cyan | Expressive |
| **Retro** | Retro Pixel | Solid | Pixel | Amber | Subtle |
| **Editorial** | Duotone | Gradient | Serif | Warm | Balanced |
| **Cyber** | Holographic | Shader | Condensed | Magenta | Expressive |
| **Minimal** | Material (small) | Solid | Grotesk (light) | Gray | Subtle |

User can save custom presets.

## Wallpaper Integration

- Wallpaper change → auto-update Material You colors
- Glass icons pick up wallpaper colors
- Feed cards use wallpaper-derived background tint
- Island background matches wallpaper (blurred)
