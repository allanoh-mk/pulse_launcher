# Pulse Launcher

A personal Android launcher that fuses the best elements of Mur Launcher,
Niagara Launcher, and Nova Launcher into a single cohesive system,
with an iOS-level Dynamic Island and a built-in digital assistant.

## Philosophy

- **Personal, not public.** Built for one user. No multi-user, no cloud sync,
  no ads. Optimize for *my* workflow and aesthetic.
- **Cohesive, not patched.** Every component (icons, widgets, feed, island,
  control center) shares one style pipeline. Change one setting → everything
  updates.
- **Three slides, three moods.** Feed / Tiles / List. Each has its own
  personality but they share the same visual DNA.
- **The island is the hero.** It's not a notification pill — it's a live
  activity surface with a digital assistant baked in.

## Core References

| Launcher | What we borrow |
|----------|---------------|
| **Mur Launcher** | Tile grid, tile dashboards, universal search with inline answers, app overlay |
| **Niagara Launcher** | Vertical list layout, simplicity, themes, Material You, icon system |
| **Nova Launcher** | Feed, accent color system, Micro Results search, style flexibility |
| **iOS / One UI 9** | Dynamic Island behavior, Control Center, haptic language, animation feel |

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (launcher + island)
- **Base:** Lawnchair fork (AOSP Launcher3 derivative)
- **Min SDK:** 31 (Android 12)
- **Shaders:** AGSL (Android Shading Language) for glass/holographic/liquid effects
- **Blur:** Haze (chrisbanes) for backdrop blur
- **Advanced glass:** Prismal (GLSL) — optional Tier 2
- **Animations:** Compose `spring()`, `Animatable`, AGSL time-based shaders
- **Haptics:** `VibrationEffect.Composition` (Android 12+)
- **Assistant:** Gemini API + local Ollama fallback
- **Island:** `TYPE_APPLICATION_OVERLAY` foreground service
- **Build:** Gradle + Kotlin DSL

## Repository Structure

```
PulseLauncher/
├── docs/                    ← You are here
├── app/                     ← Main launcher module (Lawnchair fork)
│   ├── src/main/java/...
│   │   ├── workspace/       ← 3-slide system
│   │   ├── feed/            ← Slide 1
│   │   ├── tiles/           ← Slide 2
│   │   ├── list/            ← Slide 3
│   │   ├── search/          ← Unified search
│   │   ├── iconstudio/      ← Icon Studio settings
│   │   ├── theme/           ← Style pipeline, Material You
│   │   ├── controlcenter/   ← Custom overlay panel
│   │   ├── gestures/        ← Gesture system
│   │   └── haptics/         ← Haptic patterns
│   └── src/main/res/...
├── island/                  ← Dynamic Island module
│   ├── src/main/java/...
│   │   ├── service/         ← Overlay foreground service
│   │   ├── states/          ← Compact / Minimal / Expanded
│   │   ├── activities/      ← Music, Timer, Call, Nav, Charge, Live
│   │   ├── assistant/       ← LLM + voice + context
│   │   └── animations/      ← Spring transitions
│   └── src/main/res/...
├── shared/                  ← Common models, DB, theme tokens
│   └── src/main/java/...
│       ├── models/
│       ├── db/
│       ├── theme/
│       └── utils/
└── build.gradle.kts
```

## Status

**Phase:** Planning
**Target:** Personal use, phone-only (tablet/foldable later)
