# 07 — Font System

## Overview

A font configuration system in settings that controls typography
across the entire launcher.

## Type Scale

| Level | Use | Default Size |
|-------|-----|-------------|
| Display | Big numbers (clock, battery %, timer) | 48sp |
| Headline | Card titles, section headers | 24sp |
| Title | App names, feed card titles | 16sp |
| Body | Feed content, descriptions | 14sp |
| Caption | Timestamps, subtitles, labels | 12sp |
| Micro | Badge counts, tiny labels | 10sp |

Each level is independently configurable:
- Font family
- Weight (variable font: 100–900)
- Size (override)
- Letter spacing
- Line height
- Italic (on/off)

## Available Fonts (Built-in)

| Category | Fonts |
|----------|-------|
| Grotesk | Inter, SF Pro (system), Roboto Flex |
| Geometric | Poppins, Montserrat, Outfit |
| Monospace | JetBrains Mono, Fira Code, Space Mono |
| Serif | Playfair Display, Lora, Source Serif |
| Rounded | Nunito, Quicksand, Comfortaa |
| Condensed | Roboto Condensed, Oswald, Barlow Condensed |
| Display | Clash Display, General Sans, Syne |
| Handwritten | Caveat, Pacifico, Dancing Script |
| Pixel | VT323, Press Start 2P, Silkscreen |
| Variable | Inter Variable, Roboto Flex (all weights in one file) |

## Font Pairing Presets

| Preset | Display | Body | Mono |
|--------|---------|------|------|
| **Modern** | Inter (Bold) | Inter (Regular) | JetBrains Mono |
| **Editorial** | Playfair Display | Lora | Fira Code |
| **Tech** | Space Grotesk | Inter | Space Mono |
| **Playful** | Quicksand | Nunito | VT323 |
| **Minimal** | Roboto Flex (Light) | Roboto Flex (Regular) | Roboto Mono |
| **Bold** | Syne | Outfit | JetBrains Mono |

## Custom Fonts

- User can install `.ttf` / `.otf` / `.woff2` files
- Stored in app's private directory
- Appears in font picker
- Variable fonts: user adjusts weight via slider

## Implementation

- Use `FontFamily` in Compose with `Font` resources
- Variable fonts: `Font` with `fontWeight` parameter
- Store selection in `ThemeConfig` (part of main theme system)
- Preview: show "Aa" sample at each size level when picking
