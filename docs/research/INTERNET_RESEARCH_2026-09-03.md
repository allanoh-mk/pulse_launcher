# Internet Research Notes — 2026-09-03

## Android platform
Android 12+ foreground-service restrictions mean Pulse cannot assume an always-startable background overlay service. Android 14+ requires appropriate foreground service types, and specialUse declarations require a service-level explanation. System alert overlays require explicit user approval.

Sources:
- https://developer.android.com/about/versions/12/behavior-changes-12
- https://developer.android.com/develop/background-work/services/fgs/restrictions-bg-start
- https://developer.android.com/about/versions/14/changes/fgs-types-required
- https://developer.android.com/reference/android/Manifest.permission

## Launcher architecture
Lawnchair documentation describes a split between Lawnchair-specific customization and inherited Launcher3 code. It also identifies MINUS_ONE_PAGE/feed integrations as a dedicated launcher concept rather than ordinary home content.

Sources:
- https://github.com/LawnchairLauncher/lawnchair/wiki/Getting-started
- https://github.com/LawnchairLauncher/lawnchair

## Pulse implication
Keep inherited Launcher3 and Quickstep stable while Pulse-specific features live in explicit Pulse packages or adapters. Do not mass-convert the launcher to Compose in one change.

## MetroList research
MetroList is a GPL-3.0 Android YouTube Music client with playback, caching, lyrics, discovery, library, account, and Material 3 features. It is currently in maintenance mode. Pulse may study architecture and user flows, but direct code combination/copying requires license review.

Sources:
- https://github.com/MetrolistGroup/Metrolist

## Current research conclusions
1. The current Pulse three-screen host is real and wired, but several surfaces remain prototypes.
2. The existing Lawnchair FeedBridge should be adapted into a provider boundary instead of deleted blindly.
3. Dynamic Island must have a capability-first fallback because overlay and foreground-service behavior is platform constrained.
4. Android 12 Go success depends more on bounded work and memory than on creating a separate product personality.
5. MetroList should be treated as an architecture research source unless a licensing strategy is deliberately chosen.