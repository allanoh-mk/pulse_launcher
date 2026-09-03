package app.lawnchair.pulse.workspace

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BentoFlipCardTest {

    @Test
    fun `initial state shows front content at zero rotation`() {
        val state = BentoFlipCardState()
        assertFalse(state.isFlipped)
        assertEquals(0f, state.rotation, 0.001f)
        assertFalse(state.isShowingBackContent)
    }

    @Test
    fun `toggle alternates between front and back complication views`() {
        val state = BentoFlipCardState()
        state.toggle()
        assertTrue(state.isFlipped)
        assertEquals(180f, state.rotation, 0.001f)
        assertTrue(state.isShowingBackContent)

        state.toggle()
        assertFalse(state.isFlipped)
        assertEquals(0f, state.rotation, 0.001f)
        assertFalse(state.isShowingBackContent)
    }

    @Test
    fun `flip to back and front are idempotent`() {
        val state = BentoFlipCardState()
        state.flipToBack()
        assertTrue(state.isFlipped)
        state.flipToBack()
        assertTrue(state.isFlipped)

        state.flipToFront()
        assertFalse(state.isFlipped)
        state.flipToFront()
        assertFalse(state.isFlipped)
    }
}
