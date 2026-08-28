# 08 — Animation System

## Overview

A unified animation system with a global "personality" setting
that affects all transitions, interactions, and micro-animations.

## Global Animation Personality

| Setting | Duration Multiplier | Spring Stiffness | Spring Damping | Overshoot |
|---------|--------------------|--------------------|----------------|-----------|
| **Subtle** | 0.7× | 400 | 35 | Minimal |
| **Balanced** (default) | 1.0× | 300 | 28 | Slight |
| **Expressive** | 1.3× | 200 | 20 | Noticeable |

One slider in settings. Affects everything.

## Animation Catalog

### Transitions (Between Screens/States)

| Name | Description | Use Case |
|------|-------------|----------|
| **Slide + Parallax** | Horizontal slide, bg at 0.7× | Slide navigation |
| **Morph** | Shared element transforms (icon → full screen) | App launch, tile → dashboard |
| **Flip** | 3D Y-axis rotation | Slide alternative |
| **Zoom** | Scale out/in from center | App open/close |
| **Ink** | AGSL shader: dark spread reveals next | Slide alternative (dramatic) |
| **Crossfade** | Simple opacity | Subtle state changes |
| **Page Curl** | 3D rotation + shadow | Card dismiss |

### Micro-Animations

| Name | Description | Trigger |
|------|-------------|---------|
| **Spring Pop** | Scale 0.9 → 1.05 → 1.0 | Tap feedback |
| **Ripple** | Circle expands from touch point | Any touch |
| **Stagger** | Items animate in sequence (30ms delay) | List/feed appearance |
| **Pulse** | Scale 1.0 → 1.02 → 1.0 (loop) | Active state (music, charging) |
| **Shake** | Horizontal oscillation | Error / invalid input |
| **Bounce In** | Scale 0 → 1.1 → 1.0 | New notification |
| **Slide Up** | translateY(20dp → 0) + fade | Sheet appearance |
| **Count Up** | Number increments rapidly to final value | Stats, battery % |
| **Wave** | Elements wave in sequence (sine) | Island music state |

### Shader Animations (AGSL)

| Name | Description | Use Case |
|------|-------------|----------|
| **Liquid** | Fluid/blob transition | Slide transition (Expressive mode) |
| **Aurora** | Drifting color bands | Wallpaper / island bg |
| **Smoke** | Dissipating particles | Dismiss / close |
| **Ink Spread** | Dark blob expands | Reveal / transition |
| **Holographic Shift** | Hue rotation + Fresnel | Holographic icon style |

### Haptic Patterns (Paired with Animations)

| Action | Haptic |
|--------|--------|
| Slide change | Short tick (10ms) |
| Long-press | Medium thud (30ms) |
| Island expand | Double tap (2×15ms, 50ms gap) |
| Island dismiss | Single soft (8ms) |
| Toggle on | Sharp click (5ms) |
| Toggle off | Softer click (8ms) |
| Success | Triple short (3×5ms, 30ms gap) |
| Error | Vibration pattern (50ms on, 50ms off, 50ms on) |
| Notification | Soft buzz (20ms) |

## Configuration

- Global personality slider (Subtle / Balanced / Expressive)
- Per-transition override (e.g., "Slide nav = Ink, App open = Morph")
- Reduce motion toggle (accessibility: replaces all with crossfade)
- Haptic intensity slider (0–100%)
- Disable haptics toggle
