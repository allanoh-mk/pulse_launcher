# Pulse Feed — Feature Specification

## Intent
Screen 1 is a highly customizable information feed combining schedules, weather, small device statistics, curated search results, user-selected providers, and existing feed integrations.

## Existing integration rule
The current Google/Discover-style feed integration is **in use by the Feed area**. It is KEEP/REFACTOR, never a blind deletion candidate. Audit its provider boundary before changing it.

## User capabilities
- Add, remove, resize, and place feed blocks.
- Choose square, rectangle, tall, and full-width layouts.
- Select Timeline, Bento, Magazine, Minimal, or Pulse Flow.
- Select providers and refresh behavior.
- Pin search results as persistent feed blocks.
- Control blur, curves, spacing, typography, and interaction.

## Provider contract
```kotlin
interface PulseFeedProvider {
    val id: String
    suspend fun load(request: FeedRequest): FeedResult
    fun supportsOffline(): Boolean
    fun refreshPolicy(): RefreshPolicy
}
```

## Implementation stages
1. Preserve and map the existing feed integration.
2. Create provider interfaces and a block registry.
3. Implement persisted layout state.
4. Add weather, calendar, connectivity statistics, and curated search blocks.
5. Add provider settings and offline cache.
6. Add ranking from Pulse Intelligence.

## Tests and performance
Provider failure renders a fallback card; offline mode never crashes; layout survives process death; disabled providers contribute zero background work. Weather and web results are cached and Wi-Fi speed sampling has a bounded window.