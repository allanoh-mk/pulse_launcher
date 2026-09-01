package app.lawnchair.pulse.island

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IslandStateMachineTest {

    private fun activity(id: String, type: IslandActivityType) =
        IslandActivity(id = id, type = type, title = id)

    @Test
    fun `no activities resolves to Hidden`() {
        val state = IslandStateMachine.resolve(emptyList(), userRequestedExpand = false)
        assertEquals(IslandVisualState.Hidden, state)
    }

    @Test
    fun `single activity resolves to Compact`() {
        val media = activity("media", IslandActivityType.MEDIA)
        val state = IslandStateMachine.resolve(listOf(media), userRequestedExpand = false)
        assertTrue(state is IslandVisualState.Compact)
        assertEquals(media, (state as IslandVisualState.Compact).activity)
    }

    @Test
    fun `two activities resolve to Minimal`() {
        val media = activity("media", IslandActivityType.MEDIA)
        val timer = activity("timer", IslandActivityType.TIMER)
        val state = IslandStateMachine.resolve(listOf(media, timer), userRequestedExpand = false)
        assertTrue(state is IslandVisualState.Minimal)
    }

    @Test
    fun `call always wins the primary slot over media`() {
        val media = activity("media", IslandActivityType.MEDIA)
        val call = activity("call", IslandActivityType.CALL)
        val state =
            IslandStateMachine.resolve(listOf(media, call), userRequestedExpand = false) as IslandVisualState.Minimal
        assertEquals(call, state.primary)
        assertEquals(media, state.secondary)
    }

    @Test
    fun `user requested expand always wins regardless of activity count`() {
        val media = activity("media", IslandActivityType.MEDIA)
        val state = IslandStateMachine.resolve(listOf(media), userRequestedExpand = true)
        assertTrue(state is IslandVisualState.Expanded)
        assertEquals(media, (state as IslandVisualState.Expanded).primary)
        assertTrue(state.others.isEmpty())
    }

    @Test
    fun `expanded with three activities puts non-primary ones in others`() {
        val call = activity("call", IslandActivityType.CALL)
        val media = activity("media", IslandActivityType.MEDIA)
        val timer = activity("timer", IslandActivityType.TIMER)
        val state = IslandStateMachine.resolve(
            listOf(media, timer, call),
            userRequestedExpand = true,
        ) as IslandVisualState.Expanded
        assertEquals(call, state.primary)
        assertEquals(2, state.others.size)
        assertTrue(state.others.containsAll(listOf(media, timer)))
    }
}
