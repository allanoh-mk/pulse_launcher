# 02 — Icon Studio

## Overview

A settings module that controls every visual aspect of app icons.
Inspired by Samsung Galaxy Theme Park (One UI 9) but more powerful.
Changes apply **instantly** across all 3 slides, the island,
and the control center.

## Access

Settings → Icon Studio

## Options

### Shape
- Circle
- Squircle (iOS-style superellipse)
- Rounded Square
- Teardrop
- Hexagon
- None (raw icon)

### Style (Post-Processing Effect)

| Style | Visual | Key Parameters |
|-------|--------|---------------|
| **Glass** | Translucent, reflective, backdrop blur | Transparency, edge thickness, reflection angle, color, blur radius |
| **Embossed** | Raised, 3D, directional light | Depth, light angle, bevel width, highlight intensity |
| **Material** | Flat, solid, no effect | (none — baseline) |
| **Neon** | Dark fill + glowing outline | Glow radius, glow color, core color |
| **Holographic** | Iridescent, hue-shifting | Speed, saturation, base hue |
| **Duotone** | Two-tone grayscale | Shadow color, highlight color |
| **Film Grain** | Matte, analog noise | Grain intensity, grain size |
| **Gradient** | Color gradient background | Type (linear/radial/sweep), color 1, color 2, angle |
| **Liquid Metal** | Chrome, moving highlight | Environment map, specular intensity |
| **Retro Pixel** | 16×16 / 32×32 pixelated | Resolution (16/32/64) |
| **Wireframe** | Outline only, no fill | Line width, line color |
| **Glitch** | RGB offset + slice displacement | Offset amount, slice count, color |
| **Enamel** | Glossy pin-badge | Border color, highlight opacity |
| **Stained Glass** | Colored regions + dark leading | Region count, leading width, palette |
| **Vaporwave** | Pink/purple + grid + sun | Grid density, sun size, color shift |

**Stacking:** User can apply 2 styles (e.g., "Holographic + Film Grain").
Pipeline applies them in sequence.

### Size
- Slider: 60% – 120% of grid cell
- Affects all slides uniformly

### Label
- Show / Hide
- Font family (from Font Studio)
- Size (relative to icon)
- Color (auto from Material You or manual)
- Max lines (1 or 2)

### Icon Pack
- Browse installed ADW-standard packs
- Apply globally
- **Per-app override:** tap any app → pick specific icon from pack
- Fallback: adaptive icon → system icon

### Background
- Solid color
- Wallpaper-derived (extracts dominant color)
- Gradient (2-color)
- Transparent (no background, just foreground)

### Dark Mode
- Auto (light icons on dark bg, dark icons on light bg)
- Force Light
- Force Dark
- Monochrome in dark mode (optional)

### Color Tint
- Accent color picker (applies to monochrome/duotone styles)
- Auto from Material You (wallpaper-derived)
- Manual hex picker

### Preview
- Full grid of ALL installed apps
- Updates in real-time as you tweak any setting
- Toggle: show on light bg / dark bg / wallpaper bg

### Export / Import
- Save current config as `.pulse-icon` file (JSON + per-app overrides)
- Import from file or share intent
- "Reset to defaults" button

## Technical Pipeline

```
Base Icon (adaptive / pack / system)
    ↓
[Shape Mask]         ← ClipPath / ShapeDrawable
    ↓
[Style Effect 1]     ← Glass / Emboss / Neon / etc.
    ↓
[Style Effect 2]     ← Optional second layer
    ↓
[Background]         ← Solid / Gradient / Transparent
    ↓
[Label]              ← Text rendering
    ↓
Final Rendered Icon
```

The pipeline is a single `IconRenderer` class.
Every surface (slides, island, control center, search results)
calls `IconRenderer.render(packageName, config)` to get the final drawable.

## Key Libraries

| Library | Purpose |
|---------|---------|
| Haze (chrisbanes) | Backdrop blur (Glass style) |
| Prismal | Full liquid glass via GLSL (Tier 2 Glass) |
| AGSL Shaders | Holographic, Liquid Metal, Glitch |
| RenderEffect (framework) | Blur, shadow, hue rotation |
| ColorMatrixColorFilter | Duotone, grayscale |
