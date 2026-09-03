# Pulse Implementation Follower

## Rules
- Mark DONE only with code path, tests, and manual evidence.
- Mark BLOCKED with reason and next action.
- Do not delete inherited launcher code before dependency evidence.
- Every new feature links to its docs/implement area.

## Phase 0 — Establish truth
- [ ] P0.1 Clean checkout with submodules
- [ ] P0.2 Record exact commit
- [ ] P0.3 Build debug variant
- [ ] P0.4 Run unit tests
- [ ] P0.5 Install on Android 12
- [ ] P0.6 Install on Android 12 Go test phone
- [ ] P0.7 Capture baseline recordings
GATE: baseline report exists.

## Phase 1 — Stabilize shell
- [ ] P1.1 Confirm PulseWorkspaceHost lifecycle
- [ ] P1.2 Remove accidental experimental long-press wallpaper action
- [ ] P1.3 Define overlay z-order contract
- [ ] P1.4 Add process-death restoration
- [ ] P1.5 Accessibility pass
GATE: shell survives recreation.

## Phase 2 — Onboarding
- [ ] P2.1 Persisted state
- [ ] P2.2 Welcome media loader
- [ ] P2.3 Default launcher guidance
- [ ] P2.4 Notification education
- [ ] P2.5 Contacts/files opt-in
- [ ] P2.6 Battery reliability guidance
- [ ] P2.7 Finish/profile selection
GATE: every denial path continues.

## Phase 3 — Three screens
- [ ] P3.1 Feed provider boundary
- [ ] P3.2 Feed layout persistence
- [ ] P3.3 Replace mock screen-time
- [ ] P3.4 Bento editor and undo
- [ ] P3.5 AppWidgetHost bridge
- [ ] P3.6 List top card
- [ ] P3.7 Left/right alphabet option
GATE: all screens work after cold start.

## Phase 4 — Search
- [ ] P4.1 Local apps/shortcuts
- [ ] P4.2 Calculator
- [ ] P4.3 Settings destinations
- [ ] P4.4 Contacts opt-in
- [ ] P4.5 Files opt-in
- [ ] P4.6 Currency provider
- [ ] P4.7 Internet providers
- [ ] P4.8 Ranking/deduplication
GATE: cancellation and offline behavior verified.

## Phase 5 — Notifications and intelligence
- [ ] P5.1 Badge normalization
- [ ] P5.2 Reply/action card
- [ ] P5.3 Local event model
- [ ] P5.4 Smart app ranking
- [ ] P5.5 Smart widgets/feed ranking
- [ ] P5.6 Explanation/privacy controls
GATE: blocked sources produce no events.

## Phase 6 — System surfaces
- [ ] P6.1 Capability registry
- [ ] P6.2 Control Center defaults
- [ ] P6.3 Dynamic Island adapters
- [ ] P6.4 Overlay fallbacks
GATE: unsupported controls are honest.

## Phase 7 — Music and wallpaper
- [ ] P7.1 Playback abstraction
- [ ] P7.2 MediaSession hardening
- [ ] P7.3 Pulse Music integration
- [ ] P7.4 MetroList architecture/license decision
- [ ] P7.5 Wallpaper motion prototype
- [ ] P7.6 Cached motion maps
GATE: Android 12 Go profiling passes.

## Phase 8 — Settings and brand
- [ ] P8.1 Module registry
- [ ] P8.2 Theme DNA
- [ ] P8.3 Import/export
- [ ] P8.4 Icon assets
GATE: reset/migration tests pass.

## Phase 9 — Cleanup and release hardening
- [ ] P9.1 compatLib deletion experiment
- [ ] P9.2 manifest permission audit
- [ ] P9.3 dead asset scan
- [ ] P9.4 baseline profiles
- [ ] P9.5 Android 12 Go endurance test
GATE: release checklist complete.