package app.lawnchair.pulse.island

/**
 * Pure resolution logic for docs/03-dynamic-island.md's state table:
 *
 * | State    | Trigger                        |
 * |----------|---------------------------------|
 * | Hidden   | No active activities            |
 * | Compact  | Exactly 1 active activity        |
 * | Minimal  | 2+ concurrent activities         |
 * | Expanded | User requested expansion         |
 *
 * Kept free of Android/Compose types so the transition table is fully
 * unit-testable; [app.lawnchair.pulse.island.IslandService] is the only
 * caller and owns all the actual overlay/window/animation side effects.
 */
object IslandStateMachine {

    fun resolve(activities: List<IslandActivity>, userRequestedExpand: Boolean): IslandVisualState {
        if (activities.isEmpty()) return IslandVisualState.Hidden

        if (userRequestedExpand) {
            val primary = pickPrimary(activities)
            return IslandVisualState.Expanded(primary, activities.filterNot { it.id == primary.id })
        }

        return when (activities.size) {
            1 -> IslandVisualState.Compact(activities.first())
            else -> {
                val primary = pickPrimary(activities)
                val secondary = activities.firstOrNull { it.id != primary.id } ?: primary
                IslandVisualState.Minimal(primary, secondary)
            }
        }
    }

    /**
     * Priority order when multiple activities compete for the primary (left)
     * slot: an active call always wins, then navigation, then media, then
     * everything else in the order they were reported.
     */
    private fun pickPrimary(activities: List<IslandActivity>): IslandActivity {
        val priority = listOf(
            IslandActivityType.CALL,
            IslandActivityType.NAVIGATION,
            IslandActivityType.PRIVACY,
            IslandActivityType.MEDIA,
            IslandActivityType.TIMER,
            IslandActivityType.CHARGING,
            IslandActivityType.ASSISTANT,
        )
        return activities.minByOrNull { activity ->
            priority.indexOf(activity.type).let { if (it == -1) Int.MAX_VALUE else it }
        }
            ?: activities.first()
    }
}
