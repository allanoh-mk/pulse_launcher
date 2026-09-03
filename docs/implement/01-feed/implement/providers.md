# Feed Providers

## Contract
```kotlin
interface PulseFeedProvider {
    val id: String
    suspend fun load(request: FeedRequest): FeedResult
    fun supportsOffline(): Boolean
    fun refreshPolicy(): RefreshPolicy
}
```

## Rules
The Feed scheduler owns refresh timing. Providers may not create uncontrolled polling loops. Existing Google/Discover-style integration must first be mapped behind this boundary before refactoring.