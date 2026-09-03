# Performance and Android 12 Go

## Non-negotiable
Pulse is one app for all supported devices. Android 12 Go is not a reduced product edition.

## Internal adaptation
Pulse may adapt task scheduling, cache limits, animation work, refresh cadence, image resolution, and background work while keeping the user's configured features available.

## Battery modes
Full, Balanced, Battery Saver, Extreme.

Battery Saver changes scheduling and rendering budgets; it does not silently delete launcher configuration.

## Device matrix
- Tecno Pop 7 BF6 / Android 12 Go: required real-device validation.
- Android 12 non-Go.
- Android 13+.
- low-RAM and high-RAM profiles.

## Metrics
Cold/warm start, frame pacing, memory, GC pressure, battery drain, provider latency, search latency, and process-death recovery.

## Baseline profile
Keep baseline-profile infrastructure under review and benchmark first home render, Feed open, Bento scroll, Search, and Pulse Music entry.