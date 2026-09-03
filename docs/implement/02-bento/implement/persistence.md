# Bento Persistence

```kotlin
data class BentoItem(
    val id: String,
    val providerId: String,
    val spanX: Int,
    val spanY: Int,
    val order: Int,
    val style: BentoStyle,
    val interaction: InteractionMap
)
```

Persist schema versions and migrations. App widgets need separate host/provider restoration handling.