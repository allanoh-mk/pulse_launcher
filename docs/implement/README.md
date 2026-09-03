# Pulse Implementation Workspace

This directory is the execution source of truth for Pulse-specific work.

## Product rules
- Minimum Android version: Android 12 / API 31.
- Android 12 Go is a first-class target, including low-memory real-device validation.
- Pulse is one product: no crippled Go edition.
- The launcher has exactly three primary home screens: Feed, Bento, and List.
- Existing Google/Discover-style feed integration is active in the Feed area and must be KEEP/REFACTOR, never blindly deleted.

## Feature folders
Each feature lives in its own `implement` folder and documents product intent, UX rules, architecture, stages, permissions, failure modes, tests, performance budget, motion specification, and references.
