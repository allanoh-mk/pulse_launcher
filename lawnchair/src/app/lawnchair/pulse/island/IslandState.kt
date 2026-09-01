package app.lawnchair.pulse.island

/** One background/live activity the Island can represent. */
data class IslandActivity(
    val id: String,
    val type: IslandActivityType,
    val title: String,
    val subtitle: String? = null,
    val iconPackageName: String? = null,
    val progress: Float? = null,
)

enum class IslandActivityType {
    MEDIA,
    TIMER,
    CALL,
    CHARGING,
    NAVIGATION,
    PRIVACY,
    ASSISTANT,
}

/**
 * The four visual states from docs/03-dynamic-island.md, resolved purely from
 * the list of currently-active [IslandActivity] plus whether the user asked to
 * expand (long-press). See [IslandStateMachine] for the resolution rules.
 */
sealed class IslandVisualState {
    data object Hidden : IslandVisualState()
    data class Compact(val activity: IslandActivity) : IslandVisualState()
    data class Minimal(val primary: IslandActivity, val secondary: IslandActivity) : IslandVisualState()
    data class Expanded(val primary: IslandActivity, val others: List<IslandActivity>) : IslandVisualState()
}
