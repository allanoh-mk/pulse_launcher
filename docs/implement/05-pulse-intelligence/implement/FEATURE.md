# Pulse Intelligence — Local Behavioral Graph

## Product rule
Pulse should feel personal without requiring a heavy always-running AI model.

## Event model
```text
event = { type, subject, timestamp, context, outcome }
```

Examples: app launch, completed search, music playback, dismissed recommendation.

## Outputs
Smart dock ranking, Feed ordering, search ranking, and contextual suggestions.

## Privacy
Every data source is individually controllable. Blocked apps and sources are excluded from collection and recommendation scoring, not merely hidden.

## Explainability
Every recommendation supports **Why this?** and **Don't recommend this.**

## Implementation
Event schema → local store → time-decayed scoring → context rules → explanations → battery/memory profiling.

A future on-device ML model is optional, not a prerequisite.